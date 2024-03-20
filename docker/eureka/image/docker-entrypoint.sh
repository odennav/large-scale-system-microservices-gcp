#!/bin/bash

if [ -z "${JAVA_OPTIONS}" ]; then
  JAVA_OPTIONS=-Xmx512M
fi

echo "Executing Spring Boot"
java $JAVA_OPTIONS -jar /usr/war/discovery.jar
