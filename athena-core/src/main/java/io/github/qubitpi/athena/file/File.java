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
package io.github.qubitpi.athena.file;

import io.github.qubitpi.athena.metadata.MetaData;

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.io.InputStream;
import java.util.Objects;

/**
 * The athena representations of the file concept.
 * <p>
 * A {@link File} has two parts
 * <ol>
 *     <li> file content wrapped inside an {@link InputStream}, and
 *     <li> file {@link MetaData metadata}
 * </ol>
 *
 * Note that an instance of it is guaranteed not to {@link InputStream#close() close} the encapsulated file at any time.
 */
@Immutable
@ThreadSafe
public class File {

    private final MetaData metaData;
    private final InputStream fileContent;

    /**
     * Constructor.
     *
     * @param metaData  File metadata
     * @param fileContent  File content wrapped inside an {@link InputStream}, which cannot be
     * {@link InputStream#close() closed} when passed in
     *
     * @throws NullPointerException if {@code metaData} or {@code fileContent} is {@code null}
     */
    public File(final @NotNull MetaData metaData, final @NotNull InputStream fileContent) {
        this.metaData = Objects.requireNonNull(metaData);
        this.fileContent = Objects.requireNonNull(fileContent);
    }

    /**
     * Returns an immutable representation of all metadata associated with this {@code File}.
     *
     * @return a read-only object
     */
    @NotNull
    public MetaData getMetaData() {
        return metaData;
    }

    /**
     * Returns the content of the file wrapped in an {@link InputStream}.
     *
     * @return an unclosed {@link InputStream}
     */
    @NotNull
    public InputStream getFileContent() {
        return fileContent;
    }
}
