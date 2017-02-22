#!/bin/bash

MYSQL_PACKAGE=mysql-5.7.17-linux-glibc2.5-i686
MYSQL_GROUPNAME=mysql
MYSQL_USERNAME=zxqiu
SYSTEM_USERNAME=zxqiu
CODE_PATH=$(sudo pwd);

echo $CODE_PATH;

sudo yum install libaio;

sudo groupadd $MYSQL_GROUPNAME
sudo useradd -r -g $MYSQL_GROUPNAME -s /bin/false mysql
sudo usermod -a -G $MYSQL_GROUPNAME $MYSQL_USERNAME
cd /usr/local
sudo tar zxvf $CODE_PATH/$MYSQL_PACKAGE.tar.gz
sudo ln -s $MYSQL_PACKAGE /usr/local/mysql
cd mysql
sudo mkdir mysql-files
sudo chmod 750 mysql-files
sudo chown -R $MYSQL_USERNAME .
sudo chgrp -R $MYSQL_GROUPNAME .
#scripts/mysql_install_db --user=mysql# MySQL 5.7.0 to 5.7.4
#bin/mysql_install_db --user=mysql    # MySQL 5.7.5
sudo bin/mysqld --initialize --user=$MYSQL_USERNAME # MySQL 5.7.6 and up
#sudo bin/mysql_ssl_rsa_setup              # MySQL 5.7.6 and up
sudo chown -R $SYSTEM_USERNAME .
sudo chown -R $MYSQL_USERNAME data
sudo chown -R $MYSQL_USERNAME mysql-files
sudo bin/mysqld_safe --user=$MYSQL_USERNAME &
# Next command is optional
sudo cp support-files/mysql.server /etc/init.d/mysql.server
