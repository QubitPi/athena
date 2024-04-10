---
title: Development
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

Release Versions
----------------

Athena follows [semantic versioning](https://semver.org/) for its releases. Minor and patch versions only have the
version components of `MAJOR.MINOR.PATCH`.

Major releases are often pre-released prior to the publication of the final version.  Pre-releases have the format of
`MAJOR.MINOR.PATCH-prCANDIDATE`.  For example, 5.0.0-pr2 is release candidate 2 of the Athena 5.0.0 version.
