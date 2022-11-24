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
package com.qubitpi.athena.metastore.graphql;

import static com.qubitpi.athena.config.ErrorMessageFormat.EMPTY_LIST;

import com.qubitpi.athena.metadata.MetaData;
import com.qubitpi.athena.metastore.MetaStore;
import com.qubitpi.athena.metastore.graphql.query.GraphQLQueryProvider;
import com.qubitpi.athena.metastore.graphql.query.GraphQLQueryProviderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.NotThreadSafe;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * The default implementation of {@link MetaStore}.
 * <p>
 * {@link GraphQLMetaStore} is basically the Athena's implementation of GraphQL server API.
 */
@Singleton
@NotThreadSafe
public class GraphQLMetaStore implements MetaStore {

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLMetaStore.class);

    private final GraphQL graphQL;
    private final GraphQLQueryProvider graphQLQueryProvider;

    /**
     * DI constructor.
     *
     * @param queryDataFetcher  An application defined logic for retrieving file metadata from various databases
     * @param mutationDataFetcher An application defined logic for saving/updating file metadata into various databases
     *
     * @throws NullPointerException if {@code queryDataFetcher} or {@code mutationDataFetcher} is {@code null}
     */
    @Inject
    public GraphQLMetaStore(
            final @NotNull @Named("queryDataFetcher") DataFetcher<MetaData> queryDataFetcher,
            final @NotNull @Named("mutationDataFetcher") DataFetcher<MetaData> mutationDataFetcher
    ) {
        this(
                new GraphQLFactory(
                        Objects.requireNonNull(queryDataFetcher),
                        Objects.requireNonNull(mutationDataFetcher)
                )
                        .getApi(),
                GraphQLQueryProviderFactory.getInstance()
        );
    }

    /**
     * All-args constructor.
     *
     * @param graphQL  The native GraphQL API
     * @param graphQLQueryProvider  An abstraction layer that returns native GraphQL query given a set of file metadata
     * domain spec
     *
     * @throws NullPointerException if {@code graphQL} or {@code graphQLQueryProvider} is {@code null}
     */
    private GraphQLMetaStore(@NotNull final GraphQL graphQL, @NotNull final GraphQLQueryProvider graphQLQueryProvider) {
        this.graphQL = Objects.requireNonNull(graphQL);
        this.graphQLQueryProvider = Objects.requireNonNull(graphQLQueryProvider);
    }

    @Override
    public ExecutionResult executeNative(final String query) {
        return getGraphQL().execute(Objects.requireNonNull(query));
    }

    @Override
    public ExecutionResult getMetaData(final String fileId, final List<String> metadataFields) {
        Objects.requireNonNull(fileId);
        Objects.requireNonNull(metadataFields);
        if (metadataFields.isEmpty()) {
            LOG.error(EMPTY_LIST.logFormat());
            throw new IllegalArgumentException(EMPTY_LIST.format());
        }

        return getGraphQL().execute(
                ExecutionInput.newExecutionInput()
                        .query(getGraphQLQueryProvider().query(fileId, metadataFields))
                        .build()
        );
    }

    @Override
    public void saveMetaData(final String fileId, final MetaData metaData) {
        getGraphQL().execute(
                getGraphQLQueryProvider().mutation(
                        Objects.requireNonNull(fileId),
                        Objects.requireNonNull(metaData)
                )
        );
    }

    @NotNull
    private GraphQL getGraphQL() {
        return graphQL;
    }

    @NotNull
    private GraphQLQueryProvider getGraphQLQueryProvider() {
        return graphQLQueryProvider;
    }
}
