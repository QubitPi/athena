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
package com.paiondata.athena.metastore.graphql.query;

import static com.paiondata.athena.config.ErrorMessageFormat.EMPTY_LIST;

import com.paiondata.athena.metadata.MetaData;
import com.paiondata.athena.metastore.graphql.GraphQLFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.Objects;

import jakarta.inject.Singleton;

/**
 * The default implementation of {@link GraphQLQueryProvider}.
 * <p>
 * The implementation constructs query simply by filling a String format with dynamic metadata parameters
 */
@Singleton
@Immutable
@ThreadSafe
class TemplateBasedGraphQLQueryProvider implements GraphQLQueryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateBasedGraphQLQueryProvider.class);

    private static final String QUERY_TEMPLATE_RESOURCE_FILE = "query.graphql";
    private static final String MUTATION_TEMPLATE_RESOURCE_FILE = "mutation.graphql";
    private static final String QUERY_FORMAT = GraphQLFactory.getGraphQLSchemaResourceAsString(
            QUERY_TEMPLATE_RESOURCE_FILE
    );
    private static final String MUTATION_FORMAT = GraphQLFactory.getGraphQLSchemaResourceAsString(
            MUTATION_TEMPLATE_RESOURCE_FILE
    );

    private static final GraphQLQueryProvider INSTANCE = new TemplateBasedGraphQLQueryProvider();

    /**
     * Make sure class is not instantiable outside of this class.
     */
    private TemplateBasedGraphQLQueryProvider() {
        // empty
    }

    /**
     * Returns a fully initialized {@link TemplateBasedGraphQLQueryProvider} instance.
     *
     * @return a singleton
     */
    @NotNull
    static GraphQLQueryProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public String query(final String fileId, final List<String> metadataFields) {
        Objects.requireNonNull(fileId);
        Objects.requireNonNull(metadataFields);

        if (metadataFields.isEmpty()) {
            LOG.error(EMPTY_LIST.logFormat());
            throw new IllegalArgumentException(EMPTY_LIST.format());
        }

        return String.format(QUERY_FORMAT, fileId, String.join("\n        ", metadataFields));
    }

    @Override
    public String mutation(final String fileId, final MetaData metaData) {
        Objects.requireNonNull(fileId);
        Objects.requireNonNull(metaData);

        return String.format(MUTATION_FORMAT, fileId, metaData.getFileName(), metaData.getFileType());
    }
}
