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

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * BDD step definition for file metadata read & write business logic.
 */
public class MetadataStepDefinitions extends AbstractStepDefinitions {

    private static final String QUERY = "query";

    private static final String SINGLE_FIELD_METADATA_REQUEST_JSON = "single-field-metadata-request.json";
    private static final String MULTI_FIELD_METADATA_REQUEST_JSON = "multiple-fields-metadata-request.json";
    private static final String SINGLE_FIELD_METADATA_RESPONSE_JSON = "single-field-metadata-response.json";
    private static final String MULTI_FIELD_METADATA_RESPONSE_JSON = "multiple-fields-metadata-response.json";
    private static final String SINGLE_FIELD_METADATA_REQUEST_GRAPHQL = "single-field-metadata-request.graphql";
    private static final String MULTI_FIELD_METADATA_REQUEST_GRAPHQL = "multiple-fields-metadata-request.graphql";

    private Response getResponse;
    private Response postResponse;
    private RequestSpecification getRequest;
    private RequestSpecification postRequest;

    /**
     * Step definition.
     */
    @When("^the query is executed$")
    public void executeQuery() {
        getResponse = getRequest.when().get(METADATA_ENDPOINT_PATH);
        postResponse = postRequest.when().post(METADATA_ENDPOINT_PATH);
    }

    /**
     * Step definition.
     */
    @Given("^the query is asking for file name only$")
    public void fileNameOnly() {
        getRequest = RestAssured.given()
                .queryParam(QUERY, pathParam(SINGLE_FIELD_METADATA_REQUEST_GRAPHQL));
        postRequest = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload(SINGLE_FIELD_METADATA_REQUEST_JSON));
    }

    /**
     * Step definition.
     */
    @Then("^response contains only one field, which is file name$")
    public void responseContainsFileNameFieldOnly() {
        getResponse.then()
                .statusCode(200)
                .body("", equalTo(new JsonPath(payload(SINGLE_FIELD_METADATA_RESPONSE_JSON)).get()));
        postResponse.then()
                .statusCode(200)
                .body("", equalTo(new JsonPath(payload(SINGLE_FIELD_METADATA_RESPONSE_JSON)).get()));
    }

    /**
     * Step definition.
     */
    @Given("^the query is asking for multiple metadata fields$")
    public void multipleFields() {
        getRequest = RestAssured.given()
                .queryParam(QUERY, pathParam(MULTI_FIELD_METADATA_REQUEST_GRAPHQL));
        postRequest = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload(MULTI_FIELD_METADATA_REQUEST_JSON));
    }

    /**
     * Step definition.
     */
    @Then("^response contains all requested fields$")
    public void responseContainsAllRequestedFields() {
        getResponse.then()
                .statusCode(200)
                .body("", equalTo(new JsonPath(payload(MULTI_FIELD_METADATA_RESPONSE_JSON)).get()));
        postResponse.then()
                .statusCode(200)
                .body("", equalTo(new JsonPath(payload(MULTI_FIELD_METADATA_RESPONSE_JSON)).get()));
    }
}
