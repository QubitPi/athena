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
package io.github.qubitpi.athena.application;

import io.github.qubitpi.athena.metadata.MetaData;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Map;
import java.util.Objects;

/**
 * An in-memory query {@link DataFetcher} associated with GraphQL file metadata field.
 */
@Immutable
@ThreadSafe
public class TestQueryDataFetcher implements DataFetcher<MetaData> {

    private static final String FILE_ID = "fileId";

    private final Map<String, MetaData> metaDataByFileId;

    /**
     * Constructor.
     *
     * @param metaDataByFileId an initial in-memory store state holding file metadata mapped by file ID
     *
     * @throws NullPointerException if {@code metaDataByFileId} is {@code null}
     */
    public TestQueryDataFetcher(final @NotNull Map<String, MetaData> metaDataByFileId) {
        this.metaDataByFileId = Objects.requireNonNull(metaDataByFileId);
    }

    @Override
    public MetaData get(final DataFetchingEnvironment dataFetchingEnvironment) {
        final String fileId = dataFetchingEnvironment.getArgument(FILE_ID);
        return metaDataByFileId.get(fileId);
    }
}
