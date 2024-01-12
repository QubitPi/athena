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
package org.qubitpi.athena.metastore.graphql.query

import org.qubitpi.athena.metadata.FileType
import org.qubitpi.athena.metadata.MetaData
import org.qubitpi.athena.metastore.graphql.GraphQLFactory

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

abstract class GraphQLQueryProviderSpec extends Specification {

    static final String FILE_ID = "fileId123"
    static final String FILE_NAME = "pride-and-prejudice.pdf"
    static final String EXPECTED_QUERY_FILE = "expected-query.graphql"
    static final String EXPECTED_MUTATION_FILE = "expected-mutation.graphql";

    @Shared
    @Subject
    GraphQLQueryProvider graphQLQueryProvider = getTestProvider()

    /**
     * Returns an implementation under test.
     *
     * @return a new instance
     */
    abstract GraphQLQueryProvider getTestProvider()

    def "Given a file ID and a list of metadata fields, a GraphQL file metadata Query document string is provided"() {
        expect:
        graphQLQueryProvider.query(FILE_ID, ["fileType", "fileName"]) ==
                GraphQLFactory.getGraphQLSchemaResourceAsString(EXPECTED_QUERY_FILE)
    }

    def "File ID cannot be null for making the Query document"() {
        when: "file ID is null"
        graphQLQueryProvider.query(null, ["fileType", "fileName"])

        then: "error is thrown"
        thrown(NullPointerException)
    }

    def "Metadata field list cannot be null for making the Query document"() {
        when: "metadata field list is null"
        graphQLQueryProvider.query(FILE_ID, null)

        then: "error is thrown"
        thrown(NullPointerException)
    }

    def "Metadata field list cannot be empty for making the Query document"() {
        when: "metadata field list is empty"
        graphQLQueryProvider.query(FILE_ID, [])

        then: "error is thrown"
        thrown(IllegalArgumentException)
    }

    def "Given a file ID and a metadata info, a GraphQL file metadata Mutation document string is provided"() {
        given: "a meta data info"
        MetaData metaData = Mock(MetaData) {
            getFileName() >> FILE_NAME
            getFileType() >> FileType.PDF
        }

        expect:
        graphQLQueryProvider.mutation(FILE_ID, metaData) ==
                GraphQLFactory.getGraphQLSchemaResourceAsString(EXPECTED_MUTATION_FILE)
    }

    def "File ID cannot be null for making the Mutation document"() {
        when: "file ID is null"
        graphQLQueryProvider.mutation(null, Mock(MetaData))

        then: "runtime error is thrown"
        thrown(NullPointerException)
    }

    def "Meta data object cannot be null for making the Mutation document"() {
        when: "file ID is null"
        graphQLQueryProvider.mutation(FILE_ID, null)

        then: "runtime error is thrown"
        thrown(NullPointerException)
    }
}
