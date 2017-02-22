#!/bin/bash

MYSQL_PACKAGE=$(pwd)/mysql-5.7.17-linux-glibc2.5-i686.tar.gz
MYSQL_VERSION=${MYSQL_PACKAGE%.tar*}
MYSQL_VERSION=${MYSQL_VERSION##*/}
echo $MYSQL_VERSION
exit

MYSQL_GROUPNAME=mysql
MYSQL_USERNAME=zxqiu
SYSTEM_USERNAME=zxqiu
CODE_PATH=$(pwd)
MYSQL_PATH=/bin/local

read -r -p "${1:-Make sure you have setup the script correctly. Make sure you have mysql server 5.7.6 or higher. [y/n]} " response
case "$response" in
    [yY][eE][sS]|[yY])
        ;;
    *)
        exit;
        ;;
esac

sudo yum install libaio;

sudo groupadd $MYSQL_GROUPNAME
sudo useradd -r -g $MYSQL_GROUPNAME -s /bin/false mysql
sudo usermod -a -G $MYSQL_GROUPNAME $MYSQL_USERNAME
cd MYSQL_PATH
sudo tar zxvf $CODE_PATH/$MYSQL_PACKAGE
sudo ln -s $MYSQL_VERSION MYSQL_PATH/mysql
cd mysql
sudo mkdir mysql-files
sudo chmod 750 mysql-files
sudo chown -R $MYSQL_USERNAME .
sudo chgrp -R $MYSQL_GROUPNAME .
#scripts/mysql_install_db --user=mysql# MySQL 5.7.0 to 5.7.4
#bin/mysql_install_db --user=mysql    # MySQL 5.7.5
sudo bin/mysqld --initialize-insecure --user=$MYSQL_USERNAME # MySQL 5.7.6 and up
#sudo bin/mysql_ssl_rsa_setup              # MySQL 5.7.6 and up
sudo chown -R $SYSTEM_USERNAME .
sudo chown -R $MYSQL_USERNAME data
sudo chown -R $MYSQL_USERNAME mysql-files
#bin/mysqld_safe --user=$MYSQL_USERNAME &


# setup configurations
echo "[mysqld]" > my.cnf
echo "datadir="$MYSQL_PATH"/mysql/data" >> my.cnf
echo "socket=/tmp/mysql.sock" >> my.cnf
echo "symbolic-links=0" >> my.cnf
echo "[mysqld_safe]" >> my.cnf
sudo mv my.cnf /etc/my.cnf

echo "export PATH="$(pwd)"/bin/:$PATH" > mysql.sh
sudo mv mysql.sh /etc/profile.d/

# start mysql server and init root user
killall mysqld
support-files/mysql.server start
bin/mysqladmin -u root password root
sudo cp support-files/mysql.server /etc/init.d/mysql.server

# create wallet database for project
bin/mysql -u root --password=root --execute="create database wallet"
