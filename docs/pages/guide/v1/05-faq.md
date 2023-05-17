---
layout: doc-guide
group: guide
title: FAQ
description: Frequently Asked Questions about Athena
version: 1
---

I Use IntelliJ. Is There Any Way to Easily Sync up with Athena's Code styles
----------------------------------------------------------------------------

For the moment, we have distilled the most important code style conventions with respect to Athena's code as IntelliJ
settings. If you are using IntelliJ, you may import these code style settings by importing the
[Athena-Project-intellij-code-style.xml](https://github.com/QubitPi/athena/blob/master/Athena-Project-intellij-code-style.xml)
file in the root of the repo. The setting for the project will appear as a new Scheme named *Athena-Project* under your
`Editor -> Code Style` section.

Alternatively, you might check the xml file that is included in the jar and map its settings to your development
environment.

### Troubleshooting

#### Checkstyle Error - "Extra lines between braces [RegexpMultiline]"

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
