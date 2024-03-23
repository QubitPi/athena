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
package com.qubitpi.athena.filestore.hdfs.hadoop;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import io.reactivex.Observable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A HDFS client that fetches files from HDFS and store it locally.
 * <p>
 * Here is an example of how to use {@code HdfsService}
 * <pre>
 *     {@code
 *     UserGroupInformation ugi = UserGroupInformation.createRemoteUser("root");
 *
 *     ugi.doAs(new PrivilegedExceptionAction<Void>() {
 *
 *         Void run() throws Exception {
 *             Configuration conf = new Configuration();
 *             conf.set("fs.defaultFS", "hdfs://localhost:8020/");
 *             conf.set("hadoop.job.ugi", "root");
 *
 *             hdfsFileSystem = FileSystem.get(conf);
 *
 *             HdfsService hdfsService = new HdfsService(hdfsFileSystem);
 *
 *             hdfsService.copyToLocal(
 *                 "hdfs://nameNode:8020/user/root/hadoop-data.txt",
 *                 "/home/localUser/hadoop-data.txt"
 *             );
 *
 *             return null;
 *         }
 *     });
 *     }
 * </pre>
 */
public class HdfsService {

    /**
     * A HDFS file system that HdfsService talks to to fetch the data files.
     */
    private final FileSystem fileSystem;

    /**
     * Constructs a {@code HdfsService} with a specified HDFS file system.
     *
     * @param fileSystem  A HDFS file system that HdfsService talks to to fetch the data files.
     */
    public HdfsService(final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Load remote file from HDFS to local.
     *
     * @param remotePath  The path to the remote file being fetched
     * @param localPath  The local path for the retrieved file
     *
     * @return observable of retrieved file
     *
     * @throws IOException if file system access failures occur
     */
    public Observable<File> copyToLocal(final String remotePath, final String localPath) throws IOException {
        return Observable.just(fetchRemote(remotePath, localPath));
    }

    /**
     * Load local file from local to HDFS.
     *
     * @param localPath  The local path for the uploaded file
     * @param remotePath  The path to the remote file being uploaded
     *
     * @return observable of retrieved file
     *
     * @throws IOException if file system access failures occur
     */
    public Observable<Void> moveFromLocal(final String localPath, final String remotePath) throws IOException {
        fileSystem.moveFromLocalFile(new Path(localPath), new Path(remotePath));
        return Observable.empty();
    }

    /**
     * Load remote file from HDFS to an {@link ByteArrayOutputStream}.
     *
     * @param remotePath  The path to the remote file being fetched
     *
     * @return the remote file stream
     *
     * @throws IOException if file system access failures occur
     */
    public OutputStream cat(final String remotePath) throws IOException {
        final OutputStream out = new ByteArrayOutputStream();
        final InputStream in = fileSystem.open(new Path(remotePath));

        try {
            IOUtils.copyBytes(in, out, fileSystem.getConf(), false);
        } finally {
            IOUtils.closeStream(in);
        }

        return out;
    }

    /**
     * Uses the Hadoop filesystem to fetch the remote file and store it locally.
     *
     * @param remotePath  The path to the remote file being fetched
     * @param localPath  The local path for the retrieved file
     *
     * @return the file at the location fetched to
     *
     * @throws IOException if file system access failures occur
     */
    private File fetchRemote(final String remotePath, final String localPath) throws IOException {
        final Path remote = new Path(remotePath);
        final Path local = new Path(localPath);

        (new File(local.getParent().toString())).mkdir();
        fileSystem.copyToLocalFile(remote, local);
        return new File(local.toUri().getPath());
    }
}
