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
package io.github.qubitpi.athena.metastore.graphql;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import io.github.qubitpi.athena.metadata.MetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Objects;
import java.util.Scanner;

import jakarta.inject.Singleton;

/**
 * {@link GraphQLFactory} initializes {@link GraphQL native GraphQL API}.
 */
@Singleton
@Immutable
@ThreadSafe
public class GraphQLFactory {

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLFactory.class);

    private final GraphQL api;

    /**
     * Constructor.
     *
     * @param queryDataFetcher  An application defined logic for retrieving file metadata from various databases
     * @param mutationDataFetcher An application defined logic for saving/updating file metadata into various databases
     *
     * @throws NullPointerException if {@code queryDataFetcher} or {@code mutationDataFetcher} is {@code null}
     */
    public GraphQLFactory(
            final @NotNull DataFetcher<MetaData> queryDataFetcher,
            final @NotNull DataFetcher<MetaData> mutationDataFetcher
    ) {
        this.api = init(
                Objects.requireNonNull(queryDataFetcher),
                Objects.requireNonNull(mutationDataFetcher)
        );
    }

    /**
     * Loads a resource file into a single {@code String} object.
     *
     * @param resourceName cannot be null
     *
     * @return a resource file content
     *
     * @throws NullPointerException if {@code resourceName} is {@code null}
     */
    @NotNull
    public static String getGraphQLSchemaResourceAsString(@NotNull final String resourceName) {
        @SuppressWarnings("ConstantConditions")
        final Scanner scanner = new Scanner(
                Thread
                        .currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(Objects.requireNonNull(resourceName))
        )
                .useDelimiter("\\A");

        if (scanner.hasNext()) {
            return scanner.next();
        }

        final String message = String.format("GraphQL schema file not found: '%s'", resourceName);
        LOG.error(message);
        throw new IllegalStateException(message);
    }

    @NotNull
    public GraphQL getApi() {
        return api;
    }

    /**
     * Initializes and returns an instance of {@link GraphQL native GraphQL API}.
     *
     * @param queryDataFetcher  An application defined logic for retrieving file metadata from various databases
     * @param mutationDataFetcher An application defined logic for saving/updating file metadata into various databases
     *
     * @return a new instance
     */
    @NotNull
    private static GraphQL init(
            final @NotNull DataFetcher<MetaData> queryDataFetcher,
            final @NotNull DataFetcher<MetaData> mutationDataFetcher
    ) {
        final String schemaString = getGraphQLSchemaResourceAsString("schema.graphqls");
        final GraphQLSchema graphQLSchema = buildSchema(schemaString, queryDataFetcher, mutationDataFetcher);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    /**
     * Initializes and returns an instance of {@link GraphQLSchema native GraphQL schema object}.
     *
     * @param schemaString  The schema definition, i.e. file metadata fields
     * @param queryDataFetcher  An application defined logic for retrieving file metadata from various databases
     * @param mutationDataFetcher An application defined logic for saving/updating file metadata into various databases
     *
     * @return a new instance
     */
    @NotNull
    private static GraphQLSchema buildSchema(
            final @NotNull String schemaString,
            final @NotNull DataFetcher<MetaData> queryDataFetcher,
            final @NotNull DataFetcher<MetaData> mutationDataFetcher
    ) {
        final TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaString);
        final RuntimeWiring runtimeWiring = buildWiring(queryDataFetcher, mutationDataFetcher);
        final SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    /**
     * Loads specifications of data fetchers.
     *
     * @param queryDataFetcher  An application defined logic for retrieving file metadata from various databases
     * @param mutationDataFetcher An application defined logic for saving/updating file metadata into various databases
     *
     * @return a specification of data fetchers, type resolvers and custom scalars that are needed to wire together a
     * functional {@link GraphQLSchema}
     */
    private static RuntimeWiring buildWiring(
            final @NotNull DataFetcher<MetaData> queryDataFetcher,
            final @NotNull DataFetcher<MetaData> mutationDataFetcher
    ) {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query").dataFetcher("metaData", queryDataFetcher))
                .type(newTypeWiring("Mutation").dataFetcher("createMetaData", mutationDataFetcher))
                .build();
    }
}
