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
package com.qubitpi.athena.example.books.application;

import com.qubitpi.athena.metadata.MetaData;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.constraints.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.sql.DataSource;

public class SQLMutationDataFetcher implements DataFetcher<MetaData> {

    private static final String FILE_ID = "fileId";
    private static final String META_DATA_PERSIST_QUERY_TEMPLATE =
            "INSERT INTO META_DATA (file_id, file_name, file_type) VALUES ('%s', '%s', '%s')";


    private final DataSource dataSource;

    @Inject
    public SQLMutationDataFetcher(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public MetaData get(final DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        final String fileId = dataFetchingEnvironment.getArgument(FILE_ID);

        dataSource.getConnection().createStatement().execute(
                String.format(
                        META_DATA_PERSIST_QUERY_TEMPLATE,
                        fileId,
                        dataFetchingEnvironment.getArgument(MetaData.FILE_NAME),
                        dataFetchingEnvironment.getArgument(MetaData.FILE_TYPE)
                )
        );

        return MetaData.of(
                Stream.of(
                        new AbstractMap.SimpleImmutableEntry<>(
                                MetaData.FILE_NAME,
                                dataFetchingEnvironment.getArgument(MetaData.FILE_NAME)
                        ),
                        new AbstractMap.SimpleImmutableEntry<>(
                                MetaData.FILE_TYPE,
                                dataFetchingEnvironment.getArgument(MetaData.FILE_TYPE)
                        )
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    @NotNull
    private DataSource getDataSource() {
        return dataSource;
    }
}
