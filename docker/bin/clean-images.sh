#!/bin/bash

docker rmi $(docker images --quiet --filter "dangling=true")

# Use docker prune to clean all images
# docker rmi prune --all
