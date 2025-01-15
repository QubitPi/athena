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

import io.restassured.RestAssured
import io.restassured.http.ContentType

import java.nio.charset.StandardCharsets

class FileServletSpec extends AbstractServletSpec {

    def "File can be uploaded and then download"() {
        expect: "file uploads successfully with 201"
        String fileId = RestAssured.given()
                .multiPart("file", new File("src/test/resources/pride-and-prejudice-by-jane-austen.txt"))
                .when()
                .post("/file/upload")
                .then()
                .statusCode(201)
                .extract().path("fileId")

        and: "the same file can be downloaded with file ID"
        RestAssured.given()
                .contentType(ContentType.BINARY)
                .queryParam("fileId", fileId)
                .when()
                .get("/file/download")
                .then()
                .statusCode(200)
                .extract()
                .asString() == getClass()
                .getClassLoader()
                .getResourceAsStream("pride-and-prejudice-by-jane-austen.txt")
                .getText(StandardCharsets.UTF_8.name())
    }
}
