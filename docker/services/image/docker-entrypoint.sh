#!/bin/bash

if [ -z "$1" ]; then
    echo "**** ERROR: Missing service id argument ****"
    exit -1
else
    if [ "$1" == "bash" ]; then
	exec "/bin/bash"
    fi
    SERVICE_ID=$1
    echo "Running service ${SERVICE_ID}"
fi

if [ -z "${JAVA_HEAP_MEMORY}" ]; then
  JAVA_HEAP_MEMORY=-Xmx512M
fi

if [ -z "${JMX_HOST}" ]; then
  JMX_HOST=`hostname`
fi

if [ -z "${JMX_PORT}" ]; then
    JMX_PORT=5551
fi

if [ -z "${JSTATD_PORT}" ]; then
    JSTATD_PORT=1099
fi

if [ -z "${DEBUG_PORT}" ]; then
    DEBUG_PORT=6005
fi


echo "Execute jstatd for monitoring jvm gc"
jstatd -p ${JSTATD_PORT} -J-Djava.security.policy=/usr/jstatd.policy -J-Djava.rmi.server.hostname=${JMX_HOST} &

# Wait for cassandra/postgres
if [ -n "${WAIT_TIME_FOR_DB}" ]; then
    count=0
    echo "Waiting for Cassandra DB"
    while [ $count -lt ${WAIT_TIME_FOR_DB} ]
    do
	sleep 1
	printf "$(($WAIT_TIME_FOR_DB - $count)) -> "
	count=$(( $count + 1 ))
    done
    echo "0"
fi

JAVA_GC_OPTIONS="-XX:NewRatio=1 -XX:+UseConcMarkSweepGC -XX:SoftRefLRUPolicyMSPerMB=1 -Xloggc:gc.log -XX:+PrintGCDetails"
JAVA_DEBUG_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"
JAVA_JMX_OPTIONS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.rmi.port=${JMX_PORT} -Dcom.sun.management.jmxremote.port=${JMX_PORT} -Djava.rmi.server.hostname=${JMX_HOST}"

JAVA_OPTIONS="$JAVA_HEAP_MEMORY $JAVA_GC_OPTIONS $JAVA_DEBUG_OPTIONS $JAVA_JMX_OPTIONS"

echo "Executing Spring Boot with Java Args: $JAVA_OPTIONS"
java $JAVA_OPTIONS -jar /usr/war/${SERVICE_ID}.war
