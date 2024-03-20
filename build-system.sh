#!/bin/bash

export OMS_ROOT=`pwd`

#cd $OMS_ROOT

TARGET=$1

echo "-- Start --"

function verify_success {
    if [ $1 == 0 ]; then
	echo "-- Build succeeded for $2 --"
    else
	echo "-- Build failed for $2. Exiting! --"
	exit -1;
    fi
}

echo "--- Verify system and tools ---"

./bin/verify-system.sh
verify_success $? "operating system verification"

./bin/verify-build-tools.sh
verify_success $? "build tools installation verification"

echo "--- Verification Done!! ---"


############################## Clean ####################################

function do_clean {
    echo "-- Clean --"
    cd $OMS_ROOT/services
    mvn clean
    verify_success $? "services clean"
    
    cd $OMS_ROOT/tests/jmeter
    rm -f *.csv
    verify_success $? "jmeter clean"

    if [ "$1" == "all" ]; then
	cd $OMS_ROOT/staging
	rm -rf services web registry tests
	verify_success $? "staging clean"
	
	cd $OMS_ROOT/docker/bin
	./clean-files.sh
	verify_success $? "docker clean files"

	./clean-images.sh
	verify_success $? "docker clean images"
    fi
    echo "-- Done --"
}


############################## Build ####################################

function do_build {

    echo "-- Build --"

    if [ -z "$2" ]; then
	
	do_build_services

	do_create_test_data
	
	do_build_web

	do_build_spa
    
    else

	if [ "$2" == "web" ]; then
	    do_build_web
	elif [ "$2" == "services" ]; then
	    do_build_services
	elif [ "$2" == "service" ] && [ -n "$3" ]; then
	    do_build_service $3
	else
	    echo "Wrong set of parameters"
	fi

    fi
	
    echo "-- Done --"
}

function do_create_test_data {
    cd $OMS_ROOT/tests/jmeter
    ./create-data.sh
    verify_success $? "jmeter test data"
}

function do_build_web {
    cd $OMS_ROOT/web
    ./create-build.sh
    verify_success $? "web zip"
}    

function do_build_spa {
    cd $OMS_ROOT/spa
    ./create-build.sh
    verify_success $? "spa"
}    

function do_build_services {
    cd $OMS_ROOT/services
    mvn package
    verify_success $? "services"
}

function do_build_service {
    cd $OMS_ROOT/services/$1
    mvn package
    verify_success $? "service $1"
    cp ./target/*.war ../target
}


############################## Staging ####################################

function do_stage {
    echo "-- Pull artifacts to Staging --"
    cd $OMS_ROOT/staging/bin
    ./pull-artifacts.sh
    verify_success $? "staging pull artifacts"

    echo "-- Done --"
}



############################## Images ####################################

function do_images {
    echo "-- Check if docker daemon is running --"
    docker version
    if [ $? == 0 ]; then
	echo "Docker daemon installed and running"
    elif [ $? == 1 ]; then
	echo "Build failed -- Docker daemon installed but not running"
	exit -1;
    else
	echo "Build failed -- Docker daemon not installed"
	exit -1;
    fi

    echo "-- Pull all build artifacts to staging area --"
    do_stage
    
    echo "-- Pull artifacts from Staging to Docker images dir --"
    cd $OMS_ROOT/docker/bin
    ./pull-artifacts.sh
    verify_success $? "docker pull artifacts"

    echo "-- Build Docker Images --"
    cd $OMS_ROOT/docker
    docker-compose build $1
    verify_success $? "docker build $1"

    cd $OMS_ROOT/docker/jmeter
    docker-compose build
    verify_success $? "docker jmater build"

    echo "-- Done --"
}

if [ "$TARGET" == "clean" ]; then
    do_clean $2
elif [ "$TARGET" == "build" ]; then
    do_build $1 $2 $3
elif [ "$TARGET" == "stage" ]; then
    do_stage
elif [ "$TARGET" == "images" ]; then
    do_images $2
else
    read -p "Do you wish to do complete build (y/n): " yn
    case $yn in
        [Yy]* ) do_clean; do_build; do_stage; do_images; break;;
        [Nn]* ) exit;;
        * ) do_clean; do_build; do_stage; do_images;;
    esac    
fi
    
echo "-- Done --"
