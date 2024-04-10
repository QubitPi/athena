---
title: Getting Started
sidebar_position: 1
---

[//]: # (Copyright Jiaqi Liu)

[//]: # (Licensed under the Apache License, Version 2.0 &#40;the "License"&#41;;)
[//]: # (you may not use this file except in compliance with the License.)
[//]: # (You may obtain a copy of the License at)

[//]: # (    http://www.apache.org/licenses/LICENSE-2.0)

[//]: # (Unless required by applicable law or agreed to in writing, software)
[//]: # (distributed under the License is distributed on an "AS IS" BASIS,)
[//]: # (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[//]: # (See the License for the specific language governing permissions and)
[//]: # (limitations under the License.)

The easiest way to get started with Athena is to use the [Athena Book App Starter]. The starter bundles all of the
dependencies we will need to stand up a web service. This tutorial uses the starter, and all of the code is
[available here][Athena Book App Starter]. We will deploy and play with this example locally

Docker Compose
--------------

Athena Compose is a tool for setting up and running a full-fledged Athena instance Docker application. With Compose,
an Athena application is backed by a real MySQL meta store and an in-memory OpenStack Swift service. With a single
command, we will be able to create and start all the services from Athena. **It's the quickest approach to get a taste
of Athena**.

Athena Compose works in all environments: production, staging, development, testing, as well as CI workflows. You can
learn more about it from [source code][Athena Book App Starter].

Using Athena Compose is basically a three-step process:

1. Package Athena at project root with `mvn clean package`
2. cd into [compose top directory][Athena Book App Starter] and fire-up `docker compose up`
3. hit Athena at `http://localhost/v1/metadata/graphql?query={metaData(fileId:%221%22){fileName}}` with your favorite
   browser

For more information about the Athena Compose the [Compose file definition][Athena Book App Starter].

Athena Compose has ability for managing the whole lifecycle of an Athena application:

- Start, stop, and rebuild services
- View the status of running services
- Stream the log output of running services
- Run a one-off command on a service

### Extending Athena Compose

Happy with Athena? You can go further with productionizing Athena from
here <img src="https://user-images.githubusercontent.com/16126939/174438007-b9adae25-baf8-42a7-bf39-83786435d397.gif" width="40"/>

If you would like to go from basic Athena Compose setup and changed anything, rebuild it with

```bash
docker compose up --build --force-recreate
```

Athena Compose has been tested with [MySQL 5.7](https://hub.docker.com/_/mysql) connected using
_mysql-connector-java 5.1.38_ within Athena running on [Jetty 9.3](https://hub.docker.com/_/jetty).

:::warning

Please take extra caution with MySQL 8, as some of the features might not work properly on Athena Compose. In
addition, make sure `?autoReconnect=true&useSSL=false` is in connection string. For example,
`jdbc:mysql://db:3306/Athena?autoReconnect=true&useSSL=false`

:::

### MySQL Container (Meta Store)

Athena Compose uses MySQL 5 as the backing meta store, i.e. the database that DataFetcher is talking to for file
metadata.

The MySQL instance is network-reachable at 3306 inside compose and 3305 for host (wo choose 3305 just in case 3306 has
already been occupied)

### Networking in Athena Compose

By default Athena Compose sets up a single
[network](https://docs.docker.com/engine/reference/commandline/network_create/). Both Athena and MySQL container
services join this default network and is both reachable by other containers on that network, and discoverable by them
at a hostname identical to the container name.

For example, inside [docker-compose.yml][docker-compose.yml]

```yaml
services:
  web:
    build: .
    ports:
      - "80:8080"
    depends_on:
      db:
        condition: service_healthy
  db:
    image: "mysql:5.7"
    ports:
      - "3305:3306"
    volumes:
      - "./mysql-init.sql:/docker-entrypoint-initdb.d/mysql-init.sql"
    environment:
      MYSQL_ROOT_PASSWORD: root
    healthcheck:
      test: mysqladmin ping -h localhost -u root -proot
      timeout: 3s
      retries: 3
```

When you run docker compose up, the following happens:

- A network called "athena-example-books" is created.
- An Athena container is created using athena-example-books configuration. It joins the network "athena-example-books"
  under the name "web".
- An MySQL container is created using `db`'s configuration. It joins the network "athena-example-books" under the name
  "db".

Each container can now look up the hostname `web` or `db` and get back the appropriate container's IP address. For
example, web's application code could connect to the URL `mysql://db:3306` and start using the MySQL database.

Build From Source
-----------------

### Creating a Deployable War File

We build the ".war" File first by navigating to athena project root and compile the project

```bash
cd athena/
mvn clean package
```

Successfully executing the command above shall generate a ".war" file under
`athena/athena-examples/athena-example-books/target/athena-example-books-<athena-version>.war`, where
is the version of the athena, for example `1.0.2`, please make sure to replace `<athena-version>` with one of our
release versions.

### Setting Up Jetty

#### Downloading Jetty

At [download page](https://www.eclipse.org/jetty/download.php), pick up a `.tgz` distribution, we will use
"9.4.44.v20210927" release as an example:

![Error loading download-jetty.png](img/download-jetty.png)

#### Installing Jetty

Put the `tar.gz` file into a preferred location as the installation path and extract the Jetty binary using

```bash
tar -czvf jetty-distribution-9.4.44.v20210927.tar.gz
```

#### Dropping the ".war" File into the Jetty "webapp"

```bash
cd jetty-distribution-9.4.44.v20210927/webapps/
mv /path/to/.war .
```

Then rename the war file to "ROOT.war", the reason of which is so that the context path would be root context - `/`,
which is a common industry standard.

:::tip[Setting a Context Path]

The context path is the prefix of a URL path that is used to select the context(s) to which an incoming request is
passed. Typically a URL in a Java servlet server is of the format
`http://hostname.com/contextPath/servletPath/pathInfo`, where each of the path elements can be zero or more "/"
separated elements. If there is no context path, the context is referred to as the **root context**. The root context
must be configured as "/" but is reported as the empty string by the servlet
[API `getContextPath()` method](https://www.eclipse.org/jetty/).

How we set the context path depends on how we deploy the web application (or `ContextHandler`). In this case, we
configure the context path by **naming convention**:

If a web application is deployed using the WebAppProvider of the DeploymentManager without an XML IoC file, then **the
name of the WAR file is used to set the context path**:

- If the WAR file is named "myapp.war", then the context will be deployed with a context path of `/myapp`
- **If the WAR file is named "ROOT.WAR" (or any case insensitive variation), then the context will be deployed with a
  context path of `/`**
- If the WAR file is named "ROOT-foobar.war" (or any case insensitive variation), then the context will be deployed
  with a context path of / and a
  [virtual host](https://www.eclipse.org/jetty/documentation/jetty-9/index.html#configuring-virtual-hosts) of "foobar"

:::

### Starting the Webservice

```bash
cd ../
java -jar start.jar
```

:::tip

To specify the port that container exposes for our app, we could use

```bash
java -jar start.jar -Djetty.port=8081
```

:::

### Firing The First Request

```bash
brew install --cask graphiql
```

[Athena Book App Starter]: https://github.com/QubitPi/athena/tree/master/athena-examples/athena-example-books

[docker-compose.yml]: https://github.com/QubitPi/athena/tree/master/athena-examples/athena-example-books/docker-compose.yml
[athena-demo]: https://github.com/QubitPi/athena/tree/master/athena-examples/athena-example-books
[swagger-ui]: https://swagger.io/tools/swagger-ui/
