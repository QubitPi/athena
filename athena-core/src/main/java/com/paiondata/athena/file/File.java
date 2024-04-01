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
package com.paiondata.athena.file;

import com.paiondata.athena.metadata.MetaData;

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
 *     <li> file content, and
 *     <li> file {@link MetaData metadata}
 * </ol>
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
     * @param fileContent  File content
     *
     * @throws NullPointerException if {@code metaData} or {@code fileContent} is {@code null}
     */
    public File(final @NotNull MetaData metaData, final @NotNull InputStream fileContent) {
        this.metaData = Objects.requireNonNull(metaData);
        this.fileContent = Objects.requireNonNull(fileContent);
    }

    @NotNull
    public MetaData getMetaData() {
        return metaData;
    }

    @NotNull
    public InputStream getFileContent() {
        return fileContent;
    }
}
