---
title: Testing
sidebar_position: 2
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

Groovy Spock
------------

We're _big_ believers in testing our code, both for correctness, as well as to ensure that changes don't unintentionally
break existing contracts unintentionally. For example, we rely heavily on the [Spock]
framework for our backend service tests, and see a lot of benefit from it's conciseness, built-in
[mocking framework], and the fact that it uses [Groovy].

We also strive for very high-quality code, with the belief that quality code is easier to maintain, easier to
understand, and has fewer bugs. To help keep the quality bar high. For instance we have an automated style checker
([Checkstyle]) in our Maven-based projects with rules that _should_ catch most of the common style issues.

Servlet Testing
---------------

:::tip

The design of athena-core tests, servlet tests in particular, draws extensively from
[fili](https://github.com/yahoo/fili/blob/master/fili-core/src/test/java/com/yahoo/bard/webservice/application/JerseyTestBinder.java)

One noticeable deviation is that since some of Fili's classes have made it possible for themselves to be mutable,
which Athena doesn't do, the stubbing is defined not on these classes, but on
[ApplicationState](../../../athena-core/src/test/java/io/github/qubitpi/athena/application/ApplicationState.java), which
is a modified adaption of Fili ApplicationState

:::

[Servlet-related testing](https://github.com/QubitPi/athena/tree/master/athena-core/src/main/java/io/github/qubitpi/athena/web/endpoints)
is carried out using
[Jersey Test Framework](https://qubitpi.github.io/jersey/test-framework.html).

![Error loading class-diagram.png](img/class-diagram.png)

Each
[`***ServletSpec.groovy`](https://github.com/QubitPi/athena/tree/master/athena-core/src/test/groovy/io/github/qubitpi/athena/web/endpoints)
follows the following pattern to setup, run, and shutdown tests:

### 1. Initializing ApplicationState

Test specs initializes test data and mocking through
[ApplicationState](../../../athena-core/src/test/java/io/github/qubitpi/athena/application/ApplicationState.java) in
`setup()`

```groovy
def setup() {
    ApplicationState applicationState = new ApplicationState();
    applicationState.metadataByFileId = ...
    applicationState.queryFormatter = ...
    applicationState.mutationFormatter = ...

    ...
}
```

- `applicationState.metadataByFileId` initializes GraphQL DataFetcher data
- `queryFormatter` transforms a (file ID, metadata field list) pair to a native GraphQL query
- `mutationFormatter` transforms a (file ID, metadata object) pair to a native GraphQL query that persists a new
  metadata to database (or just in-memory that usually suffices in testing scenarios)

### 2. Creating Test Harness

```groovy
def setup() {
    ...

    jerseyTestBinder = new JerseyTestBinder(true, applicationState, ***Servlet.class)
}
```

Executing the statement above will start a [Grizzly container](https://javaee.github.io/grizzly/). After that all Athena
endpoints are ready to receive test requests.

:::tip

When writing tests for
[FileServlet](../../../athena-core/src/main/java/io/github/qubitpi/athena/web/endpoints/FileServlet.java), make sure
`MultiPartFeature.class` is also passed in as a resource class since the file uploading involves a separate Jersey
component enabled by it. For example:

```java
jerseyTestBinder = new BookJerseyTestBinder(true, FileServlet.class, MultiPartFeature.class)
```

:::

The first boolean argument (`true`) is a flag to indicate whether or not, on executing the statement, servlet container
starts immediately. If we would like to defer the startup, change that to `false` and manually start the container later
by

```groovy
jerseyTestBinder.start()
```

Internally
[JerseyTestBinder](../../../athena-core/src/test/java/io/github/qubitpi/athena/application/JerseyTestBinder.java) sets
[TestBinderFactory](../../../athena-core/src/test/java/io/github/qubitpi/athena/application/TestBinderFactory.java) to
bind those data and behaviors into the actual test

:::note

The [JerseyTestBinder](../../../athena-core/src/test/java/io/github/qubitpi/athena/application/JerseyTestBinder.java)
creates separate container for each test. Setup method is named `setup()` and teardown method `cleanup()` by Groovy
Spock convention.

:::

### 3. Running Tests

To send test request in order to test endpoints, use `JerseyTestBinder.makeRequest` method, which returns a native
javax rs ws request object:

```groovy
def "File meta data can be accessed through GraphQL GET endpoint"() {
    when: "we get meta data via GraphQL GET"
    String actual = jerseyTestBinder.makeRequest(
            "/metadata/graphql",
            [query: URLEncoder.encode("""{metaData(fileId:"$FILE_ID"){fileName\nfileType}}""", "UTF-8")]
    ).get(String.class)

    then: "the response contains all requested metadata info without error"
    new JsonSlurper().parseText(actual) == new JsonSlurper().parseText(expectedMultiFieldMetadataResponse())
}
```

### 4. Tearing Down Tests

The teardown shuts down test container as well as cleaning up all ApplicationStates we defined in
[step 1](#1-initializing-applicationstate)

```groovy
def cleanup() {
    // Release the test web container
    jerseyTestBinder.tearDown()
}
```

Troubleshooting
---------------

### Adding Custom Resources to ResourceConfig

```bash
java.lang.IllegalStateException: org.glassfish.jersey.server.model.ModelValidationException: Validation of the
application resource model has failed during application initialization.
[FATAL] No injection source found for a parameter of type public javax.ws.rs.core.Response

...

Caused by: org.glassfish.jersey.server.model.ModelValidationException: Validation of the application resource model has
failed during application initialization.
```

Athena uses ResourceConfig type for configuration. We need to register the `MultiPartFeature`. Instead of using
[Athena ResourceConfig](../../../athena-core/src/main/java/io/github/qubitpi/athena/application/ResourceConfig.java),
servlet test spec configures with the native
[Jersey ResourceConfig](https://github.com/eclipse-ee4j/jersey/blob/master/core-server/src/main/java/org/glassfish/jersey/server/ResourceConfig.java).
The reason is so that we could bind certain resource classes that we only need in a test spec to enhance test
efficiency.

Athena ResourceConfig registers MultiPartFeature by default, whereas Jersey ResourceConfig does not. We could register
this resource as an extra resource class using

```groovy
jerseyTestBinder = new JerseyTestBinder(true, applicationState, FileServlet.class, MultiPartFeature.class)
```

Note that along with the `FileServlet` resource that's going to be registered and tested, `MultiPartFeature` will also
got registered by Jersey ResourceConfig.

Database
--------

Database-related tests contain 2 parts

1. Groovy Spock unit tests on

   - [Injected Query DataFetcher](../../../athena-examples/athena-example-books/src/test/groovy/io/github/qubitpi/athena/example/books/application/SQLQueryDataFetcherSpec.groovy)
   - [Injected Mutation DataFetcher](../../../athena-examples/athena-example-books/src/test/groovy/io/github/qubitpi/athena/example/books/application/SQLMutationDataFetcherSpec.groovy)

2. Live DB tests on endpoints

   - In [file servlet endpoint test](../../../athena-examples/athena-example-books/src/test/groovy/io/github/qubitpi/athena/example/books/web/endpoints/FileServletSpec.groovy)
     and
     [meta data servlet endpoint test](../../../athena-examples/athena-example-books/src/test/groovy/io/github/qubitpi/athena/example/books/web/endpoints/MetaServletSpec.groovy),
     [Flyway migration](../../../athena-examples/athena-example-books/src/test/groovy/io/github/qubitpi/athena/example/books/application/SQLDBResourceManager.groovy)
     injects real data into a Derby in-meomroy SQL DB
   - The Derby data is injected via a shared [DBCP DataSource](#reference---apache-commons-dbcp2) declared in
     [application BinderFactory](../../../athena-examples/athena-example-books/src/main/java/io/github/qubitpi/athena/example/books/application/BooksBinderFactory.java)
   - The application resource is set alive through
      [JerseyTestBinder](../../../athena-examples/athena-example-books/src/test/java/io/github/qubitpi/athena/example/books/application/BookJerseyTestBinder.java)

### Reference - Apache Commons DBCP2

For testing Book example application, we use Derby's in-memory database facility, which resides completely in main
memory, not in the file system.

In addition, we use [Apache DBCP 2](https://commons.apache.org/proper/commons-dbcp/) to provide
[DataSource](https://gitbox.apache.org/repos/asf?p=commons-dbcp.git;a=blob_plain;f=doc/BasicDataSourceExample.java;hb=HEAD)
pointing at the in-memory Derby instance.

### Derby

Derby was meant to be used only in tests and, hence, must be imported in test scope only

[Checkstyle]: http://checkstyle.sourceforge.net/

[Groovy]: http://www.groovy-lang.org/

[mocking framework]: http://spockframework.org/spock/docs/1.1-rc-2/interaction_based_testing.html

[Spock]: http://spockframework.org/
