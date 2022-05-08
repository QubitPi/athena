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
package com.qubitpi.athena.example.books.graphql;

import static java.util.AbstractMap.SimpleImmutableEntry;

import com.qubitpi.athena.metadata.MetaData;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An in-memory mutation {@link DataFetcher} associated with file metadata field in GraphQL.
 */
@Immutable
@ThreadSafe
public class MutationBookDataFetcher implements DataFetcher<MetaData> {

    private final Map<String, MetaData> bookMetaData;

    /**
     * Constructor.
     *
     * @param bookMetaData a write-only in-memory store for books mapped by book/file ID.
     *
     * @throws NullPointerException if {@code bookMetaData} is {@code null}
     */
    public MutationBookDataFetcher(final Map<String, MetaData> bookMetaData) {
        this.bookMetaData = Objects.requireNonNull(bookMetaData);
    }

    @Override
    public MetaData get(final DataFetchingEnvironment dataFetchingEnvironment) {
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
        bookMetaData.put(dataFetchingEnvironment.getArgument("fileId"), newMetaData);
        return newMetaData;
    }
}
