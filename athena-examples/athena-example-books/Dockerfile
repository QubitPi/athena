# Copyright Jiaqi Liu
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
FROM jetty:jdk17

LABEL maintainer="Jiaqi (Jack) Liu"
LABEL maintainer-email="jack20220723@gmail.com"

ARG ATHENA_VERSION=1.0-SNAPSHOT

ENV JETTY_WEBAPPS_DIR /var/lib/jetty/webapps

COPY ./target/athena-example-books-$ATHENA_VERSION.war $JETTY_WEBAPPS_DIR/ROOT.war
