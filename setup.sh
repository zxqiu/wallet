#!/bin/bash

MYSQL_PACKAGE=$(pwd)/mysql-5.7.17-linux-glibc2.5-i686.tar.gz
MYSQL_VERSION=${MYSQL_PACKAGE%.tar*}
MYSQL_VERSION=${MYSQL_VERSION##*/}

MYSQL_INSTALL_GROUPNAME=mysql
MYSQL_INSTALL_USERNAME=zxqiu
SYSTEM_USERNAME=zxqiu
CODE_PATH=$(pwd)
MYSQL_PATH=/usr/local

DATABASE_USER_NAME=root
DATABASE_USER_PASSWD=root

read -r -p "${1:-Make sure you have setup the script correctly. Make sure you have mysql server 5.7.6 or higher. [y/n]} " response
case "$response" in
    [yY][eE][sS]|[yY])
        ;;
    *)
        exit;
        ;;
esac

sudo yum install libaio;

sudo groupadd $MYSQL_INSTALL_GROUPNAME
#sudo useradd -r -g $MYSQL_INSTALL_GROUPNAME -s /bin/false mysql
sudo usermod -a -G $MYSQL_INSTALL_GROUPNAME $MYSQL_INSTALL_USERNAME
cd $MYSQL_PATH
sudo tar zxvf $MYSQL_PACKAGE
sudo ln -s $MYSQL_VERSION $MYSQL_PATH/mysql
cd mysql
sudo mkdir mysql-files
sudo chmod 750 mysql-files
sudo chown -R $MYSQL_INSTALL_USERNAME .
sudo chgrp -R $MYSQL_INSTALL_GROUPNAME .
#scripts/mysql_install_db --user=mysql# MySQL 5.7.0 to 5.7.4
#bin/mysql_install_db --user=mysql    # MySQL 5.7.5
sudo bin/mysqld --initialize-insecure --user=$MYSQL_INSTALL_USERNAME # MySQL 5.7.6 and up
#sudo bin/mysql_ssl_rsa_setup              # MySQL 5.7.6 and up
sudo chown -R $SYSTEM_USERNAME .
sudo chown -R $MYSQL_INSTALL_USERNAME data
sudo chown -R $MYSQL_INSTALL_USERNAME mysql-files
#bin/mysqld_safe --user=$MYSQL_INSTALL_USERNAME &


# setup configurations
echo "[mysqld]" > my.cnf
echo "datadir="$MYSQL_PATH"/mysql/data" >> my.cnf
echo "socket=/tmp/mysql.sock" >> my.cnf
echo "symbolic-links=0" >> my.cnf
echo "character-set-server=utf8" >> my.cnf
echo "collation-server=utf8_general_ci" >> my.cnf
echo "[mysqld_safe]" >> my.cnf
sudo mv my.cnf /etc/my.cnf

echo "export PATH="$(pwd)"/bin/:$PATH" > mysql.sh
sudo mv mysql.sh /etc/profile.d/
source /etc/profile.d/mysql.sh

# start mysql server and init root user
killall mysqld
support-files/mysql.server start
bin/mysqladmin -u $DATABASE_USER_NAME password $DATABASE_USER_PASSWD
sudo cp support-files/mysql.server /etc/init.d/mysql.server

# create wallet database for project
bin/mysql -u $DATABASE_USER_NAME --password=$DATABASE_USER_PASSWD --execute="create database wallet"
