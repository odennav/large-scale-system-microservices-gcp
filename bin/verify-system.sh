#!/bin/bash

if [ -f "/etc/os-release" ]; then
    . /etc/os-release
    if [ "$NAME" != "Ubuntu" ]; then
        ERROR_MESSAGE="WARNING: Unsupported OS. Recommended OS is Ubuntu."
    else
        echo "OS Name is $NAME"
        if [ "$VERSION_ID" != "20.04" ] && [ "$VERSION_ID" != "22.04" ]; then
            ERROR_MESSAGE="WARNING: Unsupported OS version. Recommended version are Ubuntu 20.04 and 22.04 only"
        else
            echo "$NAME version is $VERSION_ID"
        fi
    fi
else
    ERROR_MESSAGE="WARNING: Unsupported OS. Recommended OS is Ubuntu 22.04"
fi

if [ -n "$ERROR_MESSAGE" ]; then
    echo "################# WARNING ###############"
    echo $ERROR_MESSAGE
    echo "#########################################"
    exit -1
fi
