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
package io.github.qubitpi.athena.application;

import static io.github.qubitpi.athena.config.ErrorMessageFormat.CONFIG_NOT_FOUND;

import io.github.qubitpi.athena.config.SystemConfig;
import io.github.qubitpi.athena.config.SystemConfigFactory;

import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ApplicationPath;

import jakarta.inject.Inject;

/**
 * The resource configuration for the Athena web applications.
 */
@ApplicationPath("v1")
public class ResourceConfig extends org.glassfish.jersey.server.ResourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceConfig.class);

    private static final String ATHENA_ENDPOINT_PACKAGE = "io.github.qubitpi.athena.web.endpoints";

    private static final String RESOURCE_BINDER_KEY = "resource_binder";
    private static final SystemConfig SYSTEM_CONFIG = SystemConfigFactory.getInstance();

    private final String bindingFactory = SYSTEM_CONFIG.getStringProperty(
            SYSTEM_CONFIG.getPackageVariableName(RESOURCE_BINDER_KEY)
    ).orElseThrow(() -> {
        LOG.error(CONFIG_NOT_FOUND.logFormat(RESOURCE_BINDER_KEY));
        return new IllegalStateException(CONFIG_NOT_FOUND.format());
    });

    /**
     * DI Constructor.
     *
     * @throws ClassNotFoundException if a class was not found when attempting to load it
     * @throws InstantiationException if a class was not able to be instantiated
     * @throws IllegalAccessException if there was a problem accessing something due to security restrictions
     */
    @Inject
    public ResourceConfig() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class<? extends BinderFactory> binderClass = Class.forName(getBindingFactory())
                .asSubclass(BinderFactory.class);
        LOG.info("Application resource binder is '{}'", binderClass.getCanonicalName());
        final BinderFactory binderFactory = binderClass.newInstance();
        final Binder binder = binderFactory.buildBinder();

        packages(ATHENA_ENDPOINT_PACKAGE);
        register(binder);
        register(MultiPartFeature.class);

        // Call post-registration hook to allow for additional registration
        binderFactory.afterRegistration(this);
    }

    @NotNull
    private String getBindingFactory() {
        return bindingFactory;
    }
}
