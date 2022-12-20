---
layout: doc-guide
group: guide
title: File Stores
description: Configuring Athena with Different File Stores and Databases
version: 1
---

A file store is responsible for:

1. reading and writing files (.mp3, .pdf, etc.) to/from an object store.
2. providing "transactions" that make all file operations atomic in a single request.
3. declaring the native object store client it delegates persistence operations to.

If a file store implementation is unable to handle a file InputStream, Athena pushes these responsibilities to the
object store.

1. Contents
{:toc}

Included Stores
---------------

Athena comes bundled with a number of file stores:

1. Swift Store - A file store that can map operations on a file to an underlying OpenStack Swift API. Athena has
   explicit support for Swift
2. HDFS Store - File is persisted on Hadoop HDFS.

> 📋 It is assumed that the "HDFS Store" means a **single-cluster** HDFS. However, the Athena architecture does not 
> preclude implementing a multi-cluster HDFS store

Stores can be included through the following artifact dependencies:

### Swift Store

```xml
<dependency>
    <groupId>com.qubitpi.athena</groupId>
    <artifactId>athena-filestore-swift</artifactId>
    <version>${version.athena}</version>
</dependency>
```

### HDFS Store

```xml
<dependency>
    <groupId>com.qubitpi.athena</groupId>
    <artifactId>athena-filestore-hdfs</artifactId>
    <version>${version.athena}</version>
</dependency>
```

Overriding the Store
--------------------

To change the store, override the `FileStore` binding. For example, to use a store called `SomeCustomFileStore`:

```java
public class AppBinderFactory extends AbstractBinderFactory {

    ...

    @Override
    protected Class<? extends FileStore> buildFileStore() {
        return SomeCustomFileStore.class;
    }
}
```

Custom Stores
-------------

Custom stores can be written by implementing the `FileStore` interface. Take Amazon S3 for instance

```java
@Singleton
public class S3FileStore implements FileStore {

    private final AmazonS3 s3client;
    private final FileIdGenerator fileIdGenerator;

    @Inject
    public S3FileStore(@NotNull final AmazonS3 s3client, @NotNull final FileIdGenerator fileIdGenerator) {
        this.s3client = Objects.requireNonNull(s3client);
        this.fileIdGenerator = Objects.requireNonNull(fileIdGenerator);
    }

    @Override
    public String upload(final File file) {
        final String fileId = fileIdGenerator.apply(file);

        getS3client().putObject(
                file.getMetaData().getFileType().name(),
                fileId,
                file.getFileContent(),
                new ObjectMetadata()
        );

        return fileId;
    }

    @Override
    public InputStream download(final String fileId) {
       return getS3client().getObject(...);
    }

    @NotNull
    private AmazonS3 getS3client() {
        return s3client;
    }
}
```

Multiple Stores
---------------

A common pattern in Athena is the need to support multiple file stores. Typically, one file store manages most files,
but some others may require a different object storage backend or have other needs to specialize the behavior of the
store. The multiplex store in Athena manages multiple stores - delegating calls to the appropriate store which is 
responsible for a particular file.

This is a [feature](https://trello.com/c/bHwNl4sk) yet to be offered soon.
