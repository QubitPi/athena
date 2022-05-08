/*
 * Copyright Jiaqi Liu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qubitpi.athena.metastore.graphql

import com.qubitpi.athena.metadata.FileType
import com.qubitpi.athena.metadata.MetaData
import com.qubitpi.athena.metastore.graphql.query.GraphQLQueryProvider

import graphql.ExecutionInput
import graphql.GraphQL
import spock.lang.Specification

class GraphQLMetaStoreSpec extends Specification {

    static final String FILE_ID = "fileId123"
    static final String FILE_NAME = "pride-and-prejudice.pdf"
    static final String EXPECTED_QUERY_FILE = "expected-query.graphql"
    static final String EXPECTED_MUTATION_FILE = "expected-mutation.graphql";

    @SuppressWarnings(["GroovyAccessibility", "GrEqualsBetweenInconvertibleTypes"])
    def "Native GraphQL query can be directly passed to get file meta data"() {
        given: "a mocked GraphQL client"
        GraphQL graphQL = Mock(GraphQL)

        and: "a MetaStore with the injected GraphQL client"
        GraphQLMetaStore metaStore = new GraphQLMetaStore(graphQL, Mock(GraphQLQueryProvider))

        when: "a native query is sent to the GraphQL client through the MetaStore"
        metaStore.executeNative("query")

        then: "the query is processed by the client"
        1 * graphQL.execute( {it == "query"} )
    }

    @SuppressWarnings("GroovyAccessibility")
    def "Native GraphQL query cannot be null"() {
        given: "a Metastore whose executeNative method can be called "
        GraphQLMetaStore metaStore = new GraphQLMetaStore(Mock(GraphQL), Mock(GraphQLQueryProvider))

        when: "the native query is null and being executed"
        metaStore.executeNative(null)

        then: "runtime error is thrown"
        thrown(NullPointerException)
    }

    @SuppressWarnings("GroovyAccessibility")
    def "File meta data can be queried by specifying file ID and requested meta data fields"() {
        given: "a mocked GraphQL client"
        GraphQL graphQL = Mock(GraphQL)

        and: "a mocked GraphQL query maker that constructs valid query on certain input"
        GraphQLQueryProvider graphQLQueryProvider = Mock(GraphQLQueryProvider) {
            query(FILE_ID, ["fileType", "fileName"]) >> GraphQLFactory.getGraphQLSchemaResourceAsString(
                    EXPECTED_QUERY_FILE
            )
        }

        and: "a MetaStore with the injected GraphQL client and query maker"
        GraphQLMetaStore metaStore = new GraphQLMetaStore(graphQL, graphQLQueryProvider)

        when: "a query is sent to the GraphQL client using the requested query parameters"
        metaStore.getMetaData(FILE_ID, ["fileType", "fileName"])

        then: "the query is constructed and processed by the client"
        1 * graphQL.execute(_ as ExecutionInput)
    }

    @SuppressWarnings("GroovyAccessibility")
    def "File ID cannot be null for querying meta data"() {
        given: "a Metastore whose getMetaData method can be called "
        GraphQLMetaStore metaStore = new GraphQLMetaStore(Mock(GraphQL), Mock(GraphQLQueryProvider))

        when: "the getMetaData method is invoked with mock and file ID being null"
        metaStore.getMetaData(null, Mock(List))

        then: "runtime error is thrown"
        thrown(NullPointerException)
    }

    @SuppressWarnings("GroovyAccessibility")
    def "Metadata field list cannot be null for querying meta data"() {
        given: "a Metastore whose getMetaData method can be called "
        GraphQLMetaStore metaStore = new GraphQLMetaStore(Mock(GraphQL), Mock(GraphQLQueryProvider))

        when: "the getMetaData method is invoked with mock and requested fields being null"
        metaStore.getMetaData(FILE_ID, null)

        then: "runtime error is thrown"
        thrown(NullPointerException)
    }

    @SuppressWarnings("GroovyAccessibility")
    def "Metadata field list cannot be empty for querying meta data"() {
        given: "a Metastore whose getMetaData method can be called "
        GraphQLMetaStore metaStore = new GraphQLMetaStore(Mock(GraphQL), Mock(GraphQLQueryProvider))

        when: "the getMetaData method is invoked with mock and requested fields being empty list"
        metaStore.getMetaData(FILE_ID, [])

        then: "runtime error is thrown"
        thrown(IllegalArgumentException)
    }

    @SuppressWarnings("GroovyAccessibility")
    def "File meta data can be saved by specifying file ID and requested meta data fields"() {
        given: "a mocked GraphQL client"
        GraphQL graphQL = Mock(GraphQL)

        and: "mocked meta data info"
        MetaData metaData = Mock(MetaData) {
            getFileName() >> FILE_NAME
            getFileType() >> FileType.PDF
        }

        and: "a mocked GraphQL query maker that constructs valid query on certain input"
        GraphQLQueryProvider graphQLQueryProvider = Mock(GraphQLQueryProvider) {
            mutation(FILE_ID, metaData) >> GraphQLFactory.getGraphQLSchemaResourceAsString(
                    EXPECTED_MUTATION_FILE
            )
        }

        and: "a MetaStore with the injected GraphQL client and query maker"
        GraphQLMetaStore metaStore = new GraphQLMetaStore(graphQL, graphQLQueryProvider)

        when: "a query is sent to the GraphQL client using the requested query parameters"
        metaStore.saveMetaData(FILE_ID, metaData)

        then: "the query is constructed and processed by the client"
        1 * graphQL.execute({ it == GraphQLFactory.getGraphQLSchemaResourceAsString(
                EXPECTED_MUTATION_FILE) }
        )
    }

    @SuppressWarnings("GroovyAccessibility")
    def "File ID cannot be null for saving meta data"() {
        given: "a Metastore whose saveMetaData method can be called "
        GraphQLMetaStore metaStore = new GraphQLMetaStore(Mock(GraphQL), Mock(GraphQLQueryProvider))

        when: "the method is invoked with file ID being null"
        metaStore.saveMetaData(null, Mock(MetaData))

        then: "runtime error is thrown"
        thrown(NullPointerException)
    }

    @SuppressWarnings("GroovyAccessibility")
    def "Metadata field list cannot be null for saving meta data"() {
        given: "a Metastore whose saveMetaData method can be called "
        GraphQLMetaStore metaStore = new GraphQLMetaStore(Mock(GraphQL), Mock(GraphQLQueryProvider))

        when: "the method is invoked with file meta data being null"
        metaStore.saveMetaData(FILE_ID, null)

        then: "runtime error is thrown"
        thrown(NullPointerException)
    }
}
