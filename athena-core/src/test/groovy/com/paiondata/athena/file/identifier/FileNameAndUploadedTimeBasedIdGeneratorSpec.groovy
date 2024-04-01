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
package com.paiondata.athena.file.identifier

import com.paiondata.athena.file.File
import com.paiondata.athena.metadata.MetaData

import spock.lang.Specification

class FileNameAndUploadedTimeBasedIdGeneratorSpec extends Specification {

    def "File ID has length of 24 chars"() {
        given: "a mocked file with its meta data object"
        MetaData metaData = Mock(MetaData)
        File file = Mock(File)

        and: "the meta data reflects a preconfigured file name"
        metaData.getFileName() >> "Pride and Prejudice.pdf"
        file.getMetaData() >> metaData

        expect: "the id generator computes fixed-length ID"
        FileNameAndUploadedTimeBasedIdGenerator.algorithm("MD5").apply(file).length() == 24
    }
}
