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
FROM cassandra:4.1

# File Author / Maintainer
MAINTAINER Anurag Yadav

# Install utility tools
RUN apt-get update && \
    apt-get install -y net-tools && \
    apt-get install -y iputils-ping && \
    apt-get install -y dnsutils && \
    apt-get install -y curl

COPY ./image/docker-entrypoint-second.sh /docker-entrypoint-second.sh
COPY ./image/insert-data.cql /insert-data.cql
COPY ./image/create-schema.cql /create-schema.cql
COPY ./image/check-readiness.sh /check-readiness.sh
COPY ./image/jmx_prometheus_javaagent.jar /opt/cassandra/lib
COPY ./image/jmx-prometheus.yml /etc/cassandra

RUN echo 'JVM_OPTS="$JVM_OPTS -javaagent:/opt/cassandra/lib/jmx_prometheus_javaagent.jar=7070:/etc/cassandra/jmx-prometheus.yml"' >> /etc/cassandra/cassandra-env.sh

ENTRYPOINT ["/docker-entrypoint-second.sh"]
CMD ["cassandra", "-f"]
