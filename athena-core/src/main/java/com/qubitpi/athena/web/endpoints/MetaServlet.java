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
package com.qubitpi.athena.web.endpoints;

import static com.qubitpi.athena.config.ErrorMessageFormat.INVALID_GRAPHQL_REQUEST;
import static com.qubitpi.athena.config.ErrorMessageFormat.JSON_DESERIALIZATION_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubitpi.athena.metadata.MetaData;
import com.qubitpi.athena.metastore.MetaStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Endpoint for POSTing and GETing file metadata.
 * <p>
 * This is the resource that serves GraphQL over HTTP. See
 * <a href="https://graphql.org/learn/serving-over-http/">GraphQL documentation</a> for specifications on serving
 * GraphQL over HTTP.
 */
@Singleton
@Path("/metadata/graphql")
@Produces(MediaType.APPLICATION_JSON)
public class MetaServlet {

    private static final Logger LOG = LoggerFactory.getLogger(MetaServlet.class);

    private static final String QUERY = "query";
    private static final String DOUBLE_QUOTE = "\"";

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private final MetaStore metaStore;

    /**
     * DI constructor.
     *
     * @param metaStore  A delegating layer that handles all REST operations.
     *
     * @throws NullPointerException if {@code metaStore} is {@code null}
     */
    @Inject
    public MetaServlet(final @NotNull MetaStore metaStore) {
        this.metaStore = Objects.requireNonNull(metaStore);
    }

    /**
     * Query metadata via GraphQL GET.
     * <p>
     * Note that in the context of Athena, {@code variables} and {@code operationName} path params are not supported
     * here. Please check out
     * <a href="https://graphql.org/learn/serving-over-http/#get-request">GraphQL documentation</a> for more details.
     *
     * @param query  A native GraphQL query operation definition, such as "query={me{name}}"
     *
     * @return native GraphQL query result
     *
     * @throws NullPointerException if {@code query} is {@code null}
     */
    @GET
    @NotNull
    public Response get(final @NotNull @QueryParam(QUERY) String query) {
        return Response
                .status(Response.Status.OK)
                .entity(getMetaStore().executeNative(Objects.requireNonNull(query)))
                .build();
    }

    /**
     * Query metadata via GraphQL POST.
     * <p>
     * Note that in the context of Athena, {@code variables} and {@code operationName} fields are optional and will not
     * be processed. Please check out
     * <a href="https://graphql.org/learn/serving-over-http/#get-request">GraphQL documentation</a> for more details.
     *
     * @param graphQLDocument  A native GraphQL document as a JSON-encoded body of the following form:
     * <pre>
     * {@code
     * {
     *   "query": "..."
     * }
     * }
     * </pre>
     *
     * @return native GraphQL query result
     *
     * @throws NullPointerException if {@code graphQLDocument} is {@code null}
     * @throws IllegalArgumentException if no metadata fields are found in {@code graphQLDocument}
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(final @NotNull String graphQLDocument) {
        Objects.requireNonNull(graphQLDocument);

        final List<String> requestedMetadataFields = getFields(graphQLDocument);
        if (requestedMetadataFields.isEmpty()) {
            LOG.error(INVALID_GRAPHQL_REQUEST.logFormat(
                    String.format("No metadata field found in query '%s'", graphQLDocument))
            );
            throw new IllegalArgumentException(INVALID_GRAPHQL_REQUEST.format(graphQLDocument));
        }

        return Response
                .status(Response.Status.OK)
                .entity(getMetaStore().getMetaData(getFileId(graphQLDocument), requestedMetadataFields))
                .build();
    }

    /**
     * Given the JSON document wrapping a GraphQL query string, this method extracts the query argument, which is a
     * file ID.
     * <p>
     * For example, if the document is
     * <pre>
     * {@code
     * {
     *     "query":"{\n  metaData(fileId:\"2\") {\n    fileName\nfileType  }\n}",
     * }
     * }
     * </pre>
     * then this method returns "2", which means the requested metadata is for a file whose file ID is 2.
     *
     * @param graphQLDocument  The provided JSON document which cannot be {@code null}, otherwise the behavior of this
     * method is undefined
     *
     * @return an ordered list of requested metadata fields
     */
    @NotNull
    private String getFileId(final @NotNull String graphQLDocument) {
        final String query = getQuery(graphQLDocument);
        return query.substring(query.indexOf(DOUBLE_QUOTE) + 1, query.lastIndexOf(DOUBLE_QUOTE));
    }

    /**
     * Given the JSON document wrapping a GraphQL query string, this method extracts the query field and then the
     * requested metadata field(s) in an ordered list.
     * <p>
     * For example, if the document is
     * <pre>
     * {@code
     * {
     *     "query":"{\n  metaData(fileId:\"2\") {\n    fileName\nfileType  }\n}",
     * }
     * }
     * </pre>
     * then this method returns a list of ["fileName", "fileType"].
     * <p>
     * The order of the metadata fields also influences the element order in the returned list. For instance, if the
     * document above changes to
     * <pre>
     * {@code
     * {
     *     "query":"{\n  metaData(fileId:\"2\") {\n    fileType\nfileName  }\n}",
     * }
     * }
     * </pre>
     * then the returned list becomes ["fileType", "fileNAME"]
     * <p>
     * If no fields are found, this method returns an {@link Collections#emptyList() empty list}
     *
     * @param graphQLDocument  The provided JSON document which cannot be {@code null}, otherwise the behavior of this
     * method is undefined
     *
     * @return an ordered list of requested metadata fields
     */
    @NotNull
    private List<String> getFields(final @NotNull String graphQLDocument) {
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
     * @throws IllegalArgumentException if {@code graphQLDocument} is not a valid JSON
     */
    @NotNull
    private String getQuery(final @NotNull String graphQLDocument) {
        try {
            return JSON_MAPPER.readTree(graphQLDocument).get(QUERY).asText();
        } catch (final JsonProcessingException exception) {
            LOG.error(JSON_DESERIALIZATION_ERROR.logFormat(graphQLDocument));
            throw new IllegalArgumentException(JSON_DESERIALIZATION_ERROR.format(graphQLDocument), exception);
        }
    }

    @NotNull
    private MetaStore getMetaStore() {
        return metaStore;
    }
}
