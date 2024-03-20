############################################################################
# Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               #
#                                                                          #
# Licensed under the Apache License, Version 2.0 (the "License");          #
# you may not use this file except in compliance with the License.         #
# You may obtain a copy of the License at                                  #
#                                                                          #
#     http:#www.apache.org/licenses/LICENSE-2.0                            #
#                                                                          #
# Unless required by applicable law or agreed to in writing, software      #
# distributed under the License is distributed on an "AS IS" BASIS,        #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
# See the License for the specific language governing permissions and      #
# limitations under the License.                                           #
############################################################################

# Set the base image to ubuntu
FROM ubuntu:22.04

# File Author / Maintainer
MAINTAINER Anurag Yadav

# Install utility tools
RUN apt-get update && \
    apt-get install -y net-tools && \
    apt-get install -y iputils-ping && \
    apt-get install -y dnsutils && \
    apt-get install -y curl

# Install open jdk 11
RUN apt-get install -y software-properties-common \
&& add-apt-repository ppa:openjdk-r/ppa \
&& apt-get update \
&& apt install -y openjdk-11-jdk

RUN apt-get clean

COPY ./image/discovery.jar /usr/war/

COPY ./image/docker-entrypoint.sh /usr/bin/docker-entrypoint.sh

# Create mount point for log files
VOLUME ["/var/log/oms"]

EXPOSE 8761

ENTRYPOINT ["/usr/bin/docker-entrypoint.sh"]
