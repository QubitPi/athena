[Athena][Athena Home] <sup>![Java Version Badge][Java Version Badge]</sup>
==========================================================================

> My sincere thanks to [yahoo/fili](https://github.com/yahoo/fili) & [yahoo/elide](https://github.com/yahoo/elide),
> which gave tremendous amount of guidance on design and development of [Athena](https://github.com/QubitPi/athena),
> and to my former employer, Yahoo, who taught me to love software engineering and fundamentally influenced my tech
> career

[![GitHub Workflow Status][GitHub Workflow Status]](https://github.com/QubitPi/athena/actions/workflows/ci-cd.yml)
![Last Commit](https://img.shields.io/github/last-commit/QubitPi/athena/master?logo=github&style=for-the-badge)
[![Discord][Discord]](https://discord.com/widget?id=1001320502960324658&theme=dark)
[![License Badge]](https://www.apache.org/licenses/LICENSE-2.0)

Athena is a Java library that lets you set up object storage webservice with minimal effort. Athena is meant to be
specialized on managing **files**, such as books, videos, and photos. It supports object storage through two variants of
APIs:

- A [JSON API](https://jsonapi.org/) for uploading and downloading files
- A [GraphQL](https://graphql.com/) API for reading file metadata, including

    - File name
    - File type
    - etc.

Athena has **first-class support for [OpenStack Swift][OpenStack Swift] and [Hadoop HDFS][Hadoop HDFS]** file storage
back-ends, but Athena's flexible pipeline-style architecture can handle nearly any back-end for data storage, such as
[S3][S3 File Store].

Object storage (also known as object-based storage) is a computer data storage architecture that manages data as
objects, as opposed to other storage architectures like file systems which manages data as a file hierarchy, and block
storage which manages data as blocks within sectors and tracks.

Each object (i.e. file), in Athena, typically includes:

- **the data itself**,
- **a variable amount of metadata**, and
- **a globally unique identifier**

Athena allow retention of massive amounts of unstructured data in which data is _written once and read once (or many
times)_. It is used for purposes such as storing objects like videos and photos.

Athena, however, is not intended for transactional data and _ does not support the locking and sharing mechanisms
needed to maintain a single, accurately updated version of a file_.

Quick Start
-----------

Athena comes with a pre-configured [example application][example application] to help you get started and serve as a
jumping-off-point for building your own web service using Athena. The example application lets you upload and download
books you love to read, and picks up where [Swift's quick-start tutorial][Swift's quick-start tutorial] leaves off.

Features
--------

### Storage Abstraction

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

- Capture application-specific or user-specific information for better indexing purposes
- Support data-management policies (e.g. a policy to drive object movement from one storage tier to another)
- Centralize management of storage across many individual nodes and clusters
- Optimize metadata storage (e.g. encapsulated, database or key value storage) and caching/indexing (when authoritative
  metadata is encapsulated with the metadata inside the object) independently from the data storage (e.g. unstructured
  binary storage)

### Programmatic Data Management

Athena provides programmatic interfaces to allow applications to manipulate data. At the base level, this includes
create, read, and delete (CRUD) functions for basic read, write and delete operations. The API implementations are
REST-based, allowing the use of many standard HTTP calls.

Documentation
-------------

More information about Athena can be found [here](https://qubitpi.github.io/athena/)

License
-------

The use and distribution terms for [Athena][Athena Home] are covered by the
[Apache License, Version 2.0][Apache License, Version 2.0].

<div align="center">
    <a href="https://opensource.org/licenses">
        <img align="center" width="50%" alt="License Illustration" src="https://github.com/QubitPi/QubitPi/blob/master/img/apache-2.png?raw=true">
    </a>
</div>

[Apache License, Version 2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
[Athena Home]: https://athena.qubitpi.org/

[Discord]: https://img.shields.io/discord/1001320502960324658?logo=discord&logoColor=white&style=for-the-badge

[example application]: https://qubitpi.github.io/athena/pages/guide/v1/08-start.html

[GitHub Workflow Status]: https://img.shields.io/github/actions/workflow/status/QubitPi/athena/ci-cd.yml?branch=master&logo=github&style=for-the-badge
[GitHub Workflow Status sup]: https://img.shields.io/github/actions/workflow/status/QubitPi/athena/ci-cd.yml?branch=master&logo=github&style=flat-square

[Hadoop HDFS]: https://qubitpi.github.io/hadoop/

[Java Version Badge]: https://img.shields.io/badge/Java-11-brightgreen?style=flat-square&logo=OpenJDK&logoColor=white

[License Badge]: https://img.shields.io/badge/Apache%202.0-F25910.svg?style=for-the-badge&logo=Apache&logoColor=white

[OpenStack Swift]: https://qubitpi.github.io/openstack-swift/

[S3 File Store]: https://qubitpi.github.io/athena/pages/guide/v1/09-filestores.html#custom-stores
