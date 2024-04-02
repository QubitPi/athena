---
title: Glossary
description: Glossary
sidebar_position: 7
---

This is collection of terms related to Athena and its concepts.

Application Concerns
--------------------

### Health Check

A Health Check is a mechanism to programatically assert if the web service is healthy or not in a binary yes/no
fashion.

### Feature Flag

A Feature Flag is a boolean configuration mechanism that can be used to turn certain capabilities on or off via a
simple flag-like setting.

### System Config

System Config is a layered configuration infrastructure that makes it easy to handle configuration within the code, as
well as easy to specify configuration in different environments.

### Request Log

The Request Log is an extensible log line that Athena emits after a request has been handled and responded to. The data
in this log line is built up as the request is processed and it includes information about nearly every phase of
processing a request, including how long things took at both fine-grained and aggregate levels.

### File Store

A File Store is the generic name for the source of the users' files, like OpenStack Swift and Hadoop Distributed File
System (HDFS)

Miscellaneous
-------------

### Spock

Spock is a Groovy-based BDD-style testing framework.

### Groovy

Groovy is a dynamic JVM-based programming language. It's dynamic and flexible nature make it particularly good for
uses like testing.

### Servlet

A Servlet is a Java construct that usually is designed to handle an HTTP request. For Athena, we also have a Servlet
construct, and while it's similar to the Java construct, it's more akin to a Controller in other MVC web frameworks
like Ruby on Rails or Grails.

### Meta Data

A Meta Data is some piece of combined information that describes a general file, such as file name and file type.

### Web Service

A software system, usually located at the server side in a client-server organization on the web, acting as middleware
or interface between a client and a database server. In a more general definition,
[W3C](https://www.w3.org/TR/2004/NOTE-ws-gloss-20040211/#webservice) defines web service as

> a software system designed to support interoperable machine-to-machine interaction over a network.

See also [web service](https://en.wikipedia.org/wiki/Web_service).
