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
package com.qubitpi.athena.filestore.hdfs;

import static com.qubitpi.athena.config.ErrorMessageFormat.DISK_IO_FETCH_ERROR;
import static com.qubitpi.athena.config.ErrorMessageFormat.DISK_IO_WRITE_ERROR;
import static com.qubitpi.athena.config.ErrorMessageFormat.HDFS_FETCH_IO_ERROR;
import static com.qubitpi.athena.config.ErrorMessageFormat.HDFS_WRITE_IO_ERROR;

import com.qubitpi.athena.config.SystemConfig;
import com.qubitpi.athena.config.SystemConfigFactory;
import com.qubitpi.athena.file.File;
import com.qubitpi.athena.file.identifier.FileIdGenerator;
import com.qubitpi.athena.file.writer.LocalFileWriter;
import com.qubitpi.athena.filestore.FileStore;
import com.qubitpi.athena.filestore.hdfs.hadoop.HdfsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.inject.Inject;

/**
 * {@link HdfsFileStore} is in complete agnostic of HDFS namespace federations. A namespace must have already been
 * defined before any HDFS operations are performed through {@link HdfsFileStore}.
 */
public class HdfsFileStore implements FileStore {

    private static final Logger LOG = LoggerFactory.getLogger(HdfsFileStore.class);

    private final String localDir;
    private final String hdfsDir;
    private final HdfsService hdfsService;
    private final FileIdGenerator fileIdGenerator;

    @Inject
    public HdfsFileStore(
            final String localDir,
            final String hdfsDir,
            final HdfsService hdfsService,
            final FileIdGenerator fileIdGenerator
    ) {
        this.localDir = localDir;
        this.hdfsDir = hdfsDir;
        this.hdfsService = hdfsService;
        this.fileIdGenerator = fileIdGenerator;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String upload(final File file) {
        Objects.requireNonNull(file);
        final String fileId = getFileIdGenerator().apply(file);

        String filePath = LocalFileWriter.targetPath(getLocalPath(fileId)).write(file).blockingFirst();

        try {
            getHdfsService()
                    .moveFromLocal(filePath, getHdfsPath(fileId))
                    .doOnComplete(() -> Files.deleteIfExists(Paths.get(getLocalPath(fileId))));
        } catch (IOException exception) {
            LOG.error(HDFS_WRITE_IO_ERROR.logFormat(exception));
            throw new IllegalStateException(HDFS_WRITE_IO_ERROR.format(), exception);
        }

        return fileId;
    }

    private String getHdfsPath(String fileId) {
        return Paths.get(getHdfsDir(), fileId).toString();
    }

    private String getLocalPath(String fileId) {
        return Paths.get(getLocalDir(), fileId).toString();
    }

    @Override
    @SuppressWarnings("ThrowFromFinallyBlock")
    public InputStream download(final String fileId) {
        Observable<java.io.File> fileObservable;
        try {
            fileObservable = getHdfsService().copyToLocal(getHdfsPath(fileId), getLocalPath(fileId));
        } catch (IOException exception) {
            LOG.error(HDFS_FETCH_IO_ERROR.logFormat(exception));
            throw new IllegalStateException(HDFS_FETCH_IO_ERROR.format(), exception);
        }

        try {
            return Files.newInputStream(fileObservable.blockingFirst().toPath());
        } catch (IOException exception) {
            LOG.error(DISK_IO_FETCH_ERROR.logFormat(exception));
            throw new IllegalStateException(DISK_IO_FETCH_ERROR.format(), exception);
        } finally {
            try {
                Files.deleteIfExists(Paths.get(getLocalPath(fileId)));
            } catch (IOException exception) {
                LOG.error(DISK_IO_WRITE_ERROR.logFormat(exception));
                throw new IllegalStateException(DISK_IO_WRITE_ERROR.format(), exception);
            }
        }
    }

    public String getLocalDir() {
        return localDir;
    }

    public String getHdfsDir() {
        return hdfsDir;
    }

    public HdfsService getHdfsService() {
        return hdfsService;
    }

    public FileIdGenerator getFileIdGenerator() {
        return fileIdGenerator;
    }
}
