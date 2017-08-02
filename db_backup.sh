#!/bin/bash

MYSQL_USER_NAME=
MYSQL_PASSWD=
DAILY_BACKUP_MAX=5
MONTHLY_BACKUP_MAX=3
BACKUP_DIR=
BACKUP_LOG=

mysqldump -u $MYSQL_USER_NAME -p"$MYSQL_PASSWD" wallet > $BACKUP_DIR/db-$(date +%Y-%m-%d).sql


daily_min_date=$(date +%Y%m%d -d "$DAILY_BACKUP_MAX day ago")
monthly_min_month=$(date +%Y%m -d "$MONTHLY_BACKUP_MAX month ago")

for bk in $(find $BACKUP_DIR/db-*.sql -type f); do
        bk_date=$(echo $bk | grep -Eo '[[:digit:]]{4}-[[:digit:]]{2}-[[:digit:]]{2}')
        bk_year=$(echo $bk_date | cut -d'-' -f1)
        bk_month=$bk_year$(echo $bk_date | cut -d'-' -f2)
        bk_day=$(echo $bk_date | cut -d'-' -f3)
        bk_date=$(date -d "$bk_date" +"%Y%m%d")

        if [ $bk_date -lt $daily_min_date ] && [ $bk_day -ne 1 ];
        then
                rm $bk;
                echo "$bk is removed by daily backup max poicy" >> $BACKUP_LOG
        elif [ $bk_month -lt $monthly_min_month ];
        then
                rm $bk;
                echo "$bk is removed by monthly backup max poicy" >> $BACKUP_LOG
        fi
done


# you need to add this script to crontab by "crontab -e" in order to schedule a daily backup
# example config in crontab to run the script at 3:00am everyday:
#
# 0 3 * * * /home/zxqiu90/wallet/db_backup.sh
