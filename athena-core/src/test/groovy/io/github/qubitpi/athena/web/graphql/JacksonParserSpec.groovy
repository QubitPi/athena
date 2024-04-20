/*
 * Copyright 2024 Jiaqi Liu
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
package io.github.qubitpi.athena.web.graphql

import spock.lang.Unroll

class JacksonParserSpec extends JsonDocumentParserSpec {

    @Override
    JsonDocumentParser getInstance() {
        JacksonParser.instance
    }

    @SuppressWarnings("GroovyAccessibility")
    def "Query field, in happy path, can be extracted"() {
        expect:
        JacksonParser.getQuery('{"query": "GraphQL"}') == "GraphQL"
    }

    @Unroll
    @SuppressWarnings("GroovyAccessibility")
    def "Query field cannot be extracted in the case of #description"() {
        when: "invalid JSON document is parsed"
        JacksonParser.getQuery(graphQLDocument)

        then: "a runtime exception is thrown"
        Exception actual = thrown(exception)
        exceptionType.isAssignableFrom(actual.getClass())
        actual.message == exceptionMessage

        where:
        graphQLDocument | exceptionType    | exception                | exceptionMessage                                                                                                | description
        "invalid JSON"  | RuntimeException | IllegalArgumentException | "Athena could not process the request because HTTP request body is not properly JSON-formatted: 'invalid JSON'" | "invalid JSON"
        '{"foo": 123}'  | RuntimeException | IllegalArgumentException | "Athena could not process the request because payload is missing 'query' field: '{\"foo\": 123}'"               | "JSON not having 'query' field"
    }
}
