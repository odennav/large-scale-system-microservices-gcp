#!/bin/bash

if [ -z "${TEST_HOST}" ]; then 
    TEST_HOST=gateway-svc-1
fi
if [ -z "${TEST_PORT}" ]; then 
    TEST_PORT=8080
fi
if [ -z "${USER_NUM}" ]; then
    USER_NUM=1
fi
if [ -z "${LOOP_NUM}" ]; then 
    LOOP_NUM=1
fi
if [ -z "${LOG_LEVEL}" ]; then 
    LOG_LEVEL=ERROR
fi
if [ -z "${LOG_LEVEL_HTTP}" ]; then 
    LOG_LEVEL_HTTP=ERROR
fi

if [ -n "$1" ]; then
    TEST_HOST=$1
fi
if [ -n "$2" ]; then
    TEST_PORT=$2
fi
if [ -n "$3" ]; then
    USER_NUM=$3
fi
if [ -n "$4" ]; then
    LOOP_NUM=$4
fi
if [ -n "$5" ]; then
    LOG_LEVEL=$5
fi
if [ -n "$6" ]; then
    LOG_LEVEL_HTTP=$6
fi

echo "Generate test data"
pushd /usr/data/jmeter
./create-data.sh 100 100 100

printf "Executing tests with args:"
printf "host=$TEST_HOST; port=$TEST_PORT; users=$USER_NUM; loop=$LOOP_NUM; log-level=$LOG_LEVEL http-log-level=$LOG_LEVEL_HTTP \n"
/usr/bin/jmeter -n -t /usr/data/jmeter/test-plan.jmx \
		-Jusers=${USER_NUM} -Jcount=${LOOP_NUM} -L${LOG_LEVEL} -Lorg.apache.http=${LOG_LEVEL_HTTP} \
		-DSERVICES_HOST=${TEST_HOST} -DSERVICES_PORT=${TEST_PORT}
