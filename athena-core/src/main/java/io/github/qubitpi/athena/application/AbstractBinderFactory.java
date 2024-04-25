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
package io.github.qubitpi.athena.application;

import io.github.qubitpi.athena.file.identifier.FileIdGenerator;
import io.github.qubitpi.athena.file.identifier.FileIdGeneratorFactory;
import io.github.qubitpi.athena.filestore.FileStore;
import io.github.qubitpi.athena.metadata.MetaData;
import io.github.qubitpi.athena.metastore.MetaStore;
import io.github.qubitpi.athena.web.graphql.JacksonParser;
import io.github.qubitpi.athena.web.graphql.JsonDocumentParser;

import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import graphql.schema.DataFetcher;
import jakarta.validation.constraints.NotNull;

/**
 * {@link AbstractBinderFactory} implements standard buildBinder functionality.
 * <p>
 * It is left to individual projects to subclass, providing {@link FileStore} and {@link MetaStore} classes, etc.
 */
public abstract class AbstractBinderFactory implements BinderFactory {

    @Override
    public Binder buildBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(buildFileStore()).to(FileStore.class);
                bind(buildMetaStore()).to(MetaStore.class);
                bind(buildFileIdGenerator()).to(FileIdGenerator.class);
                bind(buildJsonDocumentParser()).to(JsonDocumentParser.class);
                bind(buildQueryDataFetcher())
                        .named("queryDataFetcher")
                        .to(new TypeLiteral<DataFetcher<MetaData>>() { });
                bind(buildMutationDataFetcher())
                        .named("mutationDataFetcher")
                        .to(new TypeLiteral<DataFetcher<MetaData>>() { });

                afterBinding(this);
            }
        };
    }

    @Override
    public void afterRegistration(final ResourceConfig resourceConfig) {
        // No-ops by default
    }

    /**
     * Registers file data storage.
     *
     * @return a service for persisting and fetching files
     */
    @NotNull
    protected abstract Class<? extends FileStore> buildFileStore();

    /**
     * Registers GraphQL service that provides file metadata read/write API.
     *
     * @return a service for persisting
     */
    @NotNull
    protected abstract Class<? extends MetaStore> buildMetaStore();

    /**
     * Registers an object responsible for retrieving, from metadata database, a data value back for a given graphql
     * field, i.e. {@link MetaData} object; the graphql engine uses this data fetcher to resolve/fetch a
     * {@link MetaData} into a runtime object that will be sent back as part of the overall graphql
     * {@link graphql.ExecutionResult}
     *
     * @return a native GraphQL {@link DataFetcher} instance
     */
    @NotNull
    protected abstract DataFetcher<MetaData> buildQueryDataFetcher();

    /**
     * Similar to {@link #buildQueryDataFetcher()}, this method binds an object responsible for executing the mutation
     * and returning some sensible output values of {@link MetaData}.
     *
     * @return a native GraphQL {@link DataFetcher} instance
     *
     * @see <a href="https://www.graphql-java.com/documentation/execution#mutations">GraphQL Mutations (Java)</a>
     */
    @NotNull
    protected abstract DataFetcher<MetaData> buildMutationDataFetcher();

    /**
     * Initializes service for generating file ID's.
     *
     * @return a new instance of {@link FileIdGenerator}
     *
     * @throws IllegalStateException if an internal error occurs
     */
    @NotNull
    protected FileIdGenerator buildFileIdGenerator() {
        return FileIdGeneratorFactory.getInstance();
    }

    /**
     * Initializes service for parsing client GraphQL request JSON.
     *
     * @return a new instance
     */
    @NotNull
    protected JsonDocumentParser buildJsonDocumentParser() {
        return JacksonParser.getInstance();
    }

    /**
     * Allows additional app-specific binding.
     *
     * @param abstractBinder  Binder to use for binding
     */
    protected void afterBinding(final @NotNull AbstractBinder abstractBinder) {
        // No-ops by default
    }
}
