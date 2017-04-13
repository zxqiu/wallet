#!/bin/bash

gcloud compute copy-files target/wallet-0.0.1-SNAPSHOT.jar wallet-computer:/home/kangli/project/wallet/target --zone us-central1-c
