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
package io.github.qubitpi.athena.web.endpoints;

import io.github.qubitpi.athena.metastore.MetaStore;
import io.github.qubitpi.athena.web.graphql.JsonDocumentParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.qubitpi.athena.config.ErrorMessageFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.Objects;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Endpoint for POSTing and GETing file metadata.
 * <p>
 * This is the resource that serves GraphQL over HTTP. See
 * <a href="https://graphql.org/learn/serving-over-http/">GraphQL documentation</a> for specifications on serving
 * GraphQL over HTTP.
 */
@Singleton
@Immutable
@ThreadSafe
@Path("/metadata/graphql")
@Produces(MediaType.APPLICATION_JSON)
public class MetaServlet {

    private static final Logger LOG = LoggerFactory.getLogger(MetaServlet.class);

    private final MetaStore metaStore;
    private final JsonDocumentParser jsonDocumentParser;

    /**
     * DI constructor.
     *
     * @param metaStore  A delegating layer that handles all REST operations.
     * @param jsonDocumentParser  An object for extracting metadata request info from POST request body, such as file
     * ID and metadata fields that client is asking for.
     *
     * @throws NullPointerException if {@code metaStore} is {@code null}
     */
    @Inject
    public MetaServlet(final @NotNull MetaStore metaStore, final @NotNull JsonDocumentParser jsonDocumentParser) {
        this.metaStore = Objects.requireNonNull(metaStore);
        this.jsonDocumentParser = Objects.requireNonNull(jsonDocumentParser, "jsonDocumentParser");
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
    public Response get(final @NotNull @QueryParam("query") String query) {
        return Response
                .status(Response.Status.OK)
                .entity(metaStore.executeNative(Objects.requireNonNull(query)))
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
     *     "query": "..."
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

        final List<String> requestedMetadataFields = jsonDocumentParser.getFields(graphQLDocument);
        if (requestedMetadataFields.isEmpty()) {
            LOG.error(ErrorMessageFormat.INVALID_GRAPHQL_REQUEST.logFormat("No metadata field found", graphQLDocument));
            throw new IllegalArgumentException(
                    ErrorMessageFormat.INVALID_GRAPHQL_REQUEST.format("no metadata field was found", graphQLDocument)
            );
        }

        return Response
                .status(Response.Status.OK)
                .entity(
                        metaStore.getMetaData(
                                jsonDocumentParser.getFileId(graphQLDocument),
                                requestedMetadataFields
                        )
                )
                .build();
    }
}
