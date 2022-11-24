[Athena](https://qubitpi.github.io/athena/) <sup>![Java Version Badge is Missing](https://img.shields.io/badge/Java-11-brightgreen?style=flat-square&logo=OpenJDK&logoColor=white)</sup>
===========================================

> My sincere thanks to [yahoo/fili](https://github.com/yahoo/fili) & [yahoo/elide](https://github.com/yahoo/elide),
> which gave tremendous amount of guidance on design and development of [Athena](https://github.com/QubitPi/athena), and
> to my former employer, Yahoo, who taught me to love software engineering and fundamentally influenced my tech career

<div align="left">

[ ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/QubitPi/athena/Release?logo=github&style=for-the-badge) ](https://github.com/QubitPi/athena/actions/workflows/release.yml)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/QubitPi/athena/master?logo=github&style=for-the-badge)
[ ![Discord](https://img.shields.io/discord/1001320502960324658?logo=discord&logoColor=white&style=for-the-badge) ](https://discord.com/widget?id=1001320502960324658&theme=dark)
[ ![License Badge](https://img.shields.io/badge/Apache%202.0-F25910.svg?style=for-the-badge&logo=Apache&logoColor=white) ](https://www.apache.org/licenses/LICENSE-2.0)

</div>

<a href="https://sonarcloud.io/summary/new_code?id=QubitPi_athena"><img align="left" width="15%" alt="SonarCloud" src="https://sonarcloud.io/api/project_badges/quality_gate?project=QubitPi_athena"></a>

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=coverage)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=bugs)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=QubitPi_athena&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=QubitPi_athena)

Athena is a Java library that lets you setup object storage webservice with minimal effort. Athena is meant to be
specialized on managing **files**, such as books, videos, and photos. It supports object storage through two variants of
APIs:

<a href="https://www.bilibili.com/video/BV16T4y1v78U?share_source=copy_web&vd_source=7f40ee8f7150cba61ecdf3d901bbad42">
<img align="right" width="20%" alt="崩崩崩洗脑循环" src="https://user-images.githubusercontent.com/16126939/182336015-4f1721c1-a975-4348-9919-3a45c0f891ef.gif">
</a>

* A [JSON API](https://jsonapi.org/) for uploading and downloading files
* A [GraphQL](https://graphql.com/) API for reading file metadata, including
    - File name
    - File type
    - etc.

Athena has **first-class support for [OpenStack Swift](https://docs.openstack.org/swift/latest/) and
[Hadoop HDFS](https://hadoop.apache.org/)** file storage back-end, but Athena's flexible pipeline-style architecture can
handle nearly any back-end for data storage, such as
[S3](https://qubitpi.github.io/athena/pages/guide/v1/09-filestores.html#custom-stores).

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


Quick Start
-----------

Athena comes with a pre-configured [example application](https://qubitpi.github.io/athena/pages/guide/v1/08-start.html)
to help you get started and serve as a jumping-off-point for building your own web service using Athena. The example
application lets you upload and download books you love to read, and picks up where
[Swift's quick-start tutorial](https://qubitpi.github.io/athena/pages/guide/v1/10-local-swift.html) leaves off.


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


Documentation
-------------

More information about Athena can be found [here](https://qubitpi.github.io/athena/)


Binaries (How to Get It) [ ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/QubitPi/athena/Release?logo=github&style=for-the-badge) ](https://github.com/QubitPi/athena/actions/workflows/release.yml)
------------------------

Binaries for Athena are stored in [GitHub Packages](https://github.com/QubitPi?tab=packages&repo_name=athena). To
install the packages from there, edit the
[pom.xml](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) file to include the package as a
dependency. Dependency information for each Athena sub-module can be found at their corresponding package page.
[For example](https://github.com/QubitPi/athena/packages/1557510):

```xml
<dependency>
    <groupId>com.qubitpi.athena</groupId>
    <artifactId>athena-core</artifactId>
    <version>x.y.z</version>
</dependency>
```

Next, include the following snippet in the project's POM

```xml
<project>
    ...

    <repositories>
        <repository>
            <id>download-from-github-qubitpi</id>
            <name>Download QubitPi's GitHub Packages</name>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <url>https://maven.pkg.github.com/QubitPi/athena</url>
        </repository>
    </repositories>
</project>
```

Lastly, you would need an access token to install Athena packages. Athena uses a personal access token (PAT), with
`packages:read` scope, to authenticate to GitHub Packages. Your project can authenticate to GitHub Packages with Apache
Maven by editing your `~/.m2/settings.xml` file to include the personal access token:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <activeProfiles>
        <activeProfile>download-from-github-qubitpi</activeProfile>
    </activeProfiles>

    <profiles>
        <profile>
            <id>download-from-github-qubitpi</id>
            <repositories>
                <repository>
                    <id>download-from-github-qubitpi</id>
                    <url>https://maven.pkg.github.com/qubitpi/athena</url>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <servers>
        <server>
            <id>download-from-github-qubitpi</id>
            <username>anybody</username>
            <!-- https://stackoverflow.com/a/64443958/14312712 -->
            <password>a personal access token with at least packages:read scope</password>
        </server>
    </servers>
</settings>
```


License
-------

The use and distribution terms for [Athena](https://qubitpi.github.io/athena/) are covered by the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

<div align="center">
    <a href="https://opensource.org/licenses">
        <img align="center" width="50%" alt="License Illustration" src="https://github.com/QubitPi/QubitPi/blob/master/img/apache-2.png?raw=true">
    </a>
</div>
