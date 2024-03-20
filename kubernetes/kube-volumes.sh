#!/bin/bash

if [[ -z "$1" ]]; then CMD="apply"; else CMD=$1; fi

if [ "$CMD" == "delete" ]; then
    
    echo "Delete all persistent volume claims"
    kubectl delete pvc --all
    echo "Delete all persistent volumes"
    kubectl delete pv --all
    
else

    cd ./volume

    NODES=$(kubectl get nodes -o name | sed 's#^node/##' | xargs)
    counter=0
    for NODE in $NODES
    do
	echo "$CMD volume on node $NODE"
	sed 's/NODENAME/'${NODE}'/g' persistent-volumes.yaml \
	    | sed 's/NODEIX/'$counter'/g' \
	    | kubectl $CMD -f -

	(( counter++ ))
    done
    
fi
