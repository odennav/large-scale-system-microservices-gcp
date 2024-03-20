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
FROM fluentd:v1.14-1

# File Author / Maintainer
MAINTAINER Anurag Yadav

USER root

# Workaround to fix elasticsearch bug in elasticsearch version 8
RUN ["gem", "install", "elasticsearch", "--no-document", "--version", "< 8"]

RUN ["gem", "install", "fluent-plugin-elasticsearch", "--no-document", "--version", "5.0.3"]
RUN ["gem", "install", "fluent-plugin-detect-exceptions"]
RUN ["gem", "install", "fluent-plugin-concat"]
RUN ["gem", "install", "fluent-plugin-rewrite-tag-filter"]
