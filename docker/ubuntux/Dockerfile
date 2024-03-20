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

ARG DEBIAN_FRONTEND=noninteractive

RUN apt update && \
    apt install -y xfce4 && \
    apt install -y tightvncserver

RUN apt install -y openjdk-11-jdk && \
    apt install -y curl && \
    apt install -y visualvm && \
    apt install -y firefox 

RUN cd /usr/share && \
    curl -LO https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.5.tgz && \
    tar -zxf apache-jmeter-*.tgz && \
    rm -f apache-jmeter-*.tgz && \
    mv apache-jmeter-* jmeter && \
    ln -s /usr/share/jmeter/bin/jmeter /usr/bin/jmeter

RUN apt install -y xfce4-terminal && \
    apt install -y vim && \
    apt install -y autocutsel

ENV USER=0

RUN mkdir -p /usr/data/jmeter && \
    mkdir -p /usr/data/shared

COPY ./image/tests/test-plan.jmx \
     ./image/tests/users.csv \
     ./image/tests/products.csv \
     ./image/tests/create-data.sh \
     ./image/run-jmeter.sh \
     /usr/data/jmeter/

COPY ./image/docker-entrypoint.sh /usr/bin/docker-entrypoint.sh

RUN printf "password\npassword\n\n" | vncpasswd
RUN vncserver && vncserver -kill :1

# xfwm4
RUN echo ' \
xfce4-session &; \
xfce4-panel &; \
x-window-manager &; \
xfce4-terminal &; \
autocutsel &; \
' > ~/.vnc/xstartup

echo "xfce4-terminal &" >> ~/.vnc/xstartup && \
    echo "autocutsel &" >> ~/.vnc/xstartup

# Create mount point for data files
VOLUME ["/usr/data/shared"]

EXPOSE 5901 6901

CMD ["/bin/bash"]
ENTRYPOINT ["/usr/bin/docker-entrypoint.sh"]
