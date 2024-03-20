#!/bin/bash

NL=$'\n'

if type -p java > /dev/null; then
    version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" == "11"* ]]; then
        SUCCESS_MESSAGE="Java version: 11 is installed."
    else
        ERROR_MESSAGE="Java version 11 is not installed."
    fi
else
    ERROR_MESSAGE="Java is not installed."
fi


if type -p mvn > /dev/null; then
    version=$(mvn -v | grep "Apache Maven" | awk '{print $3}')
    SUCCESS_MESSAGE="${SUCCESS_MESSAGE}${NL}Maven version: $version is installed"
else
    ERROR_MESSAGE="${ERROR_MESSAGE}${NL}Maven is not installed."
fi


if type -p node > /dev/null; then
    version=$(node -v)
    SUCCESS_MESSAGE="${SUCCESS_MESSAGE}${NL}Node version: $version is installed"
else
    ERROR_MESSAGE="${ERROR_MESSAGE}${NL}Node is not installed."
fi

if type -p python3 > /dev/null; then
    version=$(python3 -V | awk '{print $2}')
    SUCCESS_MESSAGE="${SUCCESS_MESSAGE}${NL}Python version: $version is installed"
else
    ERROR_MESSAGE="${ERROR_MESSAGE}${NL}Python is not installed."
fi

if [ -n "$SUCCESS_MESSAGE" ]; then
    echo "$SUCCESS_MESSAGE"
fi
if [ -n "$ERROR_MESSAGE" ]; then
    echo "################ ERROR ###################"
    echo "$ERROR_MESSAGE"
    echo "##########################################"
    exit -1
fi

echo
