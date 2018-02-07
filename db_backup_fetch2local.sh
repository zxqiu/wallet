#!/bin/bash

gcloud compute copy-files instance-wallet:/home/zxqiu90/wallet-db/db-20*.sql . --zone us-central1-c
