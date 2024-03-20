#!/bin/bash

if [ -z "$PROJECT_ID" ]; then
    PROJECT_ID=$(gcloud config get-value project)
fi
echo "Current Project Id: ${PROJECT_ID}"

if [ -z "$REGION_ID" ]; then
    REGION_ID=asia-southeast1
fi
echo "Current Region Id: ${REGION_ID}"

if [ -z "$NETWORK_ID" ]; then
    NETWORK_ID=kube-net-1
fi

echo "-- Create Network ${NETWORK_ID} in region ${REGION_ID} --"
gcloud compute --project=$PROJECT_ID networks create ${NETWORK_ID} --subnet-mode=custom

echo "-- Create Sub-Network ${NETWORK_ID}-subnet-1 --"
gcloud compute --project=$PROJECT_ID networks subnets create ${NETWORK_ID}-subnet-1 --network=kube-net-1 --region=$REGION_ID --range=10.10.10.0/24 --enable-private-ip-google-access

echo "-- Create Firewall Rule --"
gcloud compute --project=$PROJECT_ID firewall-rules create ${NETWORK_ID}-fw-1 --network ${NETWORK_ID} --allow tcp:80,tcp:81,tcp:22,tcp:32100,tcp:16686,tcp:9090,tcp:5601,tcp:15672,icmp --source-ranges 0.0.0.0/0

echo "-- Done --"
