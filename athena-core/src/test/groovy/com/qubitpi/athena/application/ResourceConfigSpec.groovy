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

import com.qubitpi.athena.config.SystemConfig
import com.qubitpi.athena.config.SystemConfigFactory

import org.glassfish.hk2.utilities.Binder

import spock.lang.Specification

import java.lang.reflect.InvocationTargetException
import java.util.function.Consumer

class ResourceConfigSpec extends Specification {

    static final SystemConfig SYSTEM_CONFIG = SystemConfigFactory.getInstance()
    static final String BINDER_KEY = SYSTEM_CONFIG.getPackageVariableName("resource_binder")

    static Binder binder // A mock representing the binder produced by the BinderFactory
    static Consumer clicker // A mock to arbitrarily accept events for testing

    Set<Class> filters
    Class<org.glassfish.jersey.server.ResourceConfig> resourceConfigClass

    def setup() {
        SYSTEM_CONFIG.setProperty(BINDER_KEY, MockingBinderFactory.canonicalName)
        clicker = Mock(Consumer)
        binder = Mock(Binder)
        resourceConfigClass = ResourceConfig

        filters = []
    }

    def cleanup() {
        binder = null
        clicker = null
        SYSTEM_CONFIG.clearProperty(BINDER_KEY)
    }

    static Binder getBinder() {
        return binder
    }

    static Consumer getClicker() {
        return clicker
    }

    def "Test instantiation triggers initialization and binding lifecycle"() {
        when:
        ResourceConfig config = resourceConfigClass.getDeclaredConstructor().newInstance() as ResourceConfig

        then:
        config.classes.containsAll(filters)
        config.getInstances().contains(binder)

        1 * clicker.accept(MockingBinderFactory.INIT)
        1 * clicker.accept(MockingBinderFactory.BUILD_BIND)
        1 * clicker.accept(_ as ResourceConfig)
    }

    def "When binding factory is not found, error is thrown"() {
        setup: "binder factory description config is removed"
        SYSTEM_CONFIG.clearProperty(BINDER_KEY)

        when: "resource config is constructed"
        resourceConfigClass.getDeclaredConstructor().newInstance()

        then: "new instance cannot be constructed"
        thrown(InvocationTargetException)
    }
}
