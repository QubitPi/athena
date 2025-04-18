---
slug: /filestore
title: File Stores
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

A file store is responsible for:

1. reading and writing files (.mp3, .pdf, etc.) to/from an object store. Files of the following types are supported by
   Athena

   - PDF
   - MP4
   - .txt file

2. providing "transactions" that make all file operations atomic in a single request.
3. declaring the native object store client it delegates persistence operations to.

If a file store implementation is unable to handle a file InputStream, Athena pushes these responsibilities to the
object store.

Included Stores
---------------

Athena comes bundled with a number of file stores:

1. Swift Store - A file store that can map operations on a file to an underlying OpenStack Swift API. Athena has
   explicit support for Swift
2. HDFS Store - File is persisted on Hadoop HDFS.

:::tip

It is assumed that the "HDFS Store" means a **single-cluster** HDFS. However, the Athena architecture does not preclude
implementing a multi-cluster HDFS store

:::

Stores can be included through the following artifact dependencies:

### Swift Store

```xml
<dependency>
    <groupId>io.github.qubitpi.athena</groupId>
    <artifactId>athena-filestore-swift</artifactId>
    <version>${version.athena}</version>
</dependency>
```

### HDFS Store

```xml
<dependency>
    <groupId>io.github.qubitpi.athena</groupId>
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

        s3client.putObject(
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
}
```

Multiple Stores
---------------

A common pattern in Athena is the need to support multiple file stores. Typically, one file store manages most files,
but some others may require a different object storage backend or have other needs to specialize the behavior of the
store. The multiplex store in Athena manages multiple stores - delegating calls to the appropriate store which is
responsible for a particular file.

This is a [feature](https://trello.com/c/bHwNl4sk) yet to be offered soon.
