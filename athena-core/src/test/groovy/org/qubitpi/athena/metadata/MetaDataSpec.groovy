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
package org.qubitpi.athena.metadata

import org.qubitpi.athena.config.ErrorMessageFormat

import org.glassfish.jersey.media.multipart.FormDataContentDisposition

import graphql.ExecutionResult
import spock.lang.Specification
import spock.lang.Unroll

class MetaDataSpec extends Specification {

    static final String EXPECTED_FILE_NAME = "Pride and Prejudice.pdf"
    static final String EXPECTED_FILETYPE = "PDF"

    def "HTTP header info, in happy path, can be used to construct an equivalent Athena meta data object"() {
        given: "A mocked form data from HTTP request"
        FormDataContentDisposition uploadedMetaData = Mock(FormDataContentDisposition)

        and: "the form contains all required meta data fields"
        uploadedMetaData.getFileName() >>> [EXPECTED_FILE_NAME, EXPECTED_FILE_NAME]

        when: "the form data is used to construct meta data"
        MetaData actual = MetaData.of(uploadedMetaData)

        then: "the metadata contains the equivalent data from the form"
        actual.fileName == EXPECTED_FILE_NAME
        actual.fileType.toString() == EXPECTED_FILETYPE
    }

    def "Native GraphQL ExecutionResult, in happy path, can be converted to an equivalent Athena meta data object"() {
        given: "A mocked GraphQL ExecutionResult"
        ExecutionResult executionResult = Mock(ExecutionResult)

        and: "its JSON result contains all required meta data fields"
        executionResult.toSpecification() >> [
                data: [
                        metaData: [
                                (MetaData.FILE_NAME): EXPECTED_FILE_NAME,
                                (MetaData.FILE_TYPE): EXPECTED_FILETYPE
                        ]
                ]
        ]

        when: "the ExecutionResult is use to construct meta data"
        MetaData actual = MetaData.of(executionResult)

        then: "the metadata contains the equivalent data from the ExecutionResult"
        actual.fileName == EXPECTED_FILE_NAME
        actual.fileType.toString() == EXPECTED_FILETYPE
    }

    def "When a map contains all required metadata fields in its key, a meta data object can be constructed without error"() {
        when: "a map containing file name and type key value pairs is use to construct meta data"
        MetaData actual = MetaData.of((MetaData.FILE_NAME): EXPECTED_FILE_NAME, (MetaData.FILE_TYPE): EXPECTED_FILETYPE)

        then: "the metadata contains the equivalent data from the map"
        actual.fileName == EXPECTED_FILE_NAME
        actual.fileType.toString() == EXPECTED_FILETYPE
    }

    @Unroll
    def "When a map is used to construct meta data object and #requiredField is missing in the map, error is thrown"() {
        given: "a map that contains all required metadata fields in map key set"
        Map<String, Object> fieldMap = [(MetaData.FILE_NAME): "Pride and Prejudice.pdf", (MetaData.FILE_TYPE): "PDF"]

        when: "a required field is make not present in the map"
        fieldMap.remove(requiredField)

        and: "the map is used to construct meta data"
        MetaData.of(fieldMap)

        then: "an error is thrown"
        Exception exception = thrown(IllegalArgumentException)
        exception.message == ErrorMessageFormat.MISSING_MAP_KEY.format()

        where:
        requiredField      |_
        MetaData.FILE_NAME |_
        MetaData.FILE_TYPE |_
    }

    @SuppressWarnings(["GroovyAccessibility", 'GroovyResultOfObjectAllocationIgnored'])
    def "File name cannot be null while constructing meta data"() {
        when: "a null file name is passed to File constructor"
        new MetaData(null, FileType.PDF)

        then: "a runtime exception is thrown"
        thrown(NullPointerException)
    }

    @SuppressWarnings(["GroovyAccessibility", 'GroovyResultOfObjectAllocationIgnored'])
    def "File type cannot be null while constructing meta data"() {
        when: "a null file type is passed to File constructor"
        new MetaData("Pride and Prejudice.pdf", null)

        then: "a runtime exception is thrown"
        thrown(NullPointerException)
    }
}
