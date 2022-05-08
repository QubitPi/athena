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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Objects;

import javax.ws.rs.core.Application;

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
     */
    private JettyServerFactory() {
        throw new AssertionError();
    }

    /**
     * Returns a embedded Jersey-Jetty server for local testing purposes.
     *
     * @param port  The port number serving all testing requests on the embedded Jetty
     * @param pathSpec  The common path of all API's, e.g. "/v1/*"
     * @param resourceConfig  A Jersey subclass of JAX-RS {@link Application}
     *
     * @return the embedded Jetty server for local testing purposes
     *
     * @throws NullPointerException if {@code pathSpec} or {@code resourceConfig} is {@code null}
     */
    @NotNull
    public static Server newInstance(
            final int port,
            final @NotNull String pathSpec,
            final @NotNull ResourceConfig resourceConfig
    ) {
        Objects.requireNonNull(pathSpec);
        Objects.requireNonNull(resourceConfig);
        final Server server = new Server(port);

        final ServletContainer servletContainer = new ServletContainer(resourceConfig);
        final ServletHolder servletHolder = new ServletHolder(servletContainer);
        final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(servletHolder, pathSpec);
        server.setHandler(servletContextHandler);

        return server;
    }
}
