#!/bin/bash

if [ -z "$SERVER_HOSTS" ]; then
    echo "SERVER_HOSTS env variable is empty. Setting it as localhost"
    SERVER_HOSTS=localhost
fi

if [ -z "$SERVER_PORT" ]; then
    echo "SERVER_PORT env variable is empty. Setting it as 8000"
    SERVER_PORT=8000
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

if [ "$USE_SSL" == "true" ]; then
    echo "--- Generate Site Certificate ---"
    mkdir /etc/nginx/ssl
    printf "IN\nKA\nBLR\nNTW\nTEST-DEV\nlb-web\nlb-web@test.com\n" | openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/nginx/ssl/site.key -out /etc/nginx/ssl/site.crt
    cat /etc/nginx/ssl/site.crt
    
    SSL_CONFIG="    listen 443 ssl;${NEWLINE}    ssl_certificate \/etc\/nginx\/ssl\/site.crt;${NEWLINE}    ssl_certificate_key \/etc\/nginx\/ssl\/site.key;"
    sed -i -e 's/.*listen 80.*/'"${SSL_CONFIG}"'/g' /etc/nginx/conf.d/default.conf 
fi

echo "--- Nginx Config ---"
cat /etc/nginx/conf.d/default.conf
echo "--- Nginx Config End ---"

echo -- Execute nginx --
nginx -g 'daemon off;'
