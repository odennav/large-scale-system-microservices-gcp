#!/bin/bash

echo exit | cqlsh
if [ $? = 0 ]; then
    echo "Ready"
else
    echo "Not Ready" >&2
fi
