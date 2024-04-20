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
package io.github.qubitpi.athena.filestore.swift

import io.github.qubitpi.athena.file.File
import io.github.qubitpi.athena.file.identifier.FileIdGenerator
import io.github.qubitpi.athena.filestore.FileStore
import io.github.qubitpi.athena.metadata.FileType
import io.github.qubitpi.athena.metadata.MetaData

import org.javaswift.joss.client.factory.AccountFactory
import org.javaswift.joss.client.factory.AuthenticationMethod
import org.javaswift.joss.model.Account
import org.javaswift.joss.model.Container

import jakarta.validation.constraints.NotNull
import spock.lang.Specification
import spock.lang.Subject

import java.nio.charset.StandardCharsets

class SwiftFileStoreITSpec extends Specification {

    static final String FILE_ID = "fileId"
    static final String FILENAME = "pride-and-prejudice-by-jane-austen.txt"

    @Subject
    FileStore fileStore

    def setup() {
        Account account = buildAccount()

        final Container container = account.getContainer(SwiftFileStore.DEFAULT_CONTAINER)

        if (!container.exists()) {
            container.create()
            container.makePublic()
        }

        fileStore = new SwiftFileStore(account, buildFileIdGenerator())
    }

    @SuppressWarnings("GroovyAccessibility")
    def "File can be uploaded and downloaded in its intact form"() {
        when: "a file is uploaded"
        fileStore.upload(
                new File(
                        new MetaData(FILENAME, FileType.TXT),
                        getClass().getClassLoader().getResourceAsStream(FILENAME)
                )
        )

        then: "the file can be downloaded later"
        fileStore.download(FILE_ID).getText(StandardCharsets.UTF_8.name()) ==
                getClass().getClassLoader().getResourceAsStream(FILENAME).getText(StandardCharsets.UTF_8.name())
    }

    @NotNull
    Account buildAccount() {
        new AccountFactory()
                .setUsername("chris:chris1234")
                .setPassword("testing")
                .setAuthUrl("http://127.0.0.1:12345/auth/v1.0")
                .setAuthenticationMethod(AuthenticationMethod.BASIC)
                .setMock(true)
                .createAccount()
    }

    @NotNull
    FileIdGenerator buildFileIdGenerator() {
        new FileIdGenerator() {
            @Override
            String apply(final File file) {
                return FILE_ID
            }
        }
    }
}
