#!/bin/bash

if [ -z "$SERVER_HOSTS" ]; then
    echo "SERVER_HOSTS env variable is empty. Setting it as localhost"
    SERVER_HOSTS=localhost
fi

if [ -z "$SERVER_PORT" ]; then
    echo "SERVER_PORT env variable is empty. Setting it as 8000"
    SERVER_PORT=8080
fi

IFS="," read -a servers <<< "${SERVER_HOSTS}"

echo -- Configure host port --
echo "Setting upstream servers as:"
echo "${servers[@]}"

NEWLINE=$'\\\n'

for server in ${servers[@]}
do
    if [ -z "${SERVER_CONFIG}" ]; then
	SERVER_CONFIG="   server ${server}:${SERVER_PORT};"
    else
	SERVER_CONFIG="${SERVER_CONFIG}${NEWLINE}   server ${server}:${SERVER_PORT};"
    fi
done

sed -i -e 's/.*server host:port.*/'"${SERVER_CONFIG}"'/g' /etc/nginx/conf.d/default.conf 

echo "--- Nginx Config ---"
cat /etc/nginx/conf.d/default.conf
echo "--- Nginx Config End ---"

echo -- Execute nginx --
nginx -g 'daemon off;'
