#!/bin/bash

USER_COUNT=$1
PRODUCT_COUNT=$2
PRODUCT_RANGE_MAX=$3

if [ -z "$USER_COUNT" ]; then
    USER_COUNT=100
fi

if [ -z "$PRODUCT_COUNT" ]; then
    PRODUCT_COUNT=100
fi
if [ -z "$PRODUCT_RANGE_MAX" ]; then
    PRODUCT_RANGE_MAX=100
fi

rm -f products.csv
touch products.csv

rm -f users.csv
touch users.csv

RANDOM=0

for i in $(seq 1 $USER_COUNT); do
    Uid=$i
    Uid=$(printf "%05d" $Uid)
    echo "test-user-${Uid},password" >> users.csv
done

for i in $(seq 1 $PRODUCT_COUNT); do
    Pid1=$((1 + RANDOM % PRODUCT_RANGE_MAX))
    Pid1=$(printf "%05d" $Pid1)
    Pid2=$((1 + RANDOM % PRODUCT_RANGE_MAX))
    Pid2=$(printf "%05d" $Pid2)
    Pid3=$((1 + RANDOM % PRODUCT_RANGE_MAX))
    Pid3=$(printf "%05d" $Pid3)
    echo "test-product-${Pid1},test-product-${Pid2},test-product-${Pid3}" >> products.csv
done
