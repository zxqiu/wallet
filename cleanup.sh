#!/bin/bash

MYSQL_PACKAGE=mysql-5.7.17-linux-glibc2.5-i686
MYSQL_GROUPNAME=mysql
MYSQL_USERNAME=zxqiu
SYSTEM_USERNAME=zxqiu
CODE_PATH=$(pwd);


confirm() {
    # call with a prompt string or use a default
    read -r -p "${1:-This opreation will delete all the data in MySql Database. Are you sure? [yes/no]} " response
    case "$response" in
        [yY][eE][sS]) 
            clean
            ;;
        *)
            echo "Smart choice! You just successfully avoid being fired."
            exit;
            ;;
    esac
}

clean() {
    echo "Crashing everything in your life ..."
    killall mysqld
    sudo rm -rf /usr/local/mysql*
    sudo rm -rf /etc/init.d/mysql.server
    sudo rm -rf /etc/profile.d/mysql.sh
    sudo rm -rf /etc/my.cnf
    echo "Done."
}



confirm

