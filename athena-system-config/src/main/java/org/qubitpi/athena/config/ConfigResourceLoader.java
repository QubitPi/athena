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

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Utilities to help load resources for the {@link SystemConfig}.
 */
public class ConfigResourceLoader {

    private static final ConfigResourceLoader INSTANCE = new ConfigResourceLoader();

    /**
     * Singleton factory method.
     *
     * @return a new instance
     */
    @NotNull
    public static ConfigResourceLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Load configurations matching a resource name from the class path and parse into Configuration objects.
     *
     * @param name  The class path address of a resource ('/foo' means a resource named "foo" under resource/ directory)
     *
     * @return a list of configurations corresponding to the matching class path resource
     *
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws IOException if any resource cannot be read from the class path successfully
     */
    @NotNull
    public List<Configuration> loadConfigurations(final @NotNull String name) throws IOException {
        Objects.requireNonNull(name);
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name)) {
            if (inputStream == null) {
                return Collections.emptyList();
            }

            final Properties properties = new Properties();
            properties.load(inputStream);

            final PropertiesConfiguration configuration = new PropertiesConfiguration();
            configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

            properties.forEach((key, value) -> configuration.addProperty(key.toString(), value));

            return Collections.singletonList(configuration);
        }
    }
}
