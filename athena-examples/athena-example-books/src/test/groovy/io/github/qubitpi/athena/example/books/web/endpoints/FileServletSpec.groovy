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
package io.github.qubitpi.athena.example.books.web.endpoints


import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import org.glassfish.jersey.media.multipart.MultiPart
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart
import io.github.qubitpi.athena.example.books.application.BookJerseyTestBinder
import groovy.json.JsonSlurper
import io.github.qubitpi.athena.web.endpoints.FileServlet
import jakarta.ws.rs.client.Entity

import java.nio.charset.StandardCharsets

class FileServletSpec extends AbstractServletSpec {

    @Override
    def childSetup() {
        jerseyTestBinder = new BookJerseyTestBinder(true, FileServlet.class, MultiPartFeature.class)
    }

    def "File can be uploaded and then download"() {
        when: "we upload a file"
        FileDataBodyPart filePart = new FileDataBodyPart("file", new File("src/test/resources/pride-and-prejudice-by-jane-austen.txt"))
        filePart.setContentDisposition(FormDataContentDisposition.name("file").fileName("pride-and-prejudice-by-jane-austen.txt").build())

        MultiPart multipartEntity = new FormDataMultiPart().bodyPart(filePart)

        String actual = jerseyTestBinder.makeRequest("/file/upload")
                .post(Entity.entity(multipartEntity, multipartEntity.getMediaType()))
                .readEntity(String.class)

        and: "download that file"
        String fileId = new JsonSlurper().parseText(actual).fileId
        actual = jerseyTestBinder.makeRequest("/file/download", [fileId: fileId]).get().readEntity(String.class)

        then: "we get the uploaded file back"
        getClass()
                .getClassLoader()
                .getResourceAsStream("pride-and-prejudice-by-jane-austen.txt")
                .getText(StandardCharsets.UTF_8.name()).contains(actual)
    }
}
