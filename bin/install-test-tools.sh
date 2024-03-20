#!/bin/bash

function verify_success {
    if [ $1 != 0 ]; then
	echo "-- Installallation failed for $2 --"
	exit -1;
    else
	echo "-- Successfully installed $2 --"
    fi
}

sudo apt-get update

echo "-- Install jq --"
sudo apt-get install -y jq
verify_success $? jq

echo "-- Install PostgreSQL --"
sudo apt-get install -y postgresql
verify_success $? postgresql

echo "-- Done --"
