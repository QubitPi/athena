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

import static java.util.AbstractMap.SimpleImmutableEntry;

import io.github.qubitpi.athena.metadata.MetaData;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.NotThreadSafe;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An in-memory mutation {@link DataFetcher} associated with GraphQL file metadata field.
 */
@NotThreadSafe
public class TestMutationDataFetcher implements DataFetcher<MetaData> {

    private static final String FILE_ID = "fileId";

    @GuardedBy("this")
    private final Map<String, MetaData> metaDataByFileId;

    /**
     * Constructor.
     *
     * @param metaDataByFileId an initial in-memory store state holding file metadata mapped by file ID
     *
     * @throws NullPointerException if {@code metaDataByFileId} is {@code null}
     */
    public TestMutationDataFetcher(final Map<String, MetaData> metaDataByFileId) {
        this.metaDataByFileId = Objects.requireNonNull(metaDataByFileId);
    }

    @Override
    public MetaData get(final DataFetchingEnvironment dataFetchingEnvironment) {
        final String fileId = dataFetchingEnvironment.getArgument(FILE_ID);
        final MetaData newMetaData = MetaData.of(
                Stream.of(
                        new SimpleImmutableEntry<>(
                                MetaData.FILE_NAME,
                                dataFetchingEnvironment.getArgument(MetaData.FILE_NAME)
                        ),
                        new SimpleImmutableEntry<>(
                                MetaData.FILE_TYPE,
                                dataFetchingEnvironment.getArgument(MetaData.FILE_TYPE)
                        )
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        metaDataByFileId.put(fileId, newMetaData);

        return newMetaData;
    }
}
