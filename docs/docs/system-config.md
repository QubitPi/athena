---
title: System Configuration
description: System Configuration
sidebar_position: 11
---

Athena has two main configuration avenues, the domain object configuration (File Store, Meta Store, and Data Fetchers)
which happens via compiled Java code, and system configuration via properties. The domain configuration is
covered elsewhere, and we'll only cover the system configuration infrastructure here.

The system for property configuration that Athena uses lives in it's own [sub-module][athena-system-config]. This system
is extensible and reusable so that other Athena modules, and even other projects, can leverage it for their own property
config needs. That sub-module has it's own deep set of documentation, so we'll be focusing only on how to use it for
configuring Athena.

Configuration Sources and Overrides
-----------------------------------

Configuration for Athena modules come from only one location (that is, within the [sub-module][athena-system-config]
itself) and allows for overriding other settings. This is particularly useful when overriding a property set in a module
to turn off a feature, or to override a default configuration for your application in a certain environment, for
example.

Configuration sources are shown below, and are resolved in priority order, with higher-priority sources overriding
settings from lower-priority sources. Sources that are files will available to Athena on the Classpath for them to be
loaded.

| Priority | Source                              | Notes                                       |
| -------: | ----------------------------------- |---------------------------------------------|
| (High) 1 | Environment variables               |                                             |
|        2 | Java properties                     |                                             |
|        3 | `userConfig.properties`<sup>*</sup> |                                             |
|        5 | `applicationConfig.properties`      | Every application MUST provide one of these |

<sub>* Since `userConfig.properties` is often used while developing to turn features on and off, `.gitignore` includes
a  rule to ignore this file by default to help prevent checking it in accidentally.</sub>

[athena-system-config]: https://github.com/paion-data/athena/tree/master/athena-system-config
