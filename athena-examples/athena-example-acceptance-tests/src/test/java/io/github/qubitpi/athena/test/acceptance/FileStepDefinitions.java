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
package io.github.qubitpi.athena.test.acceptance;

import static org.hamcrest.Matchers.equalTo;

import org.junit.Assert;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * BDD step definition for file upload/download business logic.
 */
public class FileStepDefinitions extends AbstractStepDefinitions {

    private static final String FILE_ID = "fileId";

    private static final String PRIDE_AND_PREJUDICE_TXT = "pride-and-prejudice-by-jane-austen.txt";

    private static final String UPLOADED_FILE_METADATA_REQUEST_JSON = "uploaded-file-metadata-request.json";
    private static final String UPLOADED_FILE_METADATA_RESPONSE_JSON = "uploaded-file-metadata-response.json";

    private Response response;
    private List<String> fileId;

    /**
     * Step definition.
     */
    @When("^a text file is uploaded$")
    //CHECKSTYLE:OFF
    public void uploadTextFile() throws URISyntaxException {
        //CHECKSTYLE:ON
        response = RestAssured.given()
                .multiPart(
                        Paths.get(
                                Objects.requireNonNull(
                                        this.getClass()
                                                .getClassLoader()
                                                .getResource(String.format("file/%s", PRIDE_AND_PREJUDICE_TXT))
                                )
                                        .toURI()
                        )
                                .toFile()
                )
                .when()
                .post(FILE_UPLOAD_PATH);
    }

    /**
     * Step definition.
     */
    @Then("^the ID of that file is returned and the file metadata is generated$")
    public void responseContainsFileNameFieldOnly() {
        response.then()
                .statusCode(201);
        fileId = Collections.singletonList(response.body().path(FILE_ID));
        Assert.assertNotNull(fileId.get(0));
        Assert.assertFalse(fileId.get(0).isEmpty());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format(payload(UPLOADED_FILE_METADATA_REQUEST_JSON), fileId.get(0)))
                .when()
                .post(METADATA_ENDPOINT_PATH)
                .then()
                .statusCode(200)
                .body("", equalTo(new JsonPath(payload(UPLOADED_FILE_METADATA_RESPONSE_JSON)).get()));
    }

    /**
     * Step definition.
     *
     * @throws IllegalArgumentException if there is an error in test setup
     */
    @When("^the file ID of an existing text file is provided to download$")
    public void download() {
        if (fileId.get(0) == null) {
            throw new IllegalArgumentException("Cannot BE NULL");
        }
        if (fileId.get(0).isEmpty()) {
            throw new IllegalArgumentException("Cannot BE EMPTY");
        }
        response = RestAssured.given()
                .queryParam(FILE_ID, fileId.get(0))
                .when()
                .get(FILE_DOWNLOAD_PATH);
    }

    /**
     * Step definition.
     */
    @Then("^the text can be properly downloaded$")
    public void originalTextIsDownloaded() {
        Assert.assertEquals(file(PRIDE_AND_PREJUDICE_TXT), response.body().print());
    }
}
