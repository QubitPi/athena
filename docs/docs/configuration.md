---
title: Configuration
sidebar_position: 2
---

Configuration
=============

The configuration for Athena is implemented using ResourceConfig class programmatically wires up dependencies with a
[BinderFactory].

The configuration does not expose all the settings that can be customized. Some requires overriding of the injected
dependency in [AbstractBinderFactory], which offers the default dependency injection and resource binding.

The required bindings are

- a [FileStore] implementation class
- a [MetaStore] implementation class with dependencies of

  1. a [QueryDataFetcher][DataFetcher] for read operation and
  2. a [MutationDataFetcher][DataFetcher] for write operation

```java
import org.apache.commons.dbcp2.BasicDataSource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import graphql.schema.DataFetcher;

import jakarta.inject.Provider;
import javax.sql.DataSource;

public class MyBinderFactory extends AbstractBinderFactory {

    @Override
    protected Class<? extends FileStore> buildFileStore() {
        return SwiftFileStore.class;
    }

    @Override
    protected Class<? extends MetaStore> buildMetaStore() {
        return GraphQLMetaStore.class;
    }

    @Override
    protected DataFetcher<MetaData> buildQueryDataFetcher() {

    }

    @Override
    protected DataFetcher<MetaData> buildMutationDataFetcher() {

    }
}
```

Any custom bindings can be achieved by overriding the `afterBinding`

```java
@Override
protected void afterBinding(final AbstractBinder abstractBinder) {
    // Custom bindings here...
}
```

Database
--------

A database should be initialized with:

```sql
CREATE DATABASE IF NOT EXISTS Athena;
USE Athena;

CREATE TABLE BOOK_META_DATA (
    id        int NOT NULL AUTO_INCREMENT,
    file_id   VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(8)   NOT NULL,
    PRIMARY KEY (id)
);
```

[AbstractBinderFactory]: https://github.com/QubitPi/athena/blob/master/athena-core/src/main/java/io/github/qubitpi/athena/application/AbstractBinderFactory.java

[BinderFactory]: https://athena.qubitpi.org/apidocs/io/github/qubitpi/athena/application/BinderFactory.html

[DataFetcher]: https://graphql-java.qubitpi.org/documentation/data-fetching/

[MetaStore]: https://athena.qubitpi.org/apidocs/io/github/qubitpi/athena/metastore/MetaStore.html

[FileStore]: https://athena.qubitpi.org/apidocs/io/github/qubitpi/athena/filestore/FileStore.html
