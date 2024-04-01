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
package com.paiondata.athena.web.graphql

import com.paiondata.athena.metadata.MetaData

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

abstract class JsonDocumentParserSpec extends Specification {

    static final String FILE_ID = "343gfdfq23"

    @Subject
    JsonDocumentParser jsonDocumentParser

    abstract JsonDocumentParser getInstance();

    def setup() {
        jsonDocumentParser = getInstance()
    }

    def "Parser can parse file ID property"() {
        expect:
        jsonDocumentParser.getFileId(jsonDocumentTemplate("fileName")) == FILE_ID
    }

    @Unroll
    def "Requested metadata of #requested got parsed into a list of #expected "() {
        expect:
        jsonDocumentParser.getFields(jsonDocumentTemplate(requested)) == expected

        where:
        requested           | expected
        ""                  | []
        "fileName"          | [MetaData.FILE_NAME]
        "fileType"          | [MetaData.FILE_TYPE]
        "fileName fileType" | [MetaData.FILE_NAME, MetaData.FILE_TYPE]
        "fileType fileName" | [MetaData.FILE_TYPE, MetaData.FILE_NAME]
    }

    static def jsonDocumentTemplate(String requested) {
        """
            {
                "query":"{\\n  metaData(fileId:\\"$FILE_ID\\") {\\n    ${requested}  }\\n}",
                "variables":null
            }
        """
    }
}
