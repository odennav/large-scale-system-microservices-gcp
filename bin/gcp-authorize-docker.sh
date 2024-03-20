#!/bin/bash

echo "-- Authorize Docker for Google Docker Registry --"
gcloud auth login
gcloud auth configure-docker -q

echo "-- Enable Google Container Registry service --"
gcloud services enable containerregistry.googleapis.com
