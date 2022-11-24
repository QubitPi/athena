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

/**
 * Common message formats for errors.
 */
public enum ErrorMessageFormat implements MessageFormatter {

    /**
     * When a {@link SystemConfig} doesn't find a config key.
     */
    CONFIG_NOT_FOUND("Config error. Please check service log for details", "Config '%s' not found."),

    /**
     * When {@link SystemConfig} initialization fails.
     */
    SYSTEM_CONFIG_LOADING_ERROR_MESSAGE("Service config error.", "Exception while loading system configuration"),

    /**
     * When a {@link java.util.List} instance is empty and is not allowed.
     */
    EMPTY_LIST("Athena could not process the request due to an internal error.", "List cannot be empty"),

    /**
     * When a {@link java.util.Map} is missing a required key.
     */
    MISSING_MAP_KEY(EMPTY_LIST.messageFormat, "Missing key '%s' in map '%s'"),

    /**
     * When an invalid JSON is seen.
     */
    JSON_DESERIALIZATION_ERROR(
            "Athena could not process the request because HTTP request body is not properly JSON-formatted: '%s'",
            "Invalid JSON: '%s'"
    ),

    /**
     * When an invalid GraphQL query is seen.
     */
    INVALID_GRAPHQL_REQUEST(
            "Athena could not process the request because %s: '%s'",
            "Invalid GraphQL query (cause: %s): '%s'"
    ),

    /**
     * When meta data not found.
     */
    META_DATA_NOT_FOUND("No meta data found for file ID '%s'");

    private final String messageFormat;
    private final String loggingFormat;

    /**
     * An error message formatter with the same message for logging and messaging.
     *
     * @param messageFormat The format string for logging and messaging
     * @throws NullPointerException if any {@code messageFormat} is {@code null}
     */
    ErrorMessageFormat(final @NotNull String messageFormat) {
        this(messageFormat, messageFormat);
    }

    /**
     * Constructor.
     *
     * @param messageFormat User facing message format
     * @param loggingFormat Server log message format
     * @throws NullPointerException if any argument is {@code null}
     */
    ErrorMessageFormat(final @NotNull String messageFormat, final @NotNull String loggingFormat) {
        this.messageFormat = Objects.requireNonNull(messageFormat);
        this.loggingFormat = Objects.requireNonNull(loggingFormat);
    }

    @Override
    public String getMessageFormat() {
        return messageFormat;
    }

    @Override
    public String getLoggingFormat() {
        return loggingFormat;
    }
}
