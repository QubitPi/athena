/*
 * Copyright 2025 Jiaqi Liu
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
package io.github.qubitpi.athena.example.books;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Objects;

/**
 * {@link JettyServerFactory} is provides embedded Jersey-Jetty instances for testing purposes.
 * <p>
 * Note that {@link JettyServerFactory} is designed only for testing purposes. Any production uses are not assumed.
 */
@Immutable
@ThreadSafe
public final class JettyServerFactory {

    /**
     * Constructor.
     * <p>
     * Suppress default constructor for noninstantiability.
     *
     * @throws AssertionError when called
     */
    private JettyServerFactory() {
        throw new AssertionError();
    }

    /**
     * Returns a embedded Jersey-Jetty server for local testing purposes.
     *
     * @param port  The port number serving all testing requests on the embedded Jetty
     * @param pathSpec  The common path of all API's, e.g. "/v1/*"
     * @param resourceConfig  A Jersey subclass of JAX-RS {@link jakarta.ws.rs.core.Application}. Due to a
     * <a href="https://github.com/eclipse-ee4j/jersey/issues/3222">bug</a>) in Jersey, {@code @ApplicationPath}
     * annotated on {@code resourceConfig} class is ignored in embedded Jetty. For example
     *
     * <pre>
     * {@code
     * @ApplicationPath("v1")
     * class TestResourceConfig extends ResourceConfig {
     *
     *     @Inject
     *     TestResourceConfig() {
     *         packages(ENDPOINT_RESOURCE_PACKAGE)
     *     }
     * }
     * }
     * </pre>
     *
     * The {@code @ApplicationPath("v1")} annotation is
     * <a href="https://github.com/eclipse-ee4j/jersey/issues/3222">not taking any effects</a>. We must somehow prefix
     * "v1" either at endpoint resource (such as {@code @Path("/v1/...")}) or completely remove "v1" in test request
     * path. Which option to choose makes no difference.
     *
     * @return the embedded Jetty server for local testing purposes
     *
     * @throws NullPointerException if {@code pathSpec} or {@code resourceConfig} is {@code null}
     */
    public static Server newInstance(final int port, final String pathSpec, final ResourceConfig resourceConfig) {
        Objects.requireNonNull(pathSpec, "pathSpec");
        Objects.requireNonNull(resourceConfig, "resourceConfig");

        final Server server = new Server(port);

        final ServletContainer servletContainer = new ServletContainer(resourceConfig);
        final ServletHolder servletHolder = new ServletHolder(servletContainer);
        final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(servletHolder, pathSpec);
        server.setHandler(servletContextHandler);

        return server;
    }
}
