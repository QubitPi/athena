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
package org.qubitpi.athena.application

import org.qubitpi.athena.file.identifier.FileIdGenerator
import org.qubitpi.athena.file.identifier.FileNameAndUploadedTimeBasedIdGenerator
import org.qubitpi.athena.filestore.FileStore
import org.qubitpi.athena.metastore.MetaStore
import org.qubitpi.athena.web.graphql.JacksonParser
import org.qubitpi.athena.web.graphql.JsonDocumentParser

import org.glassfish.hk2.api.DynamicConfiguration
import org.glassfish.hk2.utilities.Binder
import org.qubitpi.athena.filestore.TestFileStore
import org.qubitpi.athena.metastore.TestMetaStore

import graphql.schema.DataFetcher
import spock.lang.Shared
import spock.lang.Specification

class AbstractBinderFactorySpec extends Specification {

    @Shared
    TestBinderFactory binderFactory

    def setup() {
        binderFactory = new TestBinderFactory()
    }

    def "Test configure bindings"() {
        given: "an mocked HK2 Descriptor binder "
        DynamicConfiguration dynamicConfiguration = Mock(DynamicConfiguration)

        when: "the Descriptor binder is used for resource configuration"
        Binder binder = binderFactory.buildBinder()
        binder.bind(dynamicConfiguration)

        then: "the resource configuration object is present"
        binder != null

        and: "object storage resource is injected"
        1 * dynamicConfiguration.bind(
                {
                    it.advertisedContracts.contains(FileStore.canonicalName)
                    it.implementation.contains(TestFileStore.canonicalName)
                },
                _
        )

        and: "meta data storage resource is injected"
        1 * dynamicConfiguration.bind(
                {
                    it.advertisedContracts.contains(MetaStore.canonicalName)
                    it.implementation.contains(TestMetaStore.canonicalName)
                },
                _
        )

        and: "file ID generator resource is injected"
        1 * dynamicConfiguration.bind(
                {
                    it.advertisedContracts.contains(FileIdGenerator.canonicalName)
                    it.implementation.contains(FileNameAndUploadedTimeBasedIdGenerator.canonicalName)
                },
                _
        )

        and: "JSON document parser resource is injected"
        1 * dynamicConfiguration.bind(
                {
                    it.advertisedContracts.contains(JsonDocumentParser.canonicalName)
                    it.implementation.contains(JacksonParser.canonicalName)
                },
                _
        )

        and: "GraphQL resource for reading meta data is injected"
        1 * dynamicConfiguration.bind(
                {
                    it.advertisedContracts.contains(DataFetcher.canonicalName)
                    it.implementation.contains(TestQueryDataFetcher.canonicalName)
                },
                _
        )

        and: "GraphQL resource for persisting meta data is injected"
        1 * dynamicConfiguration.bind(
                {
                    it.advertisedContracts.contains(DataFetcher.canonicalName)
                    it.implementation.contains(TestMutationDataFetcher.canonicalName)
                },
                _
        )
    }

    def "BindAtEnd is called when binding"() {
        given: "an AbstractBinder"
        DynamicConfiguration dynamicConfiguration = Mock(DynamicConfiguration)

        when: "Bind is called"
        binderFactory.buildBinder().bind(dynamicConfiguration)

        then: "The afterBinding hook was called"
        binderFactory.afterBindingHookWasCalled
    }
}
