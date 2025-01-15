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
package io.github.qubitpi.athena.example.books.web.endpoints

import static org.hamcrest.Matchers.equalTo

import groovy.json.JsonSlurper
import io.restassured.RestAssured
import io.restassured.http.ContentType

class MetaServletSpec extends AbstractServletSpec {

    def "File meta data can be accessed through GraphQL GET endpoint"() {
        expect:
        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("query", """{metaData(fileId:"1"){fileName\nfileType}}""")
                .when()
                .get("/metadata/graphql")
                .then()
                .statusCode(200)
                .body("", equalTo(new JsonSlurper().parseText(expectedMultiFieldMetadataResponse())))
    }

    def "File metadata can be accessed through GraphQL POST endpoint"() {
        expect:
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "query": "{ metaData(fileId: \\"1\\") { fileName fileType } }",
                                    "variables": null
                                }
                                """
                )
                .when()
                .post("/metadata/graphql")
                .then()
                .statusCode(200)
                .body("", equalTo(new JsonSlurper().parseText(expectedMultiFieldMetadataResponse())))
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
