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

import org.glassfish.hk2.utilities.Binder;

import jakarta.validation.constraints.NotNull;

/**
 * A binder factory builds a custom binder for the Jersey application.
 */
public interface BinderFactory {

    /**
     * Builds an hk2 Binder instance.
     * <p>
     * This binder should bind all data dictionaries after loading them, as well as UI/NonUI web services and Health
     * Check metrics
     *
     * @return  a binder instance
     */
    @NotNull
    Binder buildBinder();

    /**
     * Allows additional app-specific Jersey feature registration and config.
     *
     * @param resourceConfig  Resource config to use for accessing the configuration
     */
    void afterRegistration(@NotNull ResourceConfig resourceConfig);
}
