/*
 * Copyright 2024 Jiaqi Liu
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
package io.github.qubitpi.athena.metastore.graphql.query;

import io.github.qubitpi.athena.metadata.MetaData;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * {@link GraphQLQueryProvider} is an abstraction layer that constructs GraphQL queries on file metadata.
 * <p>
 * Implementation must be package-scoped and let {@link GraphQLQueryProviderFactory} as the broker for their instance
 * provision
 */
public interface GraphQLQueryProvider {

    /**
     * Constructs and returns a GraphQL file metadata Query document given the specified file ID associated with the
     * requested metadata and the metadata fields.
     * <p>
     * For example, if file ID is "df32wsv3rr3ed", and the requested metadata fields are ["fileType", "fileName"], this
     * method will return a string of
     * <pre>
     * {@code
     * query {
     *     metaData(fileId: "df32wsv3rr3ed") {
     *         fileType
     *         fileName
     *     }
     * }
     * }
     * </pre>
     *
     * @param fileId  The file ID associated with the requested metadata info
     * @param metadataFields  The requested metadata fields
     *
     * @return a GraphQL query
     *
     * @throws NullPointerException if {@code fileId} or {@code metadataFields} is {@code null}
     * @throws IllegalArgumentException if {@code metadataFields} list is empty
     */
    @NotNull
    String query(@NotNull String fileId, @NotNull List<String> metadataFields);

    /**
     * Constructs and returns a GraphQL file metadata Mutation document given a {@link MetaData} object to be saved and
     * a specified file ID associated with the metadata.
     * <p>
     * For example, if file ID is "df32wsv3rr3ed", and the metadata to be saved is
     * <code>{"fileType": "PDF", "fileName": "Pride and Prejudice"}</code>, then this method will return a string of
     * <pre>
     * {@code
     * mutation createMetaData {
     *     createMetaData(fileId: "df32wsv3rr3ed", fileName: "Pride and Prejudice", fileType: "PDF") {
     *         fileName,
     *         fileType
     *     }
     * }
     * }
     * </pre>
     *
     * @param fileId  The file ID associated with the requested metadata info
     * @param metaData  An object that contains all information about the new metadata to be saved
     *
     * @return a GraphQL query that creates a new {@link MetaData} object in database
     *
     * @throws NullPointerException if {@code fileId} or {@code metaData} is {@code null}
     */
    @NotNull
    String mutation(@NotNull String fileId, @NotNull MetaData metaData);
}
