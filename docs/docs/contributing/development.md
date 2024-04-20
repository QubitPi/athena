---
title: Development
sidebar_position: 1
---

[//]: # (Copyright 2024 Jiaqi Liu)

[//]: # (Licensed under the Apache License, Version 2.0 &#40;the "License"&#41;;)
[//]: # (you may not use this file except in compliance with the License.)
[//]: # (You may obtain a copy of the License at)

[//]: # (    http://www.apache.org/licenses/LICENSE-2.0)

[//]: # (Unless required by applicable law or agreed to in writing, software)
[//]: # (distributed under the License is distributed on an "AS IS" BASIS,)
[//]: # (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[//]: # (See the License for the specific language governing permissions and)
[//]: # (limitations under the License.)

Athena is developed in [Jersey](https://eclipse-ee4j.github.io/jersey/) framework.

**NOTE:** In case you are not familiar with Jersey, it is a parallel technology with "Spring Boot framework". **Athena
offers absolutely NO support for Spring** and will remain as an exclusive Jersey application in the future, because
Jersey, alone with its backing technology [HK2](https://javaee.github.io/hk2/), is the reference-implementation of
JSR-370 (and HK2, JSR-330) _standards_ while Spring is not.

By "having no support for Spring", Athena means the following:

1. Athena DOES NOT, AND WILL NOT, run as a Spring Boot Webservice
2. Athena has ABSOLUTE ZERO direct-dependency from Spring
3. Athena runs in NON-SPRING containers, such as Jetty

_Athena rejects any conducts that violate the 3 rules above. NO EXCEPTION_.

Overview
--------

The following guide is intended to help developers who maintain or want to make changes to the Athena framework.

Building
--------

Athena is built using Maven. Because Athena is a mono-repo with interdependencies between modules, it is recommended to
fully build and install the project at least once:

```bash
mvn clean install
```

Thereafter, individual modules can be built whenever making changes to them. For example, the following command would
rebuild only athena-core:

```bash
mvn clean install -f athena-core
```

Pull requests and release builds leverage GitHub Action. PR builds simply run the complete build along with code
coverage.

Running Webservice in Standalone Jetty
--------------------------------------

### Download Jetty

For JDK **17**, which is the version JWT runs on, it's been tested that Jetty _11.0.15_ worked. Hence, we will use
["11.0.15" release](https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-home/11.0.15/jetty-home-11.0.15.tar.gz) as
an example:

![Error loading download-jetty.png](img/download-jetty.png)

Put the `tar.gz` file into a location of your choice as the installation path and extract the Jetty binary using

```bash
tar -xzvf jetty-home-11.0.15.tar.gz
```

The extracted directory *jetty-home-11.0.15* is the Jetty distribution. We call this directory **$JETTY_HOME**, which
should not be modified.

### Setting Up Standalone Jetty

Our [WAR file](#building) will be dropped to a directory where Jetty can pick up and run. We call this directory
**$JETTY_BASE**, which is usually different from the _$JETTY_HOME_. The _$JETTY_BASE_ also contains container runtime
configs. In short, the Standalone Jetty container will be setup with

```bash
export JETTY_HOME=/path/to/jetty-home-11.0.15
mkdir -p /path/to/jetty-base
cd /path/to/jetty-base
java -jar $JETTY_HOME/start.jar --add-module=annotations,server,http,deploy,servlet,webapp,resources,jsp
```

where `/path/to/` is the _absolute_ path to the directory containing the `jetty-home-11.0.15` directory

The `--add-module=annotations,server,http,deploy,servlet,webapp,resources,jsp` is how we configure the Jetty
container.

Lastly, drop the [WAR file](#building) into **/path/to/jetty-base/webapps** directory and rename the WAR file to
**ROOT.war**:

```bash
mv /path/to/war-file /path/to/jetty-base/webapps/ROOT.war
```

### Running Webservice

```bash
java -jar $JETTY_HOME/start.jar
```

The webservice will run on port **8080**, and you will see the data you inserted

Release Versions
----------------

Athena follows [semantic versioning](https://semver.org/) for its releases. Minor and patch versions only have the
version components of `MAJOR.MINOR.PATCH`.

Major releases are often pre-released prior to the publication of the final version.  Pre-releases have the format of
`MAJOR.MINOR.PATCH-prCANDIDATE`.  For example, 5.0.0-pr2 is release candidate 2 of the Athena 5.0.0 version.
