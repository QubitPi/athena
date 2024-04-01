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
package com.paiondata.athena.web.graphql;

import static com.paiondata.athena.config.ErrorMessageFormat.INVALID_GRAPHQL_REQUEST;
import static com.paiondata.athena.config.ErrorMessageFormat.JSON_DESERIALIZATION_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paiondata.athena.metadata.MetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link JacksonParser} parses GraphQL query using Jackson internally.
 */
public class JacksonParser implements JsonDocumentParser {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonParser.class);

    private static final String QUERY = "query";
    private static final String DOUBLE_QUOTE = "\"";

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final JsonDocumentParser INSTANCE = new JacksonParser();

    public static JsonDocumentParser getInstance() {
        return INSTANCE;
    }

    @Override
    public String getFileId(final String graphQLDocument) {
        final String query = getQuery(graphQLDocument);
        return query.substring(query.indexOf(DOUBLE_QUOTE) + 1, query.lastIndexOf(DOUBLE_QUOTE));
    }

    @Override
    public List<String> getFields(final String graphQLDocument) {
        final String query = getQuery(graphQLDocument);
        if (query.contains(MetaData.FILE_NAME) && query.contains(MetaData.FILE_TYPE)) {
            return query.indexOf(MetaData.FILE_NAME) < query.indexOf(MetaData.FILE_TYPE)
                    ? Arrays.asList(MetaData.FILE_NAME, MetaData.FILE_TYPE)
                    : Arrays.asList(MetaData.FILE_TYPE, MetaData.FILE_NAME);
        }

        if (query.contains(MetaData.FILE_NAME)) {
            return Collections.singletonList(MetaData.FILE_NAME);
        }

        if (query.contains(MetaData.FILE_TYPE)) {
            return Collections.singletonList(MetaData.FILE_TYPE);
        }

        return Collections.emptyList();
    }

    /**
     * Given the JSON document wrapping a GraphQL query string, this method extracts the query field.
     * <p>
     * For example, if the document is
     * <pre>
     * {@code
     * {
     *     "query":"{\n  metaData(fileId:\"2\") {\n    fileName\nfileType  }\n}",
     * }
     * }
     * </pre>
     * Then a string of "{@code {\n  metaData(fileId:\"2\") {\n    fileName\nfileType  }\n}}" will be returned.
     *
     * @param graphQLDocument  The provided JSON document which cannot be {@code null}, otherwise the behavior of this
     * method is undefined
     *
     * @return a native GraphQL query string
     *
     * @throws IllegalArgumentException if {@code graphQLDocument} is not a valid JSON or the {@code graphQLDocument}
     * doesn't contain "query" field.
     */
    @NotNull
    private static String getQuery(final @NotNull String graphQLDocument) {
        final JsonNode jsonDocument;
        try {
            jsonDocument = JSON_MAPPER.readTree(graphQLDocument);
        } catch (final JsonProcessingException exception) {
            LOG.error(JSON_DESERIALIZATION_ERROR.logFormat(graphQLDocument));
            throw new IllegalArgumentException(JSON_DESERIALIZATION_ERROR.format(graphQLDocument), exception);
        }

        if (!jsonDocument.has(QUERY)) {
            LOG.error(INVALID_GRAPHQL_REQUEST.logFormat("No 'query' field", graphQLDocument));
            throw new IllegalArgumentException(
                    INVALID_GRAPHQL_REQUEST.format("payload is missing 'query' field", graphQLDocument)
            );
        }

        return jsonDocument.get(QUERY).asText();
    }
}
