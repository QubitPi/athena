---
layout: doc-guide
group: guide
title: Client APIs
description: JSON API and GraphQL Client APIs
version: 1
---

Graph APIs are an evolution of web service APIs that serve and manipulate data for mobile & web applications. They have
a number of characteristics that make them well suited to this task:

1. Most notably, they present a **data model** as an entity relationship graph and an **accompanying schema**.
   * A well-defined model allows for a consistent view of the data and a centralized way to manipulate an instance of
     the model or to cache it.
   * The schema provides powerful introspection capabilities that can be used to build tools to help developers understant
     and navigate the model.
2. The API allows the client to **fetch or mutate as much or as little information in single roundtrip** between client
   and server. This also shrinks payload sizes and simplifies the process of schema evolution
3. There is a **well-defined standard** for the API that fosters a community approach to development of supporting tools
   & best practices.

Athena supports the two most widely adopted standards for graph APIs:

* [JSON-API for File Operation]({{site.baseurl}}/pages/guide/v{{ page.version }}/03-jsonapi.html)
* [GraphQL for File Meta Data Operation]({{site.baseurl}}/pages/guide/v{{ page.version }}/04-graphql.html)
