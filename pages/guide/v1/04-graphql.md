---
layout: doc-guide
group: guide
subtopic: true
title: GraphQL Queries & Mutations
description: GraphQL API
version: 1
---

[GraphQL](https://graphql.com/) is a language specification published by Facebook for constructing graph APIs. The 
specification provides great flexibility in API expression, but also little direction for best practices for common 
mutation operations.  For example, it is silent on how to perform operations of file upload and download.

While those file operation are possible through JSON API, Athena does offer an opinionated GraphQL API that addresses 
exactly how to manage meta data of those files in a uniform way across your entire data model graph.


Schemas and Types
-----------------

In this section, you'll learn about how Athena adopts the GraphQL type system and how it describes what data can be 
queried. Since GraphQL can be used with any backend framework or programming language, we'll stay away from 
implementation-specific details for now and talk only about the concepts.

### Type System

If you've seen a GraphQL query before, you know that the GraphQL query language is basically about selecting fields on 
objects. So, for example, in the following query:

{% include code_example example='graphql-type-system' offset=0 %}

1. We start with a special "root" object
2. We select the metaData field on that
3. For the object returned by metaData, we select the fileName and fileType fields

Because the shape of a GraphQL query closely matches the result, you can predict what the query will return without
knowing that much about the server. But it's useful to have an exact description of the data we can ask for - what
fields can we select? What kinds of objects might they return? What fields are available on those sub-objects? That's
where the schema comes in.

Athena, like every GraphQL service, defines a set of types which completely describe the set of possible data you can
query on it. Then, when queries come in, they are validated and executed against that schema.

### Type Language

Athena is developed in Java, but GraphQL services can be written in any language. Since it doesn't rely on a specific 
programming language syntax, like Java, to talk about GraphQL schemas, GraphQL defines its own simple language -
"GraphQL schema language" - it's similar to the query language, and allows people to talk about GraphQL schemas in a 
language-agnostic way.

### Object Types and Fields

The most basic components of a GraphQL schema are object types, which just represent a kind of object we can fetch from 
Athena service, and what fields it has. In the GraphQL schema language, a file metadata is represented as:

```graphql
type MetaData {
    fileName: String!
    fileType: FileType
}
```

The language is pretty readable, but let's go over it so that we can have a shared vocabulary:

* `MetaData` is a _GraphQL Object Type_, meaning it's a type with some fields. In fact, most of the types in schema will
  be object types
* `fileName` and `fileType` are _fields_ on the `MetaData` type. That means that `fileName` and `fileType` are the only
  fields that can appear in any part of a GraphQL query that operates on the `MetaData` type
* `String` is one of the built-in _scalar_ types - these are types that resolve to a single scalar object, and cannot
  have sub-selections in the query.
* `String!` means that the field is _non-nullable_, meaning that the GraphQL service promises to always give you a value 
  when you query this field. In the type language, GraphQL represents those with an exclamation mark.
* `FileType` as explained in [Enumeration Types](#enumeration-types)

Now you know what a GraphQL object type looks like, and how to read the basis of the GraphQL type language.

### Arguments

Every field on a GraphQL object type can have zero or more arguments, for example the id field below:

### The Query and Mutation Types

Most types in Athena schema will just be normal object types, but there are two types that are special within a schema:

```graphql
schema {
    query: Query
    mutation: Mutation
}
```

Athena, as a GraphQL service, has a `query` type and a `mutation` type. These types are the same as regular object
types, but they are special because they define the _entry point_ of every GraphQL query. So when a query like this is
issued:

{% include code_example example='graphql-query-type-system' offset=2 %}

That means that the Athena has `Query` type with `MetaData` field:

```graphql
type Query {
    metaData(fileId: ID): MetaData
}
```

Mutations work in a similar way - Athena defines fields on the `Mutation` type, and those are available as the root
mutation fields we can call in Athena query.

It's important to remember that other than the special status of being the "entry point" into the schema, the `Query`
and `Mutation` types are the same as any other GraphQL object type, and their fields work exactly the same way.

### Enumeration Types

Also called _Enums_, enumeration types are a special kind of scalar that is restricted to a particular set of allowed 
values. This allows Athena to:

* Validate that any arguments of this type are one of the allowed values. For example, `fileType` can only be some
  known file extensions such as `PDF`, `TXT`, etc. 
* Communicate through the type system that a field will always be one of a finite set of values

* Here's what an enum definition might look like in the GraphQL schema language for the `fileType` field:

```graphql
enum FileType {
   PDF
   TXT
}
```

This means that wherever we use the type `FileType` in our schema, we expect it to be exactly either `PDF` or `TXT`. 


API Structure
-------------

GraphQL splits its schema into two kinds of objects:

1.  **Query objects** which are used to compose queries and mutations
2.  **Input Objects** which are used to supply input data to mutations

The schema for both kinds of objects are defined in [Athena GraphQL Schema][Athena GraphQL Schema]. 

### Input Objects

Input objects just contain attributes and relationship with names directly matching
the property names in the file metadata model:

![GraphQL Input Object UML](/athena/assets/images/graphql_input_object_uml.png){:class="img-responsive"}

### Query Objects

Query Objects are more complex than Input Objects since they do more than simply describe data; they must
support filtering, sorting, and pagination. Athena's GraphQL structure for queries and mutations is depicted below:

![GraphQL Query Object UML](/athena/assets/images/graphql_query_object_uml.png){:class="img-responsive"}


FETCH Examples
--------------

### Fetch Single Metadata by File ID

Fetches metadata of a file whose file ID is 2. The response includes the file name and file type:

{% include code_example example='fetch-one-metadata-by-file-id' offset=4 %}

### Schema Introspection

Coming soon.


[Athena GraphQL Schema]: https://github.com/QubitPi/athena/blob/master/athena-core/src/main/resources/schema.graphqls
