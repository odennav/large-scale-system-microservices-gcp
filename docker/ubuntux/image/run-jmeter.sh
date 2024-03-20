#!/bin/bash

SERVICES_HOST=lb-services
SERVICES_PORT=80

$JMETER_HOME/bin/jmeter -n -t /usr/jmeter-data/test-plan.jmx -Jusers=1 -Jcount=1 -DSERVICES_HOST=${SERVICES_HOST} -DSERVICES_PORT=${SERVICES_PORT}
