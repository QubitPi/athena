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
package io.github.qubitpi.athena.web.endpoints;

import io.github.qubitpi.athena.file.File;
import io.github.qubitpi.athena.filestore.FileStore;
import io.github.qubitpi.athena.metadata.MetaData;
import io.github.qubitpi.athena.metastore.MetaStore;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Endpoint for POSTing files.
 */
@Singleton
@Immutable
@ThreadSafe
@Path("/file")
public class FileServlet {

    private static final String FILE_ID = "fileId";

    private final FileStore fileStore;
    private final MetaStore metaStore;

    /**
     * DI constructor.
     *
     * @param fileStore  A client connecting file data and persistence storage
     * @param metaStore  A client connecting file metadata and persistence storage
     *
     * @throws NullPointerException if any argument is {@code null}
     */
    @Inject
    public FileServlet(final @NotNull FileStore fileStore, final @NotNull MetaStore metaStore) {
        this.fileStore = Objects.requireNonNull(fileStore);
        this.metaStore = Objects.requireNonNull(metaStore);
    }

    /**
     * Persists a file to object storage.
     *
     * @param fileContent  The file content
     * @param fileMetaData  The file metadata
     *
     * @return a Json object indicating whether the request is successful or not
     *
     * @throws NullPointerException if any argument is {@code null}
     */
    @POST
    @NotNull
    @Path("/upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            final @NotNull @FormDataParam("file") InputStream fileContent,
            final @NotNull @FormDataParam("file") FormDataContentDisposition fileMetaData
    ) {
        final File file = new File(MetaData.of(fileMetaData), fileContent);
        final String fileId = fileStore.upload(file);
        metaStore.saveMetaData(fileId, file.getMetaData());
        return Response
                .status(Response.Status.CREATED)
                .entity(Collections.singletonMap(FILE_ID, fileId))
                .build();
    }

    /**
     * Retrieves a file from object storage.
     *
     * @param fileId  The {@link #uploadFile(InputStream, FormDataContentDisposition) ID of the file} previously
     * uploaded.
     *
     * @return a file to be downloaded
     *
     * @throws NullPointerException if {@code fileId} is {@code null}
     */
    @GET
    @NotNull
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@QueryParam(FILE_ID) final String fileId) {
        return Response
                .ok(
                        fileStore.download(Objects.requireNonNull(fileId)),
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .header(
                        "content-disposition",
                        String.format(
                                "attachment; filename = %s",
                                ((Map<?, ?>) ((Map<?, ?>) metaStore
                                        .getMetaData(fileId, Collections.singletonList(MetaData.FILE_NAME))
                                        .toSpecification().get("data")).get("metaData"))
                                        .get(MetaData.FILE_NAME).toString()
                        )
                )
                .build();
    }
}
