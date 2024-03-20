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

# Set the base image to nginx
FROM nginx:1.23

# File Author / Maintainer
MAINTAINER Anurag Yadav

# Install utility tools
RUN apt-get update
RUN apt-get install -y net-tools && \
    apt-get install -y iputils-ping && \
    apt-get install -y curl
RUN apt-get clean

# Create mount point for log files
VOLUME ["/var/log/oms"]

# Copy nging conf
COPY ./image/nginx.conf /etc/nginx/conf.d/default.conf

COPY ./image/docker-entrypoint.sh /usr/bin/docker-entrypoint.sh

EXPOSE 80

# Copy Python UI code
RUN mkdir /usr/src/PyUI
COPY ./image/PyUI.tar.gz /usr/src/PyUI

WORKDIR /usr/src/PyUI
RUN tar -zxf PyUI.tar.gz && \
    rm -f PyUI.tar.gz    

# Set default container command
ENTRYPOINT ["/usr/bin/docker-entrypoint.sh"]
