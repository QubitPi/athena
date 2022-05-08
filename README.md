[ ![Project Management](https://img.shields.io/badge/Project%20Management-0052CC?style=for-the-badge&logo=trello&logoColor=white) ](https://trello.com/b/ARStTLRb)
[![License Badge](https://img.shields.io/badge/Apache%202.0-F25910.svg?style=for-the-badge&logo=Apache&logoColor=white) ](https://www.apache.org/licenses/LICENSE-2.0)

Athena
======

> My sincere thanks to [yahoo/fili](https://github.com/yahoo/fili) & [yahoo/elide](https://github.com/yahoo/elide),
> which gave tremendous amount of guidance on design and development of Athena, and to my former employer, Yahoo, who 
> taught me to love software engineering and fundamentally influenced my tech career. ❤️ 

Athena is a Java library that lets you setup object storage webservice with minimal effort. Athena is meant to be
specialized on managing **files**, such as books, videos, and photos. It supports object storage through two variants of
APIs:

* A [GraphQL](https://graphql.org/) API for reading file metadata.
* A [JSON API](https://jsonapi.org/) for uploading and downloading files

> The GraphQL specification does contain part on file upload/download. Athena binds to the specification by separating
> endpoints described above

Athena has **first-class support for [OpenStack Swift](https://docs.openstack.org/swift/latest/)** as a file storage
back-end, but Athena's flexible pipeline-style architecture can handle nearly any back-end for data storage, such as 
[S3](https://github.com/QubitPi/athena/wiki/Implementing-S3-FileStore).

Object storage (also known as object-based storage) is a computer data storage architecture that manages data as
objects, as opposed to other storage architectures like file systems which manages data as a file hierarchy, and block
storage which manages data as blocks within sectors and tracks.

Each object (i.e. file), in Athena, typically includes:

* **the data itself**,
* **a variable amount of metadata**, and
* **a globally unique identifier**

Athena allow retention of massive amounts of unstructured data in which data is _written once and read once (or many
times)_. It is used for purposes such as storing objects like videos and photos.

Athena, however, is not intended for transactional data and _it does not support the locking and sharing mechanisms
needed to maintain a single, accurately updated version of a file_.

Features
--------

### Abstraction of storage

**One of the design principles of Athena is to abstract lower layers of storage away from the administrators and
applications**. Thus, data is exposed and managed as objects instead of files or blocks. They do not have to perform
lower-level storage functions like constructing and managing logical volumes to utilize disk capacity or setting RAID
levels to deal with disk failure.

Athena also allows the addressing and identification of individual objects by more than just file name and file
path. Athena adds **a unique identifier** across the entire system, to support much larger namespaces and eliminate name
collisions.

### Inclusion of Rich Custom Metadata within the Object

Athena explicitly **separates file metadata from data** to support additional capabilities. As opposed to fixed
metadata in file systems (filename, creation date, type, etc.), Athena provides for full function, custom,
object-level metadata in order to:

* Capture application-specific or user-specific information for better indexing purposes
* Support data-management policies (e.g. a policy to drive object movement from one storage tier to another)
* Centralize management of storage across many individual nodes and clusters
* Optimize metadata storage (e.g. encapsulated, database or key value storage) and caching/indexing (when authoritative
  metadata is encapsulated with the metadata inside the object) independently from the data storage (e.g. unstructured
  binary storage)

### Programmatic Data Management

Athena provides programmatic interfaces to allow applications to manipulate data. At the base level, this includes
create, read, and delete (CRUD) functions for basic read, write and delete operations. The API implementations are
REST-based, allowing the use of many standard HTTP calls.

## Quick Start

Athena comes with a pre-configured [example application](./athena-example/athena-example-books) to help you get started
and serve as a jumping-off-point for building your own web service using Athena. The example application lets you upload and download books you love to read, and picks up where
[Swift's quick-start tutorial](https://github.com/QubitPi/athena/wiki/Standup-A-Local-Swift-Instance) leaves off.

Design
------

File store and meta store do not communicate with eath other. This ensures the simplicity and maintainability of Athena.

## License

The use and distribution terms for this software are covered by the Apache License, Version 2.0
( http://www.apache.org/licenses/LICENSE-2.0.html ).