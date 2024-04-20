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
package io.github.qubitpi.athena.web.endpoints


import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import org.glassfish.jersey.media.multipart.MultiPart
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart

import io.github.qubitpi.athena.application.ApplicationState
import io.github.qubitpi.athena.application.JerseyTestBinder
import io.github.qubitpi.athena.metadata.FileType
import io.github.qubitpi.athena.metadata.MetaData
import jakarta.ws.rs.client.Entity
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.function.BiFunction

class FileServletSpec extends Specification {

    static final String FILE_ID = "2"
    static final FileType FILE_TYPE = FileType.TXT
    static final String FILE_NAME = "pride-and-prejudice.txt"

    @SuppressWarnings("GroovyAccessibility")
    static final MetaData META_DATA = new MetaData(FILE_NAME, FILE_TYPE)

    JerseyTestBinder jerseyTestBinder

    def setup() {
        ApplicationState applicationState = new ApplicationState()
        applicationState.fileIdGenerator = { FILE_ID }
        applicationState.metadataByFileId = [(FILE_ID): META_DATA]
        applicationState.queryFormatter = new BiFunction<String, List<String>, String>() {
            @Override
            String apply(final String s, final List<String> strings) {
                return """
                    query {
                        metaData(fileId: "$FILE_ID") {
                            $META_DATA.FILE_NAME
                            $META_DATA.FILE_TYPE
                        }
                    }
                """
            }
        }
        applicationState.mutationFormatter = new BiFunction<String, MetaData, String>() {
            @Override
            String apply(final String s, final MetaData metaData) {
                return """
                    mutation createMetaData {
                        createMetaData(fileId: "$FILE_ID", fileName: "$FILE_NAME", fileType: "$FILE_TYPE") {
                            $META_DATA.FILE_NAME
                            $META_DATA.FILE_TYPE
                        }
                    }
                """
            }
        }

        // Create the tet web container to test the resources
        jerseyTestBinder = new JerseyTestBinder(true, applicationState, FileServlet.class, MultiPartFeature.class)
    }

    def cleanup() {
        // Release the test web container
        jerseyTestBinder.tearDown()
    }

    def "File can be uploaded and then download"() {
        when: "we upload a file"
        FileDataBodyPart filePart = new FileDataBodyPart("file", new File("src/test/resources/pride-and-prejudice-by-jane-austen.txt"))
        filePart.setContentDisposition(FormDataContentDisposition.name("file").fileName("pride-and-prejudice-by-jane-austen.txt").build())

        MultiPart multipartEntity = new FormDataMultiPart().bodyPart(filePart)

        String actual = jerseyTestBinder.makeRequest("/file/upload")
                .post(Entity.entity(multipartEntity, multipartEntity.getMediaType()))
                .readEntity(String.class)

        then: "we get a uploaded file ID back"
        actual == """{"fileId":"$FILE_ID"}"""

        when: "we use that file ID to download that file"
        actual = jerseyTestBinder.makeRequest("/file/download", [fileId: "2"]).get().readEntity(String.class)

        then: "we get the file back"
        getClass()
                .getClassLoader()
                .getResourceAsStream("pride-and-prejudice-by-jane-austen.txt")
                .getText(StandardCharsets.UTF_8.name()).contains(actual)
    }
}
