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
package io.github.qubitpi.athena.example.books

import org.eclipse.jetty.server.Server
import org.glassfish.jersey.server.ResourceConfig

import io.restassured.RestAssured
import jakarta.inject.Inject
import jakarta.ws.rs.ApplicationPath
import spock.lang.Specification

class JettyServerFactorySpec extends Specification {

    static final int PORT = 8081
    static final String ENDPOINT_RESOURCE_PACKAGE = "io.github.qubitpi.athena.example.books.resource"

    /**
     * DI constructor.
     * <p>
     * CAUTION: the {@code @ApplicationPath("v1")} is not taking effects. See {@link JettyServerFactory} for more
     * details.
     */
    @ApplicationPath("v1")
    class TestResourceConfig extends ResourceConfig {

        @Inject
        TestResourceConfig() {
            packages(ENDPOINT_RESOURCE_PACKAGE)
        }
    }

    def setupSpec() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = PORT
        RestAssured.basePath = "/v1"
    }

    def "Factory produces Jersey-Jetty applications"() {
        setup:
        Server server = JettyServerFactory.newInstance(PORT, "/v1/*", new TestResourceConfig())
        server.start()

        expect:
        RestAssured
                .when()
                .get("/v1/example/test")
                .then()
                .statusCode(200)

        RestAssured
                .when()
                .get("/v1/example/test").asString() == "SUCCESS"

        cleanup:
        server.stop()
    }
}
