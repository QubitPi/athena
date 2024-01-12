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
package org.qubitpi.athena.application;

import org.qubitpi.athena.file.File;
import org.qubitpi.athena.file.identifier.FileIdGenerator;
import org.qubitpi.athena.filestore.FileStore;
import org.qubitpi.athena.metastore.MetaStore;
import org.qubitpi.athena.metastore.TestMetaStore;
import org.qubitpi.athena.metadata.MetaData;

import net.jcip.annotations.NotThreadSafe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Holds the application state when testing so that it can be more easily mocked in {@link JerseyTestBinder}.
 */
@NotThreadSafe
public class ApplicationState {

    /**
     * Initial {@link MetaStore} test data during each test def (not per-spec).
     * <p>
     * This map is where {@link graphql.schema.DataFetcher} is going to read test data from and write test data to and
     * will be initialized in setup() method
     *
     * @see TestQueryDataFetcher
     * @see TestMutationDataFetcher
     */
    public Map<String, MetaData> metadataByFileId = new HashMap<>();

    /**
     * Used for stubbing {@link MetaStore#getMetaData(String, List)} method's internal
     * behavior by manually constructing an overriding GraphQL query regardless of method arguments.
     * <p>
     * Combining with {@link #metadataByFileId}, we can mock
     * {@link MetaStore#getMetaData(String, List)} to return any pre-configured file
     * metadata during test
     *
     * @see TestMetaStore
     */
    public BiFunction<String, List<String>, String> queryFormatter = (fileId, fields) -> "";

    /**
     * Used for stubbing {@link MetaStore#saveMetaData(String, MetaData)} method's internal
     * behavior by manually constructing an overriding GraphQL query regardless of method arguments.
     * <p>
     * Combining with {@link #metadataByFileId}, we can mock any behavior of
     * {@link MetaStore#saveMetaData(String, MetaData) saving file metadata}
     *
     * @see TestMetaStore
     */
    public BiFunction<String, MetaData, String> mutationFormatter = (fileId, metadata) -> "";

    /**
     * Initial {@link FileStore} test data during each test def (not per-spec)
     * <p>
     * This map is where {@link FileStore} is going to read test data from and write test
     * data to and will be initialized in setup() method.
     */
    public Map<String, String> fileByFileId = new HashMap<>();

    /**
     * Used for stubbing {@link FileStore#upload(File)} method's internal behavior by
     * manually constructing an overriding file ID regardless of method arguments.
     * <p>
     * Combining with {@link #fileByFileId}, we can mock any behavior of
     * {@link FileStore#upload(File)} and
     * .{@link FileStore#download(String)}
     */
    public FileIdGenerator fileIdGenerator = (it) -> "";

    /**
     * Clears all.
     * <p>
     * This method will be called in cleanup() method.
     */
    public void resetAllStates() {
        metadataByFileId = new HashMap<>();
        queryFormatter = (fileId, fields) -> "";
        mutationFormatter = (fileId, metadata) -> "";
        fileByFileId = new HashMap<>();
        fileIdGenerator = null;
    }
}
