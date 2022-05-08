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
package com.qubitpi.athena.test.acceptance;

import com.qubitpi.athena.application.ResourceConfig;
import com.qubitpi.athena.config.SystemConfig;
import com.qubitpi.athena.config.SystemConfigFactory;

import org.eclipse.jetty.server.Server;

import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;
import jakarta.validation.constraints.NotNull;

/**
 * BDD initialization step definition before all other steps are executed.
 * <p>
 * All init logics are defined in {@link #beforeAll()}
 */
@SuppressWarnings("unused")
public class InitStepDefinitions {

    /**
     * Test config client.
     */
    protected static final SystemConfig SYSTEM_CONFIG = SystemConfigFactory.getInstance();

    private static final String RESOURCE_BINDER_CONFIG_KEY = "athena__resource_binder";
    private static final String RESOURCE_BINDER_FACTORY =
            "com.qubitpi.athena.example.books.application.BooksBinderFactory";

    /**
     * BDD initialization definition.
     */
    @BeforeAll
    public static void beforeAll() {
        startAthena();
        initRestAssured();
    }

    /**
     * Spins up an Athena instance in Jetty standalone mode using the athena-example-books configs.
     *
     * @throws IllegalStateException if an error occurs during the resource binding phase
     */
    @NotNull
    private static void startAthena() {
        SYSTEM_CONFIG.setProperty(RESOURCE_BINDER_CONFIG_KEY, RESOURCE_BINDER_FACTORY);

        final Server server;
        try {
            server = JettyServerFactory.newInstance(8080, "/v1/*", new ResourceConfig());
        } catch (final ClassNotFoundException exception) {
            final String message = String.format(
                    "Cannot locate binder factory class (%s) as specified in '%s'",
                    RESOURCE_BINDER_FACTORY,
                    RESOURCE_BINDER_CONFIG_KEY
            );
            throw new IllegalStateException(message, exception);
        } catch (final InstantiationException exception) {
            final String message = String.format("Filed to instantiate class '%s'", RESOURCE_BINDER_FACTORY);
            throw new IllegalStateException(message, exception);
        } catch (final IllegalAccessException exception) {
            final String message = String.format(
                    "The class '%s' or its no-args constructor is not accessible",
                    RESOURCE_BINDER_FACTORY
            );
            throw new IllegalStateException(message, exception);
        }

        //CHECKSTYLE:OFF
        try {
            server.start();
        } catch (final Exception exception) {
            throw new IllegalStateException("Jetty server failed to start.", exception);
        }
        //CHECKSTYLE:ON
    }

    /**
     * Defines the endpoint resource location used during acceptance test.
     */
    private static void initRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/v1";
    }
}
