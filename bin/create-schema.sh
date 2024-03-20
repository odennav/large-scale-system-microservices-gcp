#!/bin/bash

# Set PSQL file path manually if psql not in path
PSQL=$(which psql)

if [ $? == 0 ]; then
    echo "Using psql: ${PSQL}"
else
    echo "psql client not found. set path manually"
    exit -1
fi

cp ./create-schema.sql /tmp/create-schema.sql
pushd /

sudo su postgres -c "${PSQL} -U postgres -d postgres -f /tmp/create-schema.sql"
if [ $? == 0 ]; then
    echo "Create schema succeeeded"
else
    echo "Create schema failed"
fi

rm -f /tmp/create-schema.sql
popd
