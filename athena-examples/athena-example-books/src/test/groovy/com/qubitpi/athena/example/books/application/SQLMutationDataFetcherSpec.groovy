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
package com.qubitpi.athena.example.books.application

import com.qubitpi.athena.metadata.MetaData

import graphql.schema.DataFetchingEnvironment
import spock.lang.Specification
import spock.lang.Subject

import java.sql.Connection
import java.sql.PreparedStatement

import javax.sql.DataSource

class SQLMutationDataFetcherSpec extends Specification {

    static final String FILE_NAME = "pride-and-prejudice.pdf"
    static final String FILE_TYPE = "PDF"
    static final String FILE_ID = "df93if92eef"

    @Subject
    SQLMutationDataFetcher dataFetcher

    DataFetchingEnvironment dataFetchingEnvironment

    @SuppressWarnings('GroovyAccessibility')
    def setup() {
        dataFetchingEnvironment = Mock(DataFetchingEnvironment) {
            getArgument(SQLMutationDataFetcher.FILE_ID) >> FILE_ID
            getArgument(MetaData.FILE_NAME) >> FILE_NAME
            getArgument(MetaData.FILE_TYPE) >> FILE_TYPE
        }
    }

    @SuppressWarnings('GroovyAccessibility')
    def "Happy path meta data persistence causes DataSource to execute save query"() {
        setup: "instruct data source to fake a save setup"
        PreparedStatement preparedStatement = Mock(PreparedStatement)
        Connection connection = Mock(Connection) {
            prepareStatement(SQLMutationDataFetcher.META_DATA_PERSIST_QUERY_TEMPLATE) >> preparedStatement
        }
        dataFetcher = new SQLMutationDataFetcher(
                Mock(DataSource) {
                    getConnection() >> connection
                }
        )

        when: "meta data is being saved"
        dataFetcher.get(dataFetchingEnvironment)

        then: "a SQL save query is sent"
        1 * preparedStatement.executeUpdate()
    }
}
