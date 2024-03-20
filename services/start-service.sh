#!/bin/bash

function wait_for_port {
    while ! nc -z localhost $1; do   
	sleep 1 # wait for 1/10 of the second before check again
	printf "."
    done
}

PWD=`pwd`

function start_service {
    JAVA_DEBUG_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=localhost:$3"
    echo java $JAVA_DEBUG_OPTIONS -Dserver.port=$2 -jar $1/target/$1.war &
    java $JAVA_DEBUG_OPTIONS -Dserver.port=$2 -jar $1/target/$1.war &
    wait_for_port $2
}

function verify_postgres_db {

    IS_POSTGRESS_INSTALLED="false"
    IS_POSTGRESS_RUNNING="false"
    IS_POSTGRES_SCHEMA_CREATED="false"

    if type -p pg_isready > /dev/null; then
	IS_POSTGRES_INSTALLED="true"
	pg_isready -h localhost > /dev/null
	if [ $? = 0 ]; then
	    IS_POSTGRESS_RUNNING="true"
	    if sudo su postgres -c "psql -lqt 2> /dev/null" | cut -d \| -f 1 | grep -wq "oms"; then
		IS_POSTGRES_SCHEMA_CREATED="true"
		echo "Verified postgres db is up and schema is created"
	    else
		echo "Postgres DB schema not created. Some services may fail."
	    fi	
	else
	    echo "Postgres DB not running on localhost. Some services may fail."
	fi
    else
	echo "Postgres DB not installed on localhost. Some services may fail."
    fi
    
    if [ "$IS_POSTGRES_SCHEMA_CREATED" == "false" ] ||
	   [ "$IS_POSTGRESS_RUNNING" == "false" ] ||
	   [ "$IS_POSTGRES_INSTALLED" == "false" ]; then
	read -p "Start services anyway (y/n): " yn
	case $yn in
	    [Yy]* ) echo "";;
	    [Nn]* ) exit -1;;
	    * ) echo "";;
    esac
    fi
}

verify_postgres_db

if [ "$1" == "all" ]; then
    start_service gateway 8080 6000
    start_service admin 8081 6001
    start_service auth 8082 6002
    start_service product 8083 6003
    start_service order 8084 6004
    start_service inventory 8085 6005
elif [ "$1" == "gateway" ]; then
    start_service gateway 8080 6000
elif [ "$1" == "admin" ]; then
    start_service admin 8081 6001
elif [ "$1" == "auth" ]; then
    start_service auth 8082 6002
elif [ "$1" == "product" ]; then
    start_service product 8083 6003
elif [ "$1" == "order" ]; then
    start_service order 8084 6004
elif [ "$1" == "inventory" ]; then
    start_service inventory 8085 6005
else
    echo "Incorrect service name $1"
    echo "Use any one of the following as service name argument:"
    echo "all, admin, auth, product, gateway, order, inventory" 
fi

echo "Following java server processes are running"
jps -v | grep server
