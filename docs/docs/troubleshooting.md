---
title: Troubleshooting
sidebar_position: 10
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

The following offers some solutions to common issues when setting up and using Athena. If you come across any issues
that you think belong here, please feel free to contribute.

General
-------

The Athena logs are Jetty logs, so they can be found wherever your Jetty instance stores its logs.

The logs have a lot of information and should help indicate the error.
[More info on logging levels](logging-guidelines)

Checkstyle Error - "Extra lines between braces [RegexpMultiline]"
-----------------------------------------------------------------

This is an Athena-custom checkstyle rule which simple disallow the following code snippet:

```java
    }

}
```

Basically, multiple lines between right curly braces is defined as a checkstyle violation. The error, however, might
still pops up with something _visually_ like this:

```java
    }
}
```

Note that no extra line can be seen in this reported case. The most probably cause might be a shared development
environment where one team member wrote code on Windows, which uses CRLF line endings, and the other uses UNIX/Mac.
We should [replace all CRLF endings with UNIX '\n' endings](https://stackoverflow.com/a/50765523/14312712).
