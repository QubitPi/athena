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
package com.qubitpi.athena.web.graphql;

import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * {@link JsonDocumentParser} is used exclusively by {@link com.qubitpi.athena.web.endpoints.MetaServlet} and is
 * responsible for extracting metadata request info from POST request body, such as file ID and metadata fields that
 * client is asking for.
 * <p>
 * The request body must be JSON and have the following
 * <a href="https://graphql.org/learn/serving-over-http/#post-request">format</a> in order to be parsable by
 * {@link JsonDocumentParser}:
 * <pre>
 * {@code
 * {
 *     "query":"{\n  metaData(fileId:\"...\") {\n    fileName\nfileType  }\n}"
 * }
 * }
 * </pre>
 * Note that the selection {@code fileName\nfileType} can be any combination of file metadata object attributes
 */
public interface JsonDocumentParser {

    /**
     * Given the JSON document wrapping a GraphQL query string, this method extracts the query argument, which is a
     * file ID.
     * <p>
     * For example, if the document is
     * <pre>
     * {@code
     * {
     *     "query":"{\n  metaData(fileId:\"2\") {\n    fileName\nfileType  }\n}"
     * }
     * }
     * </pre>
     * then this method returns "2", which means the requested metadata is for a file whose file ID is 2.
     *
     * @param graphQLDocument  The provided JSON document
     *
     * @return an ordered list of requested metadata fields
     *
     * @throws NullPointerException if {@code graphQLDocument} is {@code null}
     */
    @NotNull
    String getFileId(@NotNull String graphQLDocument);

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
     *
     * @throws NullPointerException if {@code graphQLDocument} is {@code null}
     */
    @NotNull
    List<String> getFields(@NotNull String graphQLDocument);
}
