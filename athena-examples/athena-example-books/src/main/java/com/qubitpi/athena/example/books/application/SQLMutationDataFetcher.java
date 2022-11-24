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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import javax.sql.DataSource;

/**
 * {@link SQLMutationDataFetcher} saves file meta data into a SQL data storage via a {@link DataSource}.
 */
public class SQLMutationDataFetcher implements DataFetcher<MetaData> {

    private static final String FILE_ID = "fileId";
    private static final String META_DATA_PERSIST_QUERY_TEMPLATE =
            "INSERT INTO BOOK_META_DATA (file_id, file_name, file_type) VALUES (?, ?, ?)";

    private final DataSource dataSource;

    /**
     * Constructor.
     *
     * @param dataSource  a client object against a SQL database to save meta data into
     *
     * @throws NullPointerException if {@code dataSource} is {@code null}
     */
    @Inject
    public SQLMutationDataFetcher(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public MetaData get(final DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        final String fileId = dataFetchingEnvironment.getArgument(FILE_ID);
        final String fileName = dataFetchingEnvironment.getArgument(MetaData.FILE_NAME);
        final String fileType = dataFetchingEnvironment.getArgument(MetaData.FILE_TYPE);

        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(META_DATA_PERSIST_QUERY_TEMPLATE)
        ) {
            statement.setString(1, fileId);
            statement.setString(2, fileName);
            statement.setString(3, fileType);
            statement.executeUpdate();
        }

        return MetaData.of(
                Stream.of(
                        new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_NAME, fileName),
                        new AbstractMap.SimpleImmutableEntry<>(MetaData.FILE_TYPE, fileType)
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    @NotNull
    private DataSource getDataSource() {
        return dataSource;
    }
}
