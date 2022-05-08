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

import com.qubitpi.athena.file.File;
import com.qubitpi.athena.metadata.MetaData;

import graphql.ExecutionResult;
import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.NotThreadSafe;

import java.util.List;

/**
 * {@link MetaStore} is a GraphQL abstraction layer between Athena application and a custom file metadata database; it
 * persists metadata info into the database as well as reading it from that database.
 */
@NotThreadSafe
public interface MetaStore {

    /**
     * Retrieves a file metadata using a native GraphQL query.
     *
     * @param query  The query for fetching file metadata
     *
     * @return a metadata object
     *
     * @throws NullPointerException if {@code query} is {@code null}
     */
    @NotNull
    ExecutionResult executeNative(@NotNull String query);

    /**
     * Retrieves a file metadata identified by a specified file ID.
     *
     * @param fileId  The provided file ID
     * @param metadataFields  The dynamic set up fields that are returned to the service client
     *
     * @return a metadata object containing all requested metadata fields
     *
     * @throws NullPointerException if {@code fileId} or {@code metadataFields} is {@code null}
     * @throws IllegalArgumentException if {@code metadataFields} is an empty list
     */
    @NotNull
    ExecutionResult getMetaData(@NotNull String fileId, @NotNull List<String> metadataFields);

    /**
     * Persists a file metadata into database.
     * <p>
     * The implementation does NOT have to be thread-safe. {@link MetaStore} assumes that it is the callers'
     * responsibilities to implement the thread safety themselves.
     *
     * @param fileId  The ID of the file that has already been uploaded to object storage. The value of the ID must be
     * the return value of {@link com.qubitpi.athena.filestore.FileStore#upload(File)} so that the corresponding
     * metadata can be retrieved later by calling {@link #getMetaData(String, List)}
     * @param metaData  The metadata object that is going to be saved into database
     *
     * @throws NullPointerException if {@code fileId} or {@code metaData} is {@code null}
     */
    void saveMetaData(@NotNull String fileId, @NotNull MetaData metaData);
}
