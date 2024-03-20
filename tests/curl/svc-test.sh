#!/bin/bash

if [ -z ${SERVICES_HOST} ]; then
    SERVICES_HOST=localhost
fi
if [ -z ${SERVICES_PORT} ]; then
    SERVICES_PORT=8080
fi
if [ -z ${USER_NAME} ]; then
    USER_NAME=admin
fi
if [ -z ${USER_PASS} ]; then
    USER_PASS=password
fi

AUTH_CLIENT_CRED=$(printf "web-client:secret" | base64 - )

echo -e '\n--------Get Token----------\n'

curl -X POST -H "Content-Type: application/json" -H "Authorization: Basic ${AUTH_CLIENT_CRED}" -D - --data '{"id":"'${USER_NAME}'","password":"'${USER_PASS}'"}' ${SERVICES_HOST}:${SERVICES_PORT}/auth/token

ACCESS_TOKEN=$(curl -s -X POST -H "Authorization: Basic ${AUTH_CLIENT_CRED}" -H "Content-Type: application/json" --data '{"id":"'${USER_NAME}'","password":"'${USER_PASS}'"}' ${SERVICES_HOST}:${SERVICES_PORT}/auth/token | jq .access_token | tr -d '"')

echo -e '\n---------Get User Auth---------\n'

curl -X GET -D - ${SERVICES_HOST}:${SERVICES_PORT}/auth/token/user?access_token=$ACCESS_TOKEN

echo -e '\n-------Get Products-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - ${SERVICES_HOST}:${SERVICES_PORT}/products

echo -e '\n-------Get Product-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - ${SERVICES_HOST}:${SERVICES_PORT}/products/test-product-00001

echo -e '\n-------Get Products by Ids-----------\n'

curl -X POST -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/json" --data '["test-product-00001","test-product-00002"]' -D - ${SERVICES_HOST}:${SERVICES_PORT}/products/ids

echo -e '\n-------Post Add to Cart-----------\n'

curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -H "Authorization: Bearer $ACCESS_TOKEN" "http://${SERVICES_HOST}:${SERVICES_PORT}/carts?id=admin&productId=Test-Product-1&quantity=2"

curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -H "Authorization: Bearer $ACCESS_TOKEN" "http://${SERVICES_HOST}:${SERVICES_PORT}/carts?id=admin&productId=Test-Product-2&quantity=2"

echo -e '\n--------Post Remove from Cart----------\n'

curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -H "Authorization: Bearer $ACCESS_TOKEN" "http://${SERVICES_HOST}:${SERVICES_PORT}/carts?id=anurag&productId=Test-Product-2&quantity=0"

echo -e '\n-------Get Orders-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - ${SERVICES_HOST}:${SERVICES_PORT}/orders

echo -e '\n-------Delete Test Data-----------\n'

curl -X DELETE -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/admin/dataset

echo -e '\n-------Post Test Data 10-----------\n'

curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -H "Authorization: Bearer $ACCESS_TOKEN" -D - "http://${SERVICES_HOST}:${SERVICES_PORT}/admin/dataset?userCount=10&productCount=10"
