#!/bin/bash

if [ -z "${REPLICATION_FACTOR}" ]; then
    REPLICATION_FACTOR=1
fi

if [ -z "${SCHEMA_SEED_INSTANCE}" ]; then
    SCHEMA_SEED_INSTANCE="cassandra"
fi

CMD="cqlsh -f ./create-schema.cql"
SLEEP_DURATION=5
function create_schema {
    $CMD
    EXIT_CODE=$?
    while [ $EXIT_CODE != 0 ]; do
        sleep $SLEEP_DURATION
        $CMD
        EXIT_CODE=$?
	if [ $EXIT_CODE == 0 ]; then
	    echo "++++++++++++++++++++ SCHEMA CREATED ++++++++++++++++++++"
	elif [ $EXIT_CODE == 2 ]; then
            echo "?????????? Schema script error or schema already exists. Aborting. ??????????"
            exit 2
        else
	    echo "-------------- Cqlsh client connect error. Exit code: $EXIT_CODE  ---------------"
	    echo "-------------- Will try again connecting after $SLEEP_DURATION sec.    ---------------"
	fi
    done
}

sed -i -e 's/#REPLICATION_FACTOR#/'${REPLICATION_FACTOR}'/g' /create-schema.cql

if [[ $(hostname -s) = ${SCHEMA_SEED_INSTANCE} ]]; then
    create_schema &
fi

exec /usr/local/bin/docker-entrypoint.sh "$@"
