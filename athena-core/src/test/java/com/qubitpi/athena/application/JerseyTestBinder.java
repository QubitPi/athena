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
package com.qubitpi.athena.application;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.swing.plaf.PanelUI;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

/**
 * Configures JerseyTest and also sets up di
 */
public class JerseyTestBinder {

    /**
     * Start harness and wait
     */
    private class StartHarness extends Thread {

        private Throwable cause;

        private StartHarness() throws InterruptedException {
            super("Start Harness");
        }

        public void startHarness() throws InterruptedException {
            this.start();
            this.join(startTimeout);

            // If harness not started, throw timeout exception
            if (isAlive()) {
                // Include thread stack dump
                StringBuilder stringBuilder = new StringBuilder("Timeout starting Jersey\n");
                for (StackTraceElement stackTraceElement : this.getStackTrace()) {
                    stringBuilder.append("\tat ").append(stackTraceElement).append('\n');
                }
                // try to interrupt and tear down
                this.interrupt();
                this.join(10000);
                if (!isAlive()) {
                    try {
                        harness.tearDown();
                    } catch (Exception exception) {
                        throw new IllegalStateException(stringBuilder.toString(), exception);
                    }
                }
                throw new IllegalStateException(stringBuilder.toString(), cause);
            }

            // If problem starting harness, throw cause
            if (cause != null) {
                throw new IllegalStateException(cause);
            }
        }

        @Override
        public void run() {
            try {
                harness.setUp();
            } catch (Throwable throwable) {
                cause = throwable;
            }
        }
    }

    private static final String RANDOM_PORT = "0";

    private final TestBinderFactory testBinderFactory;
    private final ApplicationState applicationState;
    private final AbstractBinder binder;
    private final ResourceConfig resourceConfig;
    private final JerseyTest harness;
    private final long startTimeout = 30000L;

    private boolean wasStarted = false;

    /**
     * Constructor that will auto-start.
     *
     * @param resourceClasses  Resource classes for Jersey to load
     */
    public JerseyTestBinder(Class<?>... resourceClasses) {
        this(true, new ApplicationState(), resourceClasses);
    }

    /**
     * Constructor.
     *
     * @param doStart  Flag to indicate if the constructor should start the tests harness.
     * @param resourceClasses  Resource classes for Jersey to load
     */
    public JerseyTestBinder(boolean doStart, Class<?>... resourceClasses) {
        this(doStart, new ApplicationState(), resourceClasses);
    }

    /**
     * Constructor with more control over auto-start and the application state it uses.
     *
     * @param doStart  Will auto-start test harness after constructing if true, must be manually started if false.
     * @param applicationState  Application state to load for testing
     * @param resourceClasses  Resource classes for Jersey to load
     */
    public JerseyTestBinder(boolean doStart, ApplicationState applicationState, Class<?>... resourceClasses) {
        this.applicationState = applicationState;

        this.testBinderFactory = buildBinderFactory(applicationState);
        this.binder = (AbstractBinder) testBinderFactory.buildBinder();

        // Configure and register the resources
        this.resourceConfig = new ResourceConfig();
        Arrays.stream(resourceClasses).forEachOrdered(cls -> this.resourceConfig.register(cls, 5));
        this.resourceConfig.register(this.binder);

        this.harness = new JerseyTest() {
            @Override
            protected Application configure() {
                // Find first available port.
                forceSet(TestProperties.CONTAINER_PORT, RANDOM_PORT);

                return resourceConfig;
            }
        };

        if (doStart) {
            start();
        }
    }

    /**
     * Start the test harness
     */
    public void start() {
        try {
            new StartHarness().startHarness();
            wasStarted = true;
        } catch (Exception exception) {
            throw (exception instanceof IllegalStateException)
                    ? (IllegalStateException) exception
                    : new IllegalStateException(exception);
        }
    }

    /**
     * Tears down the test harness and unload the binder.
     *
     * @throws Exception if there's a problem tearing things down
     */
    public void tearDown() throws Exception {
        getHarness().tearDown();
    }

    /**
     * Constructs and sends a request to a specified URL with specified query parameters.
     * <p>
     * If the request does not have any query parameters, please use {@link #makeRequest(String)} instead.
     *
     * @param target  The specified URL
     * @param queryParams  The specified query parameters
     *
     * @return a request builder which user can use to send different types of requests, such as HTTP HEAD and HTTP GET
     * methods.
     *
     * @throws NullPointerException if {@code target} or {@code queryParams} is {@code null}
     */
    public Builder makeRequest(String target, Map<String, Object> queryParams) {
        // Set target of call
        WebTarget httpCall = getHarness().target(target);

        // Add query params to call
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            httpCall = httpCall.queryParam(entry.getKey(), entry.getValue());
        }

        return httpCall.request();
    }

    /**
     * Constructs and sends a request to a specified URL.
     * <p>
     * If the request has query parameters, please use {@link #makeRequest(String, Map)} instead.
     *
     * @param target  The specified URL
     *
     * @return a request builder which user can use to send different types of requests, such as HTTP HEAD and HTTP GET
     * methods.
     *
     * @throws NullPointerException if {@code target} is {@code null}
     */
    @NotNull
    public Builder makeRequest(final @NotNull String target) {
        return makeRequest(Objects.requireNonNull(target), Collections.emptyMap());
    }

    @NotNull
    public AbstractBinder getBinder() {
        return binder;
    }

    @NotNull
    public JerseyTest getHarness() {
        return harness;
    }

    /**
     * Builds a test binder factory
     *
     * @return a configured TestBinderFactory
     */
    @NotNull
    private TestBinderFactory buildBinderFactory(ApplicationState applicationState) {
        return new TestBinderFactory(applicationState);
    }

}
