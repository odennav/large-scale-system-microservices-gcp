#!/bin/bash

echo "-- Cleaning Docker Build Dirs --"
PWD=`pwd`
DOCKER_PROJ=$PWD/..

declare -a arr=("cassandra" "postgres" "eureka" "rest" "lb-rest" "web" "lb-web" "jmeter")

echo "-- Cleaning logs and data files --"
for i in "${arr[@]}"
do
    cd $DOCKER_PROJ/$i
    rm -rf data logs
done

cd $DOCKER_PROJ
rm -rf data logs


echo "-- Clean web --"
cd $DOCKER_PROJ/web/image
rm -f  PyUI.tar.gz 

echo "-- Clean lb-web --"
cd $DOCKER_PROJ/lb-web/image
rm -f PyUI.tar.gz 

echo "-- Clean rest --"
cd $DOCKER_PROJ/rest/image/war
rm -f *.war 

echo "-- Clean Eureka --"
cd $DOCKER_PROJ/eureka/image
rm -f DiscoverySvc.war 

echo "-- Clean Jmeter --"
cd $DOCKER_PROJ/jmeter/image/tests
rm -f *

echo "-- Clean Ubuntux --"
cd $DOCKER_PROJ/ubuntux/image/tests
rm -f * 

echo "-- Done Cleaning Docker Build Dirs --"

