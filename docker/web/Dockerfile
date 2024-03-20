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

# Set the base image to mongo
FROM ubuntu:22.04

# File Author / Maintainer
MAINTAINER Anurag Yadav

# Install utility tools
RUN apt-get update && \
    apt-get install -y net-tools && \
    apt-get install -y iputils-ping && \
    apt-get install -y dnsutils && \
    apt-get install -y curl
RUN apt-get clean

# Install pythong, django and gunicorn
RUN apt-get update && \
    apt-get install -y python3-pip && \
    pip3 install "django>=3.2,<4" && \
    pip3 install gunicorn

RUN pip3 install requests && \
    pip3 install python-json-logger && \
    pip3 install opentracing jaeger-client

RUN pip3 install django-prometheus

# Copy Python UI code
RUN mkdir /usr/src/PyUI
WORKDIR /usr/src/PyUI
COPY ./image/PyUI.tar.gz /usr/src/PyUI
RUN tar -zxf PyUI.tar.gz && \
    rm -f PyUI.tar.gz

# Create mount point for log files
VOLUME ["/var/log/oms"]

# Change current directory
WORKDIR /usr/src/PyUI

# Expose the default port
EXPOSE 8000

CMD ["/usr/local/bin/gunicorn", "pyui.wsgi:application", "--bind", "0.0.0.0:8000", "--workers", "3"]
