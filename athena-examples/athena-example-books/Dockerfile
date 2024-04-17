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
FROM ubuntu:22.04

LABEL maintainer="Jiaqi (Jack) Liu"
LABEL maintainer-email="jack20220723@gmail.com"

ARG WS_VERSION=1.0-SNAPSHOT

ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64

ENV JETTY_VERSION 11.0.15
ENV JETTY_BASE /jetty-base
ENV JETTY_HOME /jetty-home-$JETTY_VERSION
ENV JETTY_WEBAPPS_DIR $JETTY_BASE/webapps

RUN apt update
RUN apt upgrade -y
RUN apt install software-properties-common -y
RUN apt install wget

# Install JDK 17 - https://www.rosehosting.com/blog/how-to-install-java-17-lts-on-ubuntu-20-04/
RUN apt update -y
RUN apt install openjdk-17-jdk -y

# Install and configure Jetty (for JDK 17) container
RUN wget https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-home/$JETTY_VERSION/jetty-home-$JETTY_VERSION.tar.gz
RUN tar -xzvf jetty-home-$JETTY_VERSION.tar.gz
RUN rm jetty-home-$JETTY_VERSION.tar.gz
RUN mkdir jetty-base
RUN cd jetty-base && java -jar $JETTY_HOME/start.jar --add-module=annotations,server,http,deploy,servlet,webapp,resources,jsp

COPY ./target/athena-example-books-$WS_VERSION.war $JETTY_WEBAPPS_DIR/ROOT.war

COPY ./Dockerfile-startup.sh /Dockerfile-startup.sh
CMD [ "/Dockerfile-startup.sh" ]
