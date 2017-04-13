#!/bin/bash

HELP_MSG="Please refer to 'https://developers.google.com/identity/protocols/application-default-credentials' for more info"

if [[ ! -v GOOGLE_APPLICATION_CREDENTIALS ]]; then
    echo "Error: GOOGLE_APPLICATION_CREDENTIALS is not set"; echo $HELP_MSG; exit
elif [[ -z "$GOOGLE_APPLICATION_CREDENTIALS" ]]; then
    echo "Error: GOOGLE_APPLICATION_CREDENTIALS is set to empty string"; echo $HELP_MSG; exit
else
    echo "GOOGLE_APPLICATION_CREDENTIALS is set to '$GOOGLE_APPLICATION_CREDENTIALS'"
fi

sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to 8080
java -jar target/wallet-0.0.1-SNAPSHOT.jar server wallet.yml &
