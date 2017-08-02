#!/bin/bash

gcloud compute copy-files target/wallet-0.0.1-SNAPSHOT.jar instance-wallet:/home/zxqiu90/wallet/target --zone us-central1-c
gcloud compute copy-files wallet.yml instance-wallet:/home/zxqiu90/wallet --zone us-central1-c
gcloud compute copy-files start.sh instance-wallet:/home/zxqiu90/wallet --zone us-central1-c
gcloud compute copy-files db_backup.sh instance-wallet:/home/zxqiu90/wallet --zone us-central1-c
