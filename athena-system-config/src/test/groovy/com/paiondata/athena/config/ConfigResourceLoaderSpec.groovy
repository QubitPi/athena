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
package com.paiondata.athena.config

import org.apache.commons.configuration2.Configuration

import spock.lang.Specification
import spock.lang.Subject

class ConfigResourceLoaderSpec extends Specification {

    @Subject
    ConfigResourceLoader configResourceLoader

    def setup() {
        configResourceLoader = new ConfigResourceLoader();
    }

    def "Static factory method always returns the same instance"() {
        expect:
        ConfigResourceLoader.getInstance() is ConfigResourceLoader.getInstance()
    }

    def "Load configurations by name works"() {
        when:
        List<Configuration> configurations = configResourceLoader.loadConfigurations("sysConfigTestApplication.properties")

        then:
        configurations.collect{ it.getString("athena__resource_binder") } as Set ==
                ["com.paiondata.athena.example.books.application.BooksBinderFactory"] as Set
    }
}
