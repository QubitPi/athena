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
package com.paiondata.athena.metadata;

import static com.paiondata.athena.config.ErrorMessageFormat.MISSING_MAP_KEY;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.ExecutionResult;
import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * {@link MetaData} is provides information about {@link com.paiondata.athena.file.File}, but not the content of the
 * file.
 * <p>
 * {@link MetaData} offers the ability to make uploaded metadata and metadata from GraphQL {@link ExecutionResult}
 * type-safe.
 */
@Immutable
@ThreadSafe
public class MetaData {

    /**
     * Attribute name for filename in metadata object.
     */
    public static final String FILE_NAME = "fileName";

    /**
     * Attribute name for {@link FileType} in metadata object.
     */
    public static final String FILE_TYPE = "fileType";

    private static final Logger LOG = LoggerFactory.getLogger(MetaData.class);

    private final String fileName;
    private final FileType fileType;

    /**
     * All-args constructor.
     *
     * @param fileName  A filename includes both name (base name of the file) and extension, which indicates the content
     * of the file (e.g. .png, .pdf, .mp4)
     * @param fileType  The file extension
     *
     * @throws NullPointerException if {@code fileName} or {@code  fileType} is {@code null}
     */
    private MetaData(final @NotNull String fileName, final @NotNull FileType fileType) {
        this.fileName = Objects.requireNonNull(fileName);
        this.fileType = Objects.requireNonNull(fileType);
    }

    /**
     * Given a form-data content disposition header from a file upload request, constructs an athena representation
     * of the uploaded file metadata.
     *
     * @param uploadedMetaData  The file metadata object from HTTP request
     *
     * @return a new instance
     *
     * @throws NullPointerException if {@code uploadedMetaData} is {@code null}
     */
    @NotNull
    public static MetaData of(final @NotNull FormDataContentDisposition uploadedMetaData) {
        Objects.requireNonNull(uploadedMetaData);
        return new MetaData(
                uploadedMetaData.getFileName(),
                FileType.valueOf(uploadedMetaData.getFileName().split("\\.")[1].toUpperCase(Locale.ENGLISH))
        );
    }

    /**
     * Given a GraphQL query result, constructs an athena representation of a file metadata contained in that result.
     *
     * @param executionResult  The native GraphQL result
     *
     * @return a new instance
     *
     * @throws NullPointerException if {@code executionResult} is {@code null}
     */
    @NotNull
    public static MetaData of(final @NotNull ExecutionResult executionResult) {
        final Map<String, Object> map = Objects.requireNonNull(executionResult).toSpecification();

        @SuppressWarnings({"unchecked", "rawtypes"})
        final Map<String, Object> metadataMap = ((Map) ((Map) map.get("data")).get("metaData"));

        return of(metadataMap);
    }

    /**
     * Given a set of key-value pairs, constructs an athena representation of a file metadata whose states are based on
     * the specified key-value pairs.
     * <p>
     * The set of pairs must be a map with the following two keys, each of which has non-null values:
     * <ul>
     *    <li> {@link MetaData#FILE_NAME}
     *    <li> {@link MetaData#FILE_TYPE}
     * </ul>
     *
     * @param fieldMap  The provided key-value pairs
     *
     * @return a new instance
     *
     * @throws IllegalArgumentException if the key-value pairs are missing one of {@link #FILE_NAME} or
     * {@link #FILE_TYPE} keys
     */
    @NotNull
    public static MetaData of(final @NotNull Map<String, Object> fieldMap) {
        if (!fieldMap.containsKey(MetaData.FILE_NAME)) {
            LOG.error(MISSING_MAP_KEY.logFormat(MetaData.FILE_NAME, fieldMap));
            throw new IllegalArgumentException(MISSING_MAP_KEY.format());
        }

        if (!fieldMap.containsKey(MetaData.FILE_TYPE)) {
            LOG.error(MISSING_MAP_KEY.logFormat(MetaData.FILE_TYPE, fieldMap));
            throw new IllegalArgumentException(MISSING_MAP_KEY.format());
        }

        return new MetaData(fieldMap.get(FILE_NAME).toString(), FileType.valueOf(fieldMap.get(FILE_TYPE).toString()));
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    @NotNull
    public FileType getFileType() {
        return fileType;
    }
}
