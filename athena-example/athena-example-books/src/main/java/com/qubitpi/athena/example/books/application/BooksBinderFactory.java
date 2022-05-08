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
package com.qubitpi.athena.example.books.application;

import com.qubitpi.athena.application.AbstractBinderFactory;
import com.qubitpi.athena.example.books.graphql.MutationBookDataFetcher;
import com.qubitpi.athena.example.books.graphql.QueryBookDataFetcher;
import com.qubitpi.athena.filestore.FileStore;
import com.qubitpi.athena.filestore.swift.SwiftFileStore;
import com.qubitpi.athena.metadata.FileType;
import com.qubitpi.athena.metadata.MetaData;
import com.qubitpi.athena.metastore.MetaStore;
import com.qubitpi.athena.metastore.graphql.GraphQLMetaStore;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;

import graphql.schema.DataFetcher;
import jakarta.validation.constraints.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Book specialization of the Abstract Binder Factory, applying Book app configuration objects.
 */
public class BooksBinderFactory extends AbstractBinderFactory {

    private static final Map<String, Object> HARRY_POTTER = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_NAME, "Harry Potter"),
            new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_TYPE, FileType.PDF.name())
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    private static final Map<String, Object> MOBY_DICK = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_NAME, "Moby Dick"),
            new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_TYPE, "PDF")
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    private static final Map<String, Object> INTERVIEW_WITH_THE_VAMPIRE = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_NAME, "Interview with the vampire"),
            new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_TYPE, FileType.PDF.name())
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static final Map<String, MetaData> INITIAL_METADATA = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>("1", MetaData.of(HARRY_POTTER)),
            new AbstractMap.SimpleImmutableEntry<>("2", MetaData.of(MOBY_DICK)),
            new AbstractMap.SimpleImmutableEntry<>("3", MetaData.of(INTERVIEW_WITH_THE_VAMPIRE))
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    protected Class<? extends FileStore> buildFileStore() {
        return SwiftFileStore.class;
    }

    @Override
    protected Class<? extends MetaStore> buildMetaStore() {
        return GraphQLMetaStore.class;
    }

    @Override
    protected DataFetcher<MetaData> buildQueryDataFetcher() {
        return new QueryBookDataFetcher(INITIAL_METADATA);
    }

    @Override
    protected DataFetcher<MetaData> buildMutationDataFetcher() {
        return new MutationBookDataFetcher(INITIAL_METADATA);
    }

    @Override
    protected void afterBinding(final AbstractBinder abstractBinder) {
        final Account account = buildAccount();

        final Container container = account.getContainer(SwiftFileStore.DEFAULT_CONTAINER);

        if (!container.exists()) {
            container.create();
            container.makePublic();
        }

        abstractBinder.bind(account).to(Account.class);
    }

    /**
     * Creates an in-memory implementation of the OpenStackClient.
     *
     * @return a new instance
     */
    @NotNull
    private Account buildAccount() {
        return new AccountFactory()
                .setUsername("chris:chris1234")
                .setPassword("testing")
                .setAuthUrl("http://127.0.0.1:12345/auth/v1.0")
                .setAuthenticationMethod(AuthenticationMethod.BASIC)
                .setMock(true)
                .createAccount();
    }
}
