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
package org.qubitpi.athena.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;

/**
 * {@link SystemConfigFactory} provides a {@link SystemConfig} instance.
 */
public final class SystemConfigFactory {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SystemConfigFactory.class);

    /**
     * The config key for the {@link SystemConfig} implementation value, which is a class name.
     */
    private static final String SYSTEM_CONFIG_IMPL_KEY = "athena__system_config_impl";

    /**
     * The default {@link SystemConfig} implementation value, which is a class name.
     */
    private static final String DEFAULT_SYSTEM_CONFIG_IMPL = LayeredFileSystemConfig.class.getCanonicalName();

    /**
     * The instance of the {@link SystemConfig} available in this system.
     */
    private static final SystemConfig SYSTEM_CONFIG = getInstance();

    /**
     * Constructor.
     * <p>
     * Suppress default constructor for noninstantiability.
     *
     * @throws AssertionError when called
     */
    private SystemConfigFactory() {
        throw new AssertionError();
    }

    /**
     * Returns an instance of {@link SystemConfig}.
     *
     * @return a new instance
     *
     * @throws IllegalStateException if the {@link SystemConfig} implementation class cannot be instantiated
     */
    @NotNull
    public static synchronized SystemConfig getInstance() {
        if (SYSTEM_CONFIG == null) {
            String systemConfigImplementation = System.getenv(SYSTEM_CONFIG_IMPL_KEY);

            if (systemConfigImplementation == null) {
                systemConfigImplementation = System.getProperty(SYSTEM_CONFIG_IMPL_KEY, DEFAULT_SYSTEM_CONFIG_IMPL);
            }

            try {
                return (SystemConfig) Class.forName(systemConfigImplementation).newInstance();
            } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                LOG.error(ErrorMessageFormat.CLASS_LOADING_ERROR.logFormat(systemConfigImplementation));
                throw new IllegalStateException(
                        ErrorMessageFormat.CLASS_LOADING_ERROR.format(),
                        exception
                );
            }
        }

        return SYSTEM_CONFIG;
    }
}
