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
package io.github.qubitpi.athena.filestore;

import io.github.qubitpi.athena.file.File;
import io.github.qubitpi.athena.metadata.MetaData;

import io.github.qubitpi.athena.metastore.MetaStore;

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.NotThreadSafe;

import java.io.InputStream;

/**
 * {@link FileStore} is an abstraction layer between Athena application and object storage; it can persist an
 * {@link File in-memory representation} of a file into object storage as well as retrieving it from that storage.
 */
@NotThreadSafe
public interface FileStore {

    /**
     * Persists a file into object storage database.
     * <p>
     * Note that calling this method does NOT automatically update the managed metadata of this file in
     * {@link MetaStore}. The metadata must be saved separately using
     * {@link MetaStore#saveMetaData(String, MetaData)}.
     * <p>
     * The implementation does NOT have to be thread-safe. {@link FileStore} assumes that it is the callers'
     * responsibilities to implement the thread safety themselves.
     *
     * @param file  An object representing the file to be persisted
     *
     * @return the file ID that can be used later to retrieve that file in Athena
     *
     * @throws NullPointerException if {@code file} is {@code null}
     */
    @NotNull
    String upload(@NotNull File file);

    /**
     * Retrieves a file identified by a specified file ID from object storage.
     *
     * @param fileId  The provided file ID, which is the same as the return value of {@link #upload(File)}
     *
     * @return a previously {@link #upload(File) uploaded file stream}
     *
     * @throws NullPointerException if {@code fileId} is {@code null}
     */
    @NotNull
    InputStream download(@NotNull String fileId);
}
