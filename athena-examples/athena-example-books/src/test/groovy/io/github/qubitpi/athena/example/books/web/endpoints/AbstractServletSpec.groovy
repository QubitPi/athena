/*
 * Copyright 2024 Jiaqi Liu
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
package io.github.qubitpi.athena.example.books.web.endpoints

import org.eclipse.jetty.server.Server

import io.github.qubitpi.athena.application.ResourceConfig
import io.github.qubitpi.athena.example.books.JettyServerFactory
import io.github.qubitpi.athena.example.books.application.SQLDBResourceManager
import io.github.qubitpi.athena.config.SystemConfig
import io.github.qubitpi.athena.config.SystemConfigFactory

import io.restassured.RestAssured
import spock.lang.Specification
import spock.lang.Subject

abstract class AbstractServletSpec extends Specification {

    static final SystemConfig SYSTEM_CONFIG = SystemConfigFactory.getInstance()
    static final int PORT = 8080

    @Subject
    Server SERVER

    def setupSpec() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = PORT
        RestAssured.basePath = "/v1"
        SYSTEM_CONFIG.setProperty('athena__data_source_provider', 'io.github.qubitpi.athena.example.books.application.BooksBinderFactory$DerbyDataSourceProvider')
    }

    def childSetup() {
        // intentionally left blank
    }

    def setup() {
        SQLDBResourceManager.migrateDatabase()

        SERVER = JettyServerFactory.newInstance(PORT, "/v1/*", new ResourceConfig())
        SERVER.start()

        childSetup()
    }

    def cleanup() {
        SERVER.stop()
    }
}
