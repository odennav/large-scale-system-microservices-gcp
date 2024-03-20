#!/bin/bash

PROJECT=ntw-sample-sys
ZONE=asia-southeast1-b
INSTANCE_NAME=build-1
SNAPSHOT_NAME=ubuntu-18-gcloud-docker
USER=anurag_yadav

echo "-- Create disk ${INSTANCE_NAME} from snampshot ${SNAPSHOT_NAME} --"
gcloud compute --project "${PROJECT}" disks create "${INSTANCE_NAME}" --size "20" --zone "${ZONE}" --source-snapshot "${SNAPSHOT_NAME}" --type "pd-standard"

echo "-- Create machine ${INSTANCE_NAME} from disk ${INSTANCE_NAME} --"
gcloud beta compute --project=${PROJECT} instances create ${INSTANCE_NAME} --zone=${ZONE} --machine-type=n1-standard-1 --subnet=default --network-tier=PREMIUM --no-restart-on-failure --maintenance-policy=TERMINATE --preemptible --service-account=271073603011-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/devstorage.read_only,https://www.googleapis.com/auth/logging.write,https://www.googleapis.com/auth/monitoring.write,https://www.googleapis.com/auth/servicecontrol,https://www.googleapis.com/auth/service.management.readonly,https://www.googleapis.com/auth/trace.append --disk=name=${INSTANCE_NAME},device-name=${INSTANCE_NAME},mode=rw,boot=yes --reservation-affinity=any


echo "Wait for 30 seconds"
for i in `seq 1 5`; do
    sleep 2s
    printf .
done
echo ""

echo "-- Copy your public key for git --"
gcloud compute scp ~/.ssh/id_rsa ${USER}@${INSTANCE_NAME}:~/.ssh --project ${PROJECT} --zone ${ZONE}

echo "-- Add bitbucket to known hosts --"
gcloud compute ssh ${USER}@${INSTANCE_NAME} --project ${PROJECT} --zone ${ZONE} -- 'touch ~/.ssh/known_hosts'
gcloud compute ssh ${USER}@${INSTANCE_NAME} --project ${PROJECT} --zone ${ZONE} -- 'ssh-keyscan bitbucket.org >> ~/.ssh/known_hosts'

echo "-- Clone git repository --"
gcloud compute ssh ${USER}@${INSTANCE_NAME} --project ${PROJECT} --zone ${ZONE} -- 'git clone git@bitbucket.org:newtechways/minisys.git'

echo "-- Install build tools --"
gcloud compute ssh ${USER}@${INSTANCE_NAME} --project ${PROJECT} --zone ${ZONE} -- '~/minisys/bin/install-build-tools.sh'
