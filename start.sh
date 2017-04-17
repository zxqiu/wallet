#!/bin/bash

sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to 8080
java -jar target/wallet-0.0.1-SNAPSHOT.jar server wallet.yml --release &
