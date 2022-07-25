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
package com.qubitpi.athena.config;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.inject.Singleton;

/**
 * A class to hold and fetch configuration values from the environment and runtime.
 * <p>
 * {@link  LayeredFileSystemConfig} uses a layered model with the highest priority granted to runtime variable
 * modifications, followed by environment variables. It also uses a Property resource to allow runtime override of
 * configured behavior.
 */
@Singleton
@Immutable
@ThreadSafe
public class LayeredFileSystemConfig implements SystemConfig {

    /**
     * The resource path for local user override of application and default properties.
     */
    private static final String USER_CONFIG_FILE_NAME = "/userConfig.properties";

    /**
     * The resource path for configuring properties within an application.
     */
    private static final String APPLICATION_CONFIG_FILE_NAME = "/applicationConfig.properties";

    /**
     * A composite configuration serving layered configs.
     */
    private final CompositeConfiguration compositeConfiguration;

    /**
     * The 1st-prioritized set of configs.
     */
    private final Properties runtimeProperties;

    /**
     * Constructor.
     */
    public LayeredFileSystemConfig() {
        this.compositeConfiguration = new CompositeConfiguration();
        this.compositeConfiguration.setThrowExceptionOnMissing(true);
        this.compositeConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

        this.runtimeProperties = new Properties();

        // Use PropertiesConfiguration to hold environment variables to ensure the same behaviour as properties files
        final PropertiesConfiguration environmentConfiguration = new PropertiesConfiguration();
        environmentConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            environmentConfiguration.addProperty(entry.getKey(), entry.getValue());
        }

        // ConfigResourceLoader pulls resources in from class path locations.
        final ConfigResourceLoader configResourceLoader = new ConfigResourceLoader();

        final List<Configuration> userConfig;
        try {
            userConfig = configResourceLoader.loadConfigurations(USER_CONFIG_FILE_NAME);
        } catch (final IOException exception) {
            throw new IllegalStateException(exception);
        }

        final List<Configuration> applicationConfig;
        try {
            applicationConfig = configResourceLoader.loadConfigurations(APPLICATION_CONFIG_FILE_NAME);
        } catch (final IOException exception) {
            throw new IllegalStateException(exception);
        }

        // Environment config has higher priority than Java system properties
        // Java system properties have higher priority than file based configuration
        // Also, a runtime map is maintained to support on-the-fly configuration changes
        // Load the rest of the config "top-down" throught layers, in highest to lowest precedence
        Stream.of(
                Stream.of(new MapConfiguration(getRuntimeProperties())),
                Stream.of(environmentConfiguration),
                Stream.of(new SystemConfiguration()),
                userConfig.stream(),
                applicationConfig.stream()
        )
                .flatMap(Function.identity())
                .filter(Objects::nonNull)
                .forEachOrdered(compositeConfiguration::addConfiguration);
    }

    @Override
    public String getPackageVariableName(final String suffix) {
        return "athena" + "__" + suffix;
    }

    @Override
    public Optional<String> getStringProperty(final String key) {
        return getConfig().containsKey(key) ? Optional.of(getConfig().getString(key)) : Optional.empty();
    }

    @Override
    public Optional<Integer> getIntProperty(final String key) {
        return getConfig().containsKey(key) ? Optional.of(getConfig().getInt(key)) : Optional.empty();
    }

    @Override
    public Optional<Boolean> getBooleanProperty(final String key) {
        return getConfig().containsKey(key) ? Optional.of(getConfig().getBoolean(key)) : Optional.empty();
    }

    @Override
    public Optional<Long> getLongProperty(final String key) {
        return getConfig().containsKey(key) ? Optional.of(getConfig().getLong(key)) : Optional.empty();
    }

    @Override
    public Optional<Double> getDoubleProperty(final String key) {
        return getConfig().containsKey(key) ? Optional.of(getConfig().getDouble(key)) : Optional.empty();
    }

    @Override
    public Optional<Float> getFloatProperty(final String key) {
        return getConfig().containsKey(key) ? Optional.of(getConfig().getFloat(key)) : Optional.empty();
    }

    @Override
    public Properties getRuntimeProperties() {
        return runtimeProperties;
    }

    /**
     * Returns the underlying config.
     *
     * @return the config value provider
     */
    @NotNull
    private Configuration getConfig() {
        return compositeConfiguration;
    }
}
