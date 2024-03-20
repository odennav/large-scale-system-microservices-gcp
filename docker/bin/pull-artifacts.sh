#!/bin/bash

OMS_ROOT=../..
OMS_STAGING=$OMS_ROOT/staging
OMS_DEPLOY=$OMS_ROOT/docker

echo Copy PyUI to web
cp $OMS_STAGING/web/PyUI.tar.gz $OMS_DEPLOY/web/image

echo Copy react app to spa
cp $OMS_STAGING/spa/reactapp.tar.gz $OMS_DEPLOY/spa/image

echo Copy PyUI to lb-web
cp $OMS_STAGING/web/PyUI.tar.gz $OMS_DEPLOY/lb-web/image

echo Copy create-schema.sql to postgres
cp $OMS_STAGING/schema/*.sql $OMS_DEPLOY/postgres/image

echo Copy Services war files
test -d $OMS_DEPLOY/services/image/war || mkdir -p $OMS_DEPLOY/services/image/war && \
	cp $OMS_STAGING/services/*.war $OMS_DEPLOY/services/image/war

echo Copy Eureka Server war file
test -d $OMS_DEPLOY/eureka/image || mkdir -p $OMS_DEPLOY/eureka/image && \
	cp $OMS_STAGING/registry/discovery.jar $OMS_DEPLOY/eureka/image

echo Copy Jmeter test files
test -d $OMS_DEPLOY/jmeter/image/tests || mkdir -p $OMS_DEPLOY/jmeter/image/tests && \
	cp $OMS_STAGING/tests/* $OMS_DEPLOY/jmeter/image/tests
test -d $OMS_DEPLOY/ubuntux/image/tests || mkdir -p $OMS_DEPLOY/ubuntux/image/tests && \
	cp $OMS_STAGING/tests/* $OMS_DEPLOY/ubuntux/image/tests

echo Done!!
