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
package com.paiondata.athena.example.books.application

import com.paiondata.athena.config.SystemConfig
import com.paiondata.athena.config.SystemConfigFactory

import spock.lang.Specification
import spock.lang.Unroll

import jakarta.inject.Provider
import javax.sql.DataSource

class BooksBinderFactorySpec extends Specification {

    static final SystemConfig SYSTEM_CONFIG = SystemConfigFactory.instance

    @Unroll
    @SuppressWarnings('GroovyAccessibility')
    def "#providerClass produces singleton DataSource"() {
        given: "a provider instance"
        Provider<DataSource> provider = BooksBinderFactory.initProvider(providerClass)

        expect: "getting DataSource from it twice results in the same DataSource object"
        provider.get() is provider.get()

        where:
        [providerClass] << getTestIterationData()
    }

    @Unroll
    @SuppressWarnings('GroovyAccessibility')
    def "When #providerClass is specified, #dbType DataSource provider is instantiated"() {
        setup: "provider config is specified at runtime"
        SYSTEM_CONFIG.setProperty('athena__data_source_provider', providerClass)

        expect: "re-initiating factory reloads a new provider"
        new BooksBinderFactory().dataSourceProvider.class.simpleName == "${dbType}DataSourceProvider"

        where:
        [providerClass, dbType] << getTestIterationData()
    }

    @Unroll
    @SuppressWarnings('GroovyAccessibility')
    def "Happy path loads #providerClass" () {
        when: "a loadable provider class is trying to be instantiated"
        BooksBinderFactory.initProvider(providerClass)

        then: "no error occurs"
        noExceptionThrown()

        where:
        [providerClass] << getTestIterationData()
    }

    @SuppressWarnings('GroovyAccessibility')
    def "When DataSource Provider is not on classpath, a runtime exception occurs"() {
        when: "trying to instantiate a non-existing provider from classpath"
        BooksBinderFactory.initProvider("non.existing.datasource.provider")

        then:
        Exception exception = thrown(IllegalStateException)
        exception.message == "Cannot locate DataSource provider class 'non.existing.datasource.provider'"
    }

    @SuppressWarnings('GroovyResultOfObjectAllocationIgnored')
    def "When a DataSource Provider classpath config value is not found, runtime error occurs"() {
        setup: "provider class name is made non-existent"
        SYSTEM_CONFIG.clearProperty("athena__data_source_provider")

        when: "binding happens"
        new BooksBinderFactory()

        then: "error occurs"
        Exception exception = thrown(IllegalStateException)
        exception.message == "Config error. Please check service log for details"
    }

    /**
     * Returns the data to use in where blocks as a list of lists.
     *
     * @return the data as a list of lists
     */
    def getTestIterationData() {
        [
                ['com.paiondata.athena.example.books.application.BooksBinderFactory$DerbyDataSourceProvider', "Derby"],
                ['com.paiondata.athena.example.books.application.BooksBinderFactory$MySQLDataSourceProvider', "MySQL"]
        ]
    }
}
