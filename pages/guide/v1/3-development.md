---
layout: doc-guide
group: guide
title: Development
description: Building and Releasing Athena
version: 1
---

{:toc}

Overview
========

The following guide is intended to help developers who maintain or want to make changes to 
the Athena framework.
           
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

Release Versions
----------------

Athena follows [semantic versioning](https://semver.org/) for its releases. Minor and patch versions only have the 
version components of `MAJOR.MINOR.PATCH`.

Major releases are often pre-released prior to the publication of the final version.  Pre-releases have the format of 
`MAJOR.MINOR.PATCH-prCANDIDATE`.  For example, 5.0.0-pr2 is release candidate 2 of the Athena 5.0.0 version.
