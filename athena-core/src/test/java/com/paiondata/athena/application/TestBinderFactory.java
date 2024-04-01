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
package com.paiondata.athena.application;

import com.paiondata.athena.file.identifier.FileIdGenerator;
import com.paiondata.athena.filestore.FileStore;
import com.paiondata.athena.filestore.TestFileStore;
import com.paiondata.athena.metadata.MetaData;
import com.paiondata.athena.metastore.MetaStore;
import com.paiondata.athena.metastore.TestMetaStore;

import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import graphql.schema.DataFetcher;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Athena test app configuration binder.
 */
public class TestBinderFactory extends AbstractBinderFactory {

    /**
     * A switch indicating whether after-binding is invoked.
     */
    public boolean afterBindingHookWasCalled = false;

    /**
     * A switch indicating whether after-registration is invoked.
     */
    public boolean afterRegistrationHookWasCalled = false;

    private final ApplicationState applicationState;

    /**
     * Constructor used for unit-testing {@link AbstractBinderFactory} extension capability to make sure all bindings
     * occurs.
     *
     * @see AbstractBinderFactorySpec
     */
    public TestBinderFactory() {
        this.applicationState = new ApplicationState();
    }

    /**
     * Constructor for servlet testing where JerseyTest harness and relevant DI are involved.
     *
     * @param applicationState  An entry point for setting up test data
     *
     * @see com.paiondata.athena.web.endpoints.MetaServletSpec
     * @see com.paiondata.athena.web.endpoints.FileServletSpec
     */
    public TestBinderFactory(final ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    @Override
    protected Class<? extends FileStore> buildFileStore() {
        return TestFileStore.class;
    }

    @Override
    protected Class<? extends MetaStore> buildMetaStore() {
        return TestMetaStore.class;
    }

    @Override
    protected DataFetcher<MetaData> buildQueryDataFetcher() {
        return new TestQueryDataFetcher(applicationState.metadataByFileId);
    }

    @Override
    protected DataFetcher<MetaData> buildMutationDataFetcher() {
        return new TestMutationDataFetcher(applicationState.metadataByFileId);
    }

    @Override
    public void afterRegistration(final ResourceConfig resourceConfig) {
        afterRegistrationHookWasCalled = true;
    }

    @Override
    protected void afterBinding(final AbstractBinder abstractBinder) {
        abstractBinder.bind(applicationState.queryFormatter)
                .named("queryFormatter")
                .to(new TypeLiteral<BiFunction<String, List<String>, String>>() { });
        abstractBinder.bind(applicationState.mutationFormatter)
                .named("mutationFormatter")
                .to(new TypeLiteral<BiFunction<String, MetaData, String>>() { });
        abstractBinder.bind(applicationState.fileByFileId)
                .named("fileByFileId")
                .to(new TypeLiteral<Map<String, String>>() { });
        abstractBinder.bind(applicationState.fileIdGenerator)
                .named("fileIdGenerator")
                .to(FileIdGenerator.class);

        afterBindingHookWasCalled = true;
    }
}
