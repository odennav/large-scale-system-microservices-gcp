#!/bin/bash

OMS_ROOT=`pwd`
cd $OMS_ROOT

TARGET=$1

declare -a service_containers=("eureka" "gateway-svc" "lb-services" "admin-svc" "auth-svc" "product-svc" "order-svc" "inventory-svc")
declare -a web_containers=("web" "lb-web")
declare -a data_containers=("cassandra" "postgres" "rabbitmq" "redis")
declare -a infra_containers=("elasticsearch" "kibana" "jaeger-collector" "jaeger-query" "jaeger-agent" "fluentd" "es-exporter" "redis-exporter" "pg-exporter" "prometheus")

echo "-- Start --"


############################## Start ####################################

function do_start {
    echo "-- Run Containers --"
    cd $OMS_ROOT/docker
    if [ "$1" == "infra" ]; then
	do_start_infra
    elif [ "$1" == "app" ]; then
	do_start_app
    else
	do_start_infra
	do_start_app
    fi
    echo "-- Done --"
}

function do_start_infra {
    echo "-- Run Infra Containers --"
    for container in "${infra_containers[@]}"
    do
	do_start_one $container
	sleep 5s
    done    
    echo "-- Done --"
}

function do_start_app {
    echo "-- Run App Containers --"
    for container in "${data_containers[@]}"
    do
	do_start_one $container
	sleep 5s
    done    
    for container in "${web_containers[@]}"
    do
	do_start_one $container
	sleep 5s
    done    
    for container in "${service_containers[@]}"
    do
	do_start_one $container
	sleep 30s
    done    
    echo "-- Done --"
}

function do_start_one {
    docker-compose up -d $1
    if [ $? != 0 ]; then
	echo "Build failed - Run $1 container failed"
	exit -1;
    fi
}


############################## Test ####################################

function do_test {
    echo "-- Run tests --"
    cd $OMS_ROOT/docker/jmeter
    docker-compose build
    docker-compose up
    if [ $? != 0 ]; then
	echo "Tests failed - Unable to start Jmeter container"
	exit -1;
    fi
    echo "-- Done --"
}


############################## Stop ####################################

function do_stop {
    echo "-- Stop containers --"
    cd $OMS_ROOT/docker
    if [ "$1" == "infra" ]; then
	do_stop_infra
    elif [ "$1" == "app" ]; then
	do_stop_app $2
    elif [ "$1" == "all" ]; then
	do_stop_app
	do_stop_infra
    else
	echo "Incorrect parameter. Specify infra, app, all ?"
	exit -1;
    fi
    docker-compose rm -f
    echo "-- Done --"
}

function do_stop_infra {
    echo "-- Stop Infra Containers --"
#    for container in "${infra_containers[@]}"
#    do
#	do_stop_one $container
#    done    
    do_stop_one "$(echo ${infra_containers[@]})"
    echo "-- Done --"
}

function do_stop_app {
    echo "-- Stop App Containers --"
#    for (( index=${#service_containers[@]}-1 ; index>=0 ; index-- )) ;
#    do
#	do_stop_one "${service_containers[index]}"
#    done    
#    for (( index=${#web_containers[@]}-1 ; index>=0 ; index-- )) ;
#    do
#	do_stop_one "${web_containers[index]}"
#    done    
#    for (( index=${#data_containers[@]}-1 ; index>=0 ; index-- )) ;
#    do
#	do_stop_one "${data_containers[index]}"
    #    done
    if [ "$1" == "services" ]; then
	do_stop_one "$(echo ${service_containers[@]})"
    elif [ "$1" == "web" ]; then
	do_stop_one "$(echo ${web_containers[@]})"
    else
	do_stop_one "$(echo ${service_containers[@]})"
	do_stop_one "$(echo ${web_containers[@]})"
	do_stop_one "$(echo ${data_containers[@]})"
    fi
    echo "-- Done --"
}

function do_stop_one {
    docker-compose stop $1
    if [ $? != 0 ]; then
	echo "Build failed - Stop $1 container failed"
	exit -1;
    fi
}


############################## Update ####################################

function do_update {
    
    COMPONENT=$1

    if [ "$COMPONENT" == "service" ]; then
	SVC=$2
	cd $OMS_ROOT/services/${SVC}
	mvn clean package
	if [ $? != 0 ]; then
	    echo "Build failed!! .. Exiting"
	    exit -1;
	fi
	cp ./target/${SVC}.war ../target
	CONTAINER=$2-svc
	COMPONENT=services

    elif [ "$COMPONENT" == "web" ]; then

	cd $OMS_ROOT/web
	./create-build.sh
	CONTAINER=web
	
    else

	echo "Incorrect component name. Specify service or web.";
	exit -1;

    fi

    cd $OMS_ROOT/staging/bin
    ./pull-artifacts.sh

    cd $OMS_ROOT/docker/bin
    ./pull-artifacts.sh

    cd $OMS_ROOT/docker
    docker-compose build ${COMPONENT}
    docker-compose stop ${CONTAINER}
    docker-compose rm -f ${CONTAINER}
    docker-compose up -d ${CONTAINER}
}


############################## Status ####################################

function get_status {
    cd $OMS_ROOT/docker
    watch -n 3 docker-compose ps
}



############################## All ####################################

function do_all {
    do_start 
    i="0"
    RESPONSE=$(curl -s -I -X GET http://localhost:9000/GatewaySvc/status | head -n 1)
    echo "$i - $RESPONSE"
    while [ "$(echo $RESPONSE | cut -d$' ' -f2)" != "200" ] && [ $i -lt 120 ];
    do sleep 5;
       RESPONSE=$(curl -s -I -X GET http://localhost:9000/GatewaySvc/status | head -n 1)
       i=$[$i+5]
       echo "$i - $RESPONSE"
    done;
    sleep 5
    do_test
    do_stop
}


############################## Main ####################################

if [ "$TARGET" == "start" ]; then
    do_start $2
elif [ "$TARGET" == "update" ]; then
    do_update $2 $3
elif [ "$TARGET" == "test" ]; then
    do_test
elif [ "$TARGET" == "stop" ]; then
    do_stop $2
elif [ "$TARGET" == "status" ]; then
    get_status
else
    read -p "Do you wish to start all: " yn
    case $yn in
        [Yy]* ) do_all; break;;
        [Nn]* ) exit;;
        * ) do_all;;
    esac    
fi
    
echo "-- Done --"
