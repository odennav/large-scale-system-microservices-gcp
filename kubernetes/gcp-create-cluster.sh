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
echo "Current Network Id: ${NETWORK_ID}"

CLUSTER_ID=kube-cluster-1
SUBNET_ID=${NETWORK_ID}-subnet-1

# 0 for Zonal cluster
# 1 for Regional cluster
IS_REGIONAL_CLUSTER=1
REGION_ID=asia-southeast1
echo "Region id: $REGION_ID"
ZONE_ID=${REGION_ID}-b

echo "-- Creating Kubernetes Cluster --"

if [ ${IS_REGIONAL_CLUSTER} == 1 ]; then
    echo "-- Creating Regional Cluster ${CLUSTER_ID} in region ${REGION_ID}"
    gcloud beta container --project "${PROJECT_ID}" clusters create "kube-cluster-1" --region "${REGION_ID}" --no-enable-basic-auth --cluster-version "1.17.9-gke.1504" --machine-type "custom-4-7680" --image-type "UBUNTU" --disk-type "pd-standard" --disk-size "50" --metadata disable-legacy-endpoints=true --scopes "https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management.readonly","https://www.googleapis.com/auth/trace.append" --max-pods-per-node "24" --preemptible --num-nodes "1" --enable-stackdriver-kubernetes --enable-ip-alias --network "projects/${PROJECT_ID}/global/networks/${NETWORK_ID}" --subnetwork "projects/${PROJECT_ID}/regions/${REGION_ID}/subnetworks/${SUBNET_ID}" --cluster-ipv4-cidr "192.168.0.0/16" --services-ipv4-cidr "10.10.11.0/24" --default-max-pods-per-node "24" --no-enable-master-authorized-networks --addons HorizontalPodAutoscaling --no-enable-autoupgrade --no-enable-autorepair --max-surge-upgrade 1 --max-unavailable-upgrade 0
else
    echo "-- Creating Zonal Cluster ${CLUSTER_ID} in region ${ZONE_ID}"
    gcloud beta container --project "${PROJECT_ID}" clusters create "${CLUSTER_ID}" --zone "${ZONE_ID}" --no-enable-basic-auth --cluster-version "1.17.9-gke.1504" --machine-type "custom-4-7680" --image-type "UBUNTU" --disk-type "pd-standard" --disk-size "50" --metadata disable-legacy-endpoints=true --scopes "https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management.readonly","https://www.googleapis.com/auth/trace.append" --preemptible --num-nodes "3" --enable-stackdriver-kubernetes --enable-ip-alias --network "projects/${PROJECT_ID}/global/networks/${NETWORK_ID}" --subnetwork "projects/${PROJECT_ID}/regions/${REGION_ID}/subnetworks/${SUBNET_ID}" --cluster-ipv4-cidr "192.168.0.0/16" --services-ipv4-cidr "10.10.11.0/24" --default-max-pods-per-node "24" --no-enable-master-authorized-networks --addons HorizontalPodAutoscaling --enable-autoupgrade --enable-autorepair --max-surge-upgrade 1 --max-unavailable-upgrade 0
fi

if [ $? != 0 ]; then
    echo "-- Failed to create the cluster $CLUSTER_ID --"
    exit -1;
fi

echo "-- Get Kubernetes Cluster Credentials for $CLUSTER_ID --"
if [ ${IS_REGIONAL_CLUSTER} == 1 ]; then
    gcloud container clusters get-credentials $CLUSTER_ID --region $REGION_ID --project $PROJECT_ID
else
    gcloud container clusters get-credentials $CLUSTER_ID --zone $ZONE_ID --project $PROJECT_ID
fi

echo "-- Done --"
