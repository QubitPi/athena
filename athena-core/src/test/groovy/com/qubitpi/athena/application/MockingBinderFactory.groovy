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
package com.qubitpi.athena.application

import static com.qubitpi.athena.application.ResourceConfigSpec.getBinder
import static com.qubitpi.athena.application.ResourceConfigSpec.getClicker

import org.glassfish.hk2.utilities.Binder

import jakarta.validation.constraints.NotNull

/**
 * A class to Mock ResourceBinding
 */
class MockingBinderFactory implements BinderFactory {

    public static final String INIT = "init"
    public static final String BUILD_BIND = "build_binder"

    MockingBinderFactory() {
        getClicker().accept(INIT)
    }

    @Override
    Binder buildBinder() {
        getClicker().accept(BUILD_BIND)
        return getBinder()
    }

    @Override
    void afterRegistration(final ResourceConfig resourceConfig) {
        getClicker().accept(resourceConfig)
    }
}
