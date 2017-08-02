#!/bin/bash

gcloud compute copy-files start.sh instance-wallet:/home/zxqiu90/wallet --zone us-central1-c
gcloud compute copy-files db_backup.sh instance-wallet:/home/zxqiu90/wallet --zone us-central1-c
