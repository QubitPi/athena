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
package com.qubitpi.athena.metastore;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import com.qubitpi.athena.metadata.MetaData;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Test app meta store.
 */
public class TestMetaStore implements MetaStore {

    private final GraphQL api;
    private final BiFunction<String, List<String>, String> queryFormatter;
    private final BiFunction<String, MetaData, String> mutationFormatter;

    @Inject
    public TestMetaStore(
            final @NotNull @Named("queryDataFetcher") DataFetcher<MetaData> queryDataFetcher,
            final @NotNull @Named("mutationDataFetcher") DataFetcher<MetaData> mutationDataFetcher,
            final @NotNull @Named("queryFormatter") BiFunction<String, List<String>, String> queryFormatter,
            final @NotNull @Named("mutationFormatter") BiFunction<String, MetaData, String> mutationFormatter
    ) throws IOException {
        // https://www.graphql-java.com/documentation/getting-started#hello-world
        String schema = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.graphqls")));

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query").dataFetcher("metaData", queryDataFetcher))
                .type(newTypeWiring("Mutation").dataFetcher("createMetaData", mutationDataFetcher))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

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
