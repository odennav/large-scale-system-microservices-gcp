#!/bin/bash

#REGISTRY_HOST=eu.gcr.io
#REVISION_ID=latest

############### Arg Validations ###############
                                                                                                                                                     
if [ $# -gt 0 ] && [ ! -d "$1" ] && [ ! -f "$1" ]; then
    echo "First arg should point to config dir or file"; exit 1;
else
    CONFIG_FILES_PATH=$1
fi

if [ $# -gt 1 ] && [ "$2" != "apply" ] && [ "$2" != "delete" ]; then
    echo "Second parameter should be apply or delete"; exit 1;
else
    CMD=$2
fi

if [ $# -gt 2 ] && [ "$3" == "-i" ]; then
    INTERACTIVE="true"
else
    INTERACTIVE="false"
fi

if [ -z "$CONFIG_FILES_PATH" ]; then
    CONFIG_FILES_PATH="./config"
fi

if [ -z "$CMD" ]; then
    CMD="apply"
fi

############### Env Validations ###############

if [ -z "${REVISION_ID}" ]; then
    REVISION_ID=latest
    echo "Image revision id set to: ${REVISION_ID}"
fi

if [ -z "${REGISTRY_HOST}" ]; then
    REGISTRY_HOST="asia.gcr.io"
    echo "Image Registry Host set to: ${REGISTRY_HOST}"
fi

PROJECT_NAME=$(gcloud config get-value project)
IMAGE_REGISTRY_PATH="${REGISTRY_HOST}\/${PROJECT_NAME}\/ntw"

###############################################

if [ "$CMD" = "delete" ]; then ORDER="-r"; fi

if [ -d $CONFIG_FILES_PATH ]; then
    CONFIG_FILES=$(find ${CONFIG_FILES_PATH%/} -name "*.yaml" | sort $ORDER | xargs)
elif [ -f $CONFIG_FILES_PATH ]; then
    CONFIG_FILES=$CONFIG_FILES_PATH
fi

function kube_execute {
    CONFIG_FILE=$1
    echo "Executing: kubectl $CMD -f $CONFIG_FILE"
    sed 's/IMAGE_REGISTRY_PATH/'${IMAGE_REGISTRY_PATH}'/g' ${CONFIG_FILE} \
        | sed 's/REVISION_ID/'${REVISION_ID}'/g' \
        | kubectl ${CMD} -f -
}

function kube_execute_interactive {
    CONFIG_FILE=$1                                                                                                                                   
    read -p "Execute $CMD for $CONFIG_FILE (y/n): " yn
    case $yn in
        [Yy]* ) kube_execute $CONFIG_FILE;;                                                                                                          
        [Nn]* ) exit;;
        * ) kube_execute $CONFIG_FILE;;
    esac
}

for CONFIG_FILE in ${CONFIG_FILES}
do
    if [ "$INTERACTIVE" == "true" ]; then
        kube_execute_interactive $CONFIG_FILE
    else
        kube_execute $CONFIG_FILE
    fi
done
