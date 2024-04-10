---
title: FAQ
sidebar_position: 9
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

I Use IntelliJ. Is There Any Way to Easily Sync up with Athena's Code styles
----------------------------------------------------------------------------

For the moment, we have distilled the most important code style conventions with respect to Athena's code as IntelliJ
settings. If you are using IntelliJ, you may import these code style settings by importing the
[Athena-Project-intellij-code-style.xml] file in the root of the repo. The setting for the project will appear as a new
Scheme named *Athena-Project* under IntelliJ's `Editor -> Code Style` section.

Alternatively, you might check the xml file that is included in the jar and map its settings to your development
environment.

[Athena-Project-intellij-code-style.xml]: https://github.com/QubitPi/athena/blob/master/Athena-Project-intellij-code-style.xml
