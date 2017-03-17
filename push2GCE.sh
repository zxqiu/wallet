#!/bin/bash

gcloud compute copy-files ~/src/wallet/target/wallet-0.0.1-SNAPSHOT.jar instance-wallet:~/wallet --zone us-central1-c
gcloud compute copy-files ~/src/wallet/wallet.yml instance-wallet:~/wallet --zone us-central1-c
gcloud compute copy-files ~/src/wallet/start.sh instance-wallet:~/wallet --zone us-central1-c
