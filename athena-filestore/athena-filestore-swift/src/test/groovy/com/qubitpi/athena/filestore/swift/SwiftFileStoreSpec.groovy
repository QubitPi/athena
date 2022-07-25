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
package com.qubitpi.athena.filestore.swift

import com.qubitpi.athena.file.File
import com.qubitpi.athena.file.identifier.FileIdGenerator
import com.qubitpi.athena.filestore.FileStore

import org.javaswift.joss.model.Account
import org.javaswift.joss.model.Container
import org.javaswift.joss.model.StoredObject

import spock.lang.Ignore
import spock.lang.Specification

class SwiftFileStoreSpec extends Specification {

    static final FILE_ID = "fileId123"

    FileIdGenerator fileIdGenerator
    Account account

    def setup() {
        fileIdGenerator = Mock(FileIdGenerator) { apply(_ as File) >> FILE_ID }
        account = Mock(Account) {
            getContainer(_ as String) >> Mock(Container) {
                getObject(FILE_ID) >> Mock(StoredObject)
            }
        }
    }

    @Ignore("The mocking is not working in an usual way. Need more investigations")
    def "File upload operation delegates to Swift API"() {
        given:
        FileStore fileStore = new SwiftFileStore(account, fileIdGenerator)

        when:
        fileStore.upload(Mock(File))

        then:
        1 * account.getContainer(_ as String)
    }
}
