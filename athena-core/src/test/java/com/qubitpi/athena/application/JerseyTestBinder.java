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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Configures JerseyTest and also sets up DI.
 * <p>
 * This is a singleton since JerseyTest binds a network port
 */
public class JerseyTestBinder {

    /**
     * Start harness and wait.
     */
    private class StartHarness extends Thread {

        private Throwable cause;

        /**
         * Start harness and wait.
         *
         * @throws InterruptedException if thread was interrupted
         */
        private StartHarness() throws InterruptedException {
            super("Start Harness");
        }

        /**
         * Start the test harness and track it for timeouts.
         *
         * @throws InterruptedException if the harness is interrupted
         */
        @SuppressWarnings("IllegalCatch")
        public void startHarness() throws InterruptedException {
            this.start();
            this.join(startTimeout);

            // If harness not started, throw timeout exception
            if (isAlive()) {
                // Include thread stack dump
                final StringBuilder stringBuilder = new StringBuilder("Timeout starting Jersey\n");
                for (final StackTraceElement stackTraceElement : this.getStackTrace()) {
                    stringBuilder.append("\tat ").append(stackTraceElement).append('\n');
                }
                // try to interrupt and tear down
                this.interrupt();
                this.join(10000);
                if (!isAlive()) {
                    try {
                        harness.tearDown();
                    } catch (final Exception exception) {
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
        @SuppressWarnings("IllegalCatch")
        public void run() {
            try {
                harness.setUp();
            } catch (final Throwable throwable) {
                cause = throwable;
            }
        }
    }

    private static final String RANDOM_PORT = "0";

    private final BinderFactory testBinderFactory;
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
    public JerseyTestBinder(final Class<?>... resourceClasses) {
        this(true, new ApplicationState(), resourceClasses);
    }

    /**
     * Constructor.
     *
     * @param doStart  Flag to indicate if the constructor should start the tests harness.
     * @param resourceClasses  Resource classes for Jersey to load
     */
    public JerseyTestBinder(final boolean doStart, final Class<?>... resourceClasses) {
        this(doStart, new ApplicationState(), resourceClasses);
    }

    /**
     * Constructor with more control over auto-start and the application state it uses.
     *
     * @param doStart  Will auto-start test harness after constructing if true, must be manually started if false.
     * @param applicationState  Application state to load for testing
     * @param resourceClasses  Resource classes for Jersey to load
     */
    public JerseyTestBinder(
            final boolean doStart,
            final ApplicationState applicationState,
            final Class<?>... resourceClasses
    ) {
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

            @Override
            protected void configureClient(final ClientConfig config) {
                for (final Class<?> cls : resourceClasses) {
                    if (cls.getSimpleName().equals(MultiPartFeature.class.getSimpleName())) {
                        config.register(MultiPartFeature.class);
                    }
                }
            }
        };

        if (doStart) {
            start();
        }
    }

    /**
     * Start the test harness.
     *
     * @throws IllegalStateException if thread was interrupted
     */
    public void start() {
        try {
            new StartHarness().startHarness();
            wasStarted = true;
        } catch (final InterruptedException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Tears down the test harness and reset all test application states.
     *
     * @throws Exception if there's a problem tearing things down
     */
    public void tearDown() throws Exception {
        getHarness().tearDown();
        applicationState.resetAllStates();
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
    public Builder makeRequest(final String target, final Map<String, Object> queryParams) {
        // Set target of call
        WebTarget httpCall = getHarness().target(target);

        // Add query params to call
        for (final Map.Entry<String, Object> entry : queryParams.entrySet()) {
            httpCall = httpCall.queryParam(entry.getKey(), entry.getValue());
        }

        return httpCall.request();
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
     * Builds a test binder factory.
     *
     * @param state  A set of mocked objects that are to be injected during testing phase
     *
     * @return a configured TestBinderFactory
     */
    @NotNull
    protected BinderFactory buildBinderFactory(final ApplicationState state) {
        return new TestBinderFactory(state);
    }
}
