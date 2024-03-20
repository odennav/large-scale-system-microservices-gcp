#!/bin/bash

function verify_success {
    if [ $1 != 0 ]; then
	echo "-- Installallation failed for $2 --"
	exit -1;
    else
	echo "-- Successfully installed $2 --"
    fi
}

./verify-system.sh
verify_success $? "operating system requirements"

sudo apt-get update

echo "-- Install JDK --"
sudo apt-get install -y openjdk-11-jdk
verify_success $? java

echo "-- Install Maven --"
sudo apt-get install -y maven
verify_success $? maven

echo "-- Install NodeJS --"
curl -sL https://deb.nodesource.com/setup_16.x | sudo bash - && \
sudo apt-get install -y nodejs
verify_success $? nodejs

echo "-- Install python --"
sudo apt-get install -y python3-pip
verify_success $? python3-pip

sudo pip3 install "django>=3.2,<4" gunicorn python-json-logger 
verify_success $? "django and gunicorn"

sudo pip3 install opentracing jaeger-client django-prometheus
verify_success $? "jaeger tracing and prometheus"

echo "-- Done --"

./verify-build-tools.sh
