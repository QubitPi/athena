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

import com.qubitpi.athena.metastore.MetaStore;
import com.qubitpi.athena.web.graphql.JsonDocumentParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

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

        final List<String> requestedMetadataFields = getJsonDocumentParser().getFields(graphQLDocument);
        if (requestedMetadataFields.isEmpty()) {
            LOG.error(INVALID_GRAPHQL_REQUEST.logFormat("No metadata field found", graphQLDocument));
            throw new IllegalArgumentException(
                    INVALID_GRAPHQL_REQUEST.format("no metadata field was found", graphQLDocument)
            );
        }

        return Response
                .status(Response.Status.OK)
                .entity(
                        getMetaStore().getMetaData(
                                getJsonDocumentParser().getFileId(graphQLDocument),
                                requestedMetadataFields
                        )
                )
                .build();
    }

    @NotNull
    private MetaStore getMetaStore() {
        return metaStore;
    }

    @NotNull
    private JsonDocumentParser getJsonDocumentParser() {
        return jsonDocumentParser;
    }
}
