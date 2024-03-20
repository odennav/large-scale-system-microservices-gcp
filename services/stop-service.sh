#!/bin/bash
function stop_service {
    SERVICE=$1
    PID=$(ps -eaf | grep -e ${SERVICE}.war | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        echo "Killing service $SERVICE with pid $PID"
        echo $PID | xargs kill
    else
        echo "$SERVICE service not running"
    fi
}
if [ "$1" == "all" ]; then
    stop_service auth
    stop_service product
    stop_service order
    stop_service inventory
    stop_service gateway
    stop_service admin
elif [ "$1" == "admin" ]; then
    stop_service admin
elif [ "$1" == "auth" ]; then
    stop_service auth
elif [ "$1" == "product" ]; then
    stop_service product
elif [ "$1" == "order" ]; then
    stop_service order
elif [ "$1" == "inventory" ]; then
    stop_service inventory
elif [ "$1" == "gateway" ]; then
    stop_service gateway
else
    echo "Incorrect service name $1"
    echo "Use any one of the following as service name argument:"
    echo "all, admin, auth, product, gateway, order, inventory" 
fi
