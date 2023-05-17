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

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * {@link SystemConfig} is an interface for retrieving configuration values, allowing for implicit type conversion and
 * use of a runtime properties interface to override configured settings.
 */
public interface SystemConfig {

    /**
     * Sets property value for a key.
     *
     * @param key  The key of the property to change
     * @param value  The value to set to
     *
     * @throws NullPointerException if either {@code key} or {@code value} is {@code null}
     */
    default void setProperty(final @NotNull String key, final @NotNull String value) {
        getRuntimeProperties().setProperty(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    /**
     * Updates a property from the user-defined runtime configuration.
     *
     * @param key  The key of the property to update
     * @param value  The value to set to
     *
     * @throws NullPointerException if either {@code key} or {@code value} is {@code null}
     */
    default void resetProperty(final @NotNull String key, final @NotNull String value) {
        setProperty(key, value);
    }

    /**
     * Removes property from the use-defined runtime configuration.
     *
     * @param key  The key of the property to remove
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    default void clearProperty(final @NotNull String key) {
        getRuntimeProperties().remove(Objects.requireNonNull(key));
    }

    /**
     * Gets a package scoped variable name.
     *
     * @param suffix  The variable name of the configuration variable without the package prefix
     *
     * @return variable name
     *
     * @throws NullPointerException if {@code suffix} is {@code null}
     */
    @NotNull
    String getPackageVariableName(@NotNull String suffix);

    /**
     * Returns property value as String value wrapped inside an {@link Optional} for a key or
     * {@link Optional#empty() empty} if no such property exists.
     *
     * @param key  The key for which value needs to be fetched
     *
     * @return an {@link Optional} value for the requested key or {@link Optional#empty()}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @NotNull
    Optional<String> getStringProperty(@NotNull String key);

    /**
     * Returns property value as int value wrapped inside an {@link Optional} for a key or
     * {@link Optional#empty() empty} if no such property exists.
     *
     * @param key  The key for which value needs to be fetched
     *
     * @return an {@link Optional} value for the requested key or {@link Optional#empty()}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @NotNull
    Optional<Integer> getIntProperty(@NotNull String key);

    /**
     * Returns property value as boolean value wrapped inside an {@link Optional} for a key or
     * {@link Optional#empty() empty} if no such property exists.
     *
     * @param key  The key for which value needs to be fetched
     *
     * @return an {@link Optional} value for the requested key or {@link Optional#empty()}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @NotNull
    Optional<Boolean> getBooleanProperty(@NotNull String key);

    /**
     * Returns property value as long value wrapped inside an {@link Optional} for a key or
     * {@link Optional#empty() empty} if no such property exists.
     *
     * @param key  The key for which value needs to be fetched
     *
     * @return an {@link Optional} value for the requested key or {@link Optional#empty()}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @NotNull
    Optional<Long> getLongProperty(@NotNull String key);

    /**
     * Returns property value as a double wrapped inside an {@link Optional} for a key or {@link Optional#empty() empty}
     * if no such property exists.
     *
     * @param key  The key for which value needs to be fetched
     *
     * @return an {@link Optional} value for the requested key or {@link Optional#empty()}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @NotNull
    Optional<Double> getDoubleProperty(@NotNull String key);

    /**
     * Returns property value as float value wrapped inside an {@link Optional} for a key or
     * {@link Optional#empty() empty} if no such property exists.
     *
     * @param key  The key for which value needs to be fetched
     *
     * @return an {@link Optional} value for the requested key or {@link Optional#empty()}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @NotNull
    Optional<Float> getFloatProperty(@NotNull String key);

    /**
     * Returns the properties used to hold the highest-priority config values.
     * <p>
     * <b>This method is intended primarily for interface support and not for client interactions</b>
     * <p>
     * Notes: this is a design flaw based on how it is used in method such as {@link #setProperty(String, String)}. If
     * implementations produces a shallow copy of the runtime properties, then {@link #setProperty(String, String)} will
     * fail to accomplish what it supposed to do. The fact that this method is "not being called by client" pushes us to
     * look at problem at a higher level: this method assumes the returned properties are directly mutable and will
     * mutate the config instance directly. This violates encapsulation. we should remove this method and ask client to
     * implement {@link #setProperty(String, String)}
     *
     * @return a properties object which act as a runtime mask against other configuration properties
     */
    @NotNull
    Properties getRuntimeProperties();
}
