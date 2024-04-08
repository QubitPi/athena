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
package io.github.qubitpi.athena.filestore.swift

import io.github.qubitpi.athena.file.File
import io.github.qubitpi.athena.file.identifier.FileIdGenerator

import org.javaswift.joss.model.Account
import org.javaswift.joss.model.Container
import org.javaswift.joss.model.StoredObject

import spock.lang.Specification

class SwiftFileStoreSpec extends Specification {

    static final FILE_ID = "fileId123"

    def "File upload operation delegates to Swift API"() {
        when:
        new SwiftFileStore(
                Mock(Account) {
                    1 * getContainer(SwiftFileStore.DEFAULT_CONTAINER) >> Mock(Container) {
                        1 * getObject(FILE_ID) >> Mock(StoredObject) {
                            1 * uploadObject(_ as InputStream)
                        }
                    }
                },
                Mock(FileIdGenerator) { apply(_ as File) >> FILE_ID }
        ).upload(Mock(File) {getFileContent() >> Mock(InputStream)})

        then:
        noExceptionThrown()
    }
}
