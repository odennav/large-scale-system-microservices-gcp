#!/bin/bash

function verify_success {
    if [ $1 == 0 ]; then
	echo "-- Build succeeded for $2 --"
    else
	echo "-- Build failed for $2 --"
	exit -1;
    fi
}

npm update
verify_success $? "npm update"

rm -rf build

npm run build
verify_success $? "npm run build"

pushd build

echo Create reactapp.tar.gz file
rm -f ./reactapp.tar.gz ./build/reactapp.tar.gz && \
    tar -zcf reactapp.tar.gz ./*
verify_success $? "gzip reactapp"

mv reactapp.tar.gz ..

popd

echo Done building react app!!
