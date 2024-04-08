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
package io.github.qubitpi.athena.metastore;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import io.github.qubitpi.athena.metadata.MetaData;

import io.github.qubitpi.athena.application.JerseyTestBinder;
import io.github.qubitpi.athena.application.TestBinderFactory;
import io.github.qubitpi.athena.application.TestMutationDataFetcher;
import io.github.qubitpi.athena.application.TestQueryDataFetcher;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.NotThreadSafe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * A {@link MetaStore} test stub that facilitates MetaServletSpec mocking through {@link TestBinderFactory} and
 * {@link JerseyTestBinder}.
 * <p>
 * The canned answer to calls made to {@link MetaStore} during the test is defined by
 * {@link TestQueryDataFetcher} and
 * {@link TestMutationDataFetcher}
 */
@NotThreadSafe
public class TestMetaStore implements MetaStore {

    @GuardedBy("this")
    private final GraphQL api;

    @GuardedBy("this")
    private final BiFunction<String, List<String>, String> queryFormatter;

    @GuardedBy("this")
    private final BiFunction<String, MetaData, String> mutationFormatter;

    /**
     * DI Constructor.
     *
     * @param queryDataFetcher  A test defined logic for retrieving file metadata from memory
     * @param mutationDataFetcher  A test defined logic for saving/updating file metadata into memory
     * @param queryFormatter  A per-test def defined logic that overrides the file meta data GraphQL query
     * @param mutationFormatter  A per-test def defined logic that overrides the file meta data GraphQL mutation query
     *
     * @throws IOException if an error occurs while reading GraphQL schema file
     */
    @Inject
    public TestMetaStore(
            final @NotNull @Named("queryDataFetcher") DataFetcher<MetaData> queryDataFetcher,
            final @NotNull @Named("mutationDataFetcher") DataFetcher<MetaData> mutationDataFetcher,
            final @NotNull @Named("queryFormatter") BiFunction<String, List<String>, String> queryFormatter,
            final @NotNull @Named("mutationFormatter") BiFunction<String, MetaData, String> mutationFormatter
    ) throws IOException {
        // https://www.graphql-java.com/documentation/getting-started#hello-world
        final String schema = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.graphqls")));

        final SchemaParser schemaParser = new SchemaParser();
        final TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        final RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query").dataFetcher("metaData", queryDataFetcher))
                .type(newTypeWiring("Mutation").dataFetcher("createMetaData", mutationDataFetcher))
                .build();

        final SchemaGenerator schemaGenerator = new SchemaGenerator();
        final GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        this.api = GraphQL.newGraphQL(graphQLSchema).build();
        this.queryFormatter = queryFormatter;
        this.mutationFormatter = mutationFormatter;
    }

    @Override
    public ExecutionResult executeNative(final String query) {
        return api.execute(query);
    }

    @Override
    public ExecutionResult getMetaData(final String fileId, final List<String> metadataFields) {
        return api.execute(
                ExecutionInput.newExecutionInput()
                        .query(queryFormatter.apply(fileId, metadataFields))
                        .build()
        );
    }

    @Override
    public void saveMetaData(final String fileId, final MetaData metaData) {
        api.execute(
                mutationFormatter.apply(
                        Objects.requireNonNull(fileId),
                        Objects.requireNonNull(metaData)
                )
        );
    }
}
