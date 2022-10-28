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
package com.qubitpi.athena.example.books.web.endpoints

import com.qubitpi.athena.example.books.application.BookJerseyTestBinder
import com.qubitpi.athena.web.endpoints.MetaServlet

import groovy.json.JsonSlurper

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

class MetaServletSpec extends AbstractServletSpec {

    @Override
    def childSetup() {
        jerseyTestBinder = new BookJerseyTestBinder(true, MetaServlet.class)
    }

    def "File meta data can be accessed through GraphQL GET endpoint"() {
        when: "we get meta data via GraphQL GET"
        String actual = jerseyTestBinder.makeRequest(
                "/metadata/graphql",
                [query: URLEncoder.encode("""{metaData(fileId:"1"){fileName\nfileType}}""", "UTF-8")]
        ).get(String.class)

        then: "the response contains all requested metadata info without error"
        new JsonSlurper().parseText(actual) == new JsonSlurper().parseText(expectedMultiFieldMetadataResponse())
    }

    def "File metadata can be accessed through GraphQL POST endpoint"() {
        when: "we get meta data via GraphQL POST"
        String actual = jerseyTestBinder.makeRequest("/metadata/graphql")
                .post(
                        Entity.entity(
                                """
                                {
                                    "query": "{ metaData(fileId: \\"1\\") { fileName fileType } }",
                                    "variables": null
                                }
                                """,
                                MediaType.APPLICATION_JSON
                        )

                )
                .readEntity(String.class)

        then: "the response contains all requested metadata info without error"
        new JsonSlurper().parseText(actual) == new JsonSlurper().parseText(expectedMultiFieldMetadataResponse())
    }

    def expectedMultiFieldMetadataResponse() {
        """
        {
           "errors":[

           ],
           "data":{
              "metaData":{
                 "fileName":"Harry Potter",
                 "fileType":"PDF"
              }
           },
           "extensions":null,
           "dataPresent":true
        }
        """
    }
}
