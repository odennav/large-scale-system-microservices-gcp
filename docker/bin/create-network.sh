#!/bin/bash

docker network create --subnet=192.168.0.0/16 mynet1

docker network ls
