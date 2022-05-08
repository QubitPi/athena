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

import com.qubitpi.athena.filestore.FileStore;
import com.qubitpi.athena.filestore.TestFileStore;
import com.qubitpi.athena.metadata.MetaData;
import com.qubitpi.athena.metastore.MetaStore;
import com.qubitpi.athena.metastore.TestMetaStore;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import graphql.schema.DataFetcher;

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
        return new TestQueryDataFetcher();
    }

    @Override
    protected DataFetcher<MetaData> buildMutationDataFetcher() {
        return new TestMutationDataFetcher();
    }

    @Override
    public void afterRegistration(final ResourceConfig resourceConfig) {
        afterRegistrationHookWasCalled = true;
    }

    @Override
    protected void afterBinding(final AbstractBinder abstractBinder) {
        afterBindingHookWasCalled = true;
    }
}
