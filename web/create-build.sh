#!/bin/bash

npm update

cp -r ./node_modules/bootstrap ./app/static
cp -r ./node_modules/jquery ./app/static
cp -r ./node_modules/js-cookie ./app/static

rm -rf ./node_modules

echo Create PyUI .tar.gz file
rm -f ./logs/* PyUI.tar.gz
tar -zcf PyUI.tar.gz ./app ./db.sqlite3 manage.py pyui

echo Done!!
