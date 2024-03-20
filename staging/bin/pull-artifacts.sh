#!/bin/bash

OMS_ROOT=../..
OMS_STAGING=$OMS_ROOT/staging

function verify_success {
    if [ $1 == 0 ]; then
	echo "-- Copied build for $2 to staging --"
    else
	echo "-- Copy failed for $2. Build not available. Exiting! --"
	exit -1;
    fi
}

echo Create directories
test -d $OMS_STAGING/services || mkdir -p $OMS_STAGING/services && \
test -d $OMS_STAGING/registry || mkdir -p $OMS_STAGING/registry && \
test -d $OMS_STAGING/web || mkdir -p $OMS_STAGING/web && \
test -d $OMS_STAGING/spa || mkdir -p $OMS_STAGING/spa && \
test -d $OMS_STAGING/tests || mkdir -p $OMS_STAGING/tests && \
test -d $OMS_STAGING/schema || mkdir -p $OMS_STAGING/schema

echo Copy PyUI .tar.gz file
cp -r $OMS_ROOT/web/PyUI.tar.gz $OMS_STAGING/web
verify_success $? "web build"

echo Copy reactapp.tar.gz file
cp -r $OMS_ROOT/spa/reactapp.tar.gz $OMS_STAGING/spa
verify_success $? "spa build"

echo Copy Services war files
cp $OMS_ROOT/services/target/*.war $OMS_STAGING/services
verify_success $? "services build"

echo Copy eureka.jar file
cp -r $OMS_ROOT/services/target/discovery.jar $OMS_STAGING/registry
verify_success $? "discovery service build"

echo Copy Jmeter tests files
cp -r $OMS_ROOT/tests/jmeter/* $OMS_STAGING/tests
verify_success $? "jmeter files"

echo Copy create schema script
cp -r $OMS_ROOT/bin/*.sql $OMS_STAGING/schema
verify_success $? "create schema files"

echo Done!!
