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
package com.qubitpi.athena.filestore.hdfs.hadoop

import org.apache.commons.io.IOUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hdfs.MiniDFSCluster

import io.reactivex.Observable
import spock.lang.IgnoreIf
import spock.lang.Specification

/**
 * Unit tests that runs against an in-memory HDFS.
 * <p>
 * The tests are ignored on windows operating system, because windows does not allow in-memory HDFS to delete non-empty
 * directories in a normal way and, thus, throws the following external error:
 *
 * "java.io.IOException: Could not fully delete ..\aristotle\target\test\data\dfs\name-0-1"
 *
 * The tests are kept being ignored until there is a work-around
 *
 * The absolute path of this file must not contain white space otherwise test HDFS will screw up its test data dir
 * setup
 */
@IgnoreIf({System.getProperty("os.name").toLowerCase().contains("windows")})
class HdfsServiceSpec extends Specification {

    /**
     * Name of the file that is going to be loaded into HDFS.
     */
    private static final String FILENAME = "hadoop-data.txt"

    /**
     * The local parent directory of the fetched file.
     */
    private static final String LOCAL_DIR = "./target/tmp/hadoop/"

    /**
     * The location of the testing file as a test resource.
     */
    private static final String RESOURCE_PATH = "src/test/resources/hadoop/"

    /**
     * A HDFS file system that HdfsService talks to to fetch the testing file.
     */
    FileSystem hdfsFileSystem

    /**
     * The location of the testing file on remote HDFS.
     */
    Path remoteHadoopPath

    /**
     * The remote HDFS path given to HdfsService in order to fetch the remote testing file.
     */
    String remotePath

    /**
     * The local path of the fetched file.
     */
    String localPath

    /**
     * The file object that is going to be loaded into HDFS.
     */
    File testingFile

    HdfsService hdfsService

    def setup() {
        hdfsService = new HdfsService(initHdfs())
    }

    def cleanup() {
        hdfsFileSystem.delete(remoteHadoopPath, true)
    }

    def "Fetching the file from Hadoop creates local file"() {
        when: "Fetch the file from Hadoop"
        hdfsService.fetchRemote(remotePath, localPath)

        then: "Local file is created"
        localFileText() == expectedFileText()
    }

    def "Fetching the file from Hadoop returns the file to local"() {
        when: "Fetch the file from Hadoop"
        Observable<File> fileObservable = hdfsService.copyToLocal(remotePath, localPath)

        then: "File at local is returned"
        fileObservable.blockingFirst().text == expectedFileText()

        and: "File is the same as the one on HDFS"
        localFileText() == testingFile.text
    }

    def "Error on file fetch throws IO exception"() {
        given: "A HDFS that throws error on file fetch"
        FileSystem testFileSystem = Mock(FileSystem)
        hdfsService = new HdfsService(testFileSystem)

        when: "Fetch the file from Hadoop"
        hdfsService.fetchRemote(remotePath, localPath)

        then: "IOException is thrown"
        1 * testFileSystem.copyToLocalFile(_, _) >> {throw new IOException()}
        IOException exception = thrown()
    }

    def "Error on file fetch catches IOException and returns throws illegal state exception"() {
        setup: "A HDFS that throws error on file fetch"
        FileSystem testFileSystem = Mock(FileSystem)
        hdfsService = new HdfsService(testFileSystem)

        when: "Fetch the file from Hadoop"
        hdfsService.copyToLocal(remotePath, localPath)

        then: "IOException is caught and IllegalStateException is thrown"
        1 * testFileSystem.copyToLocalFile(_, _) >> {throw new IOException()}
        IOException exception = thrown()
    }

    def "Catting the file from Hadoop returns the file stream"() {
        expect:
        OutputStream fileStream = hdfsService.cat(remotePath)
        fileStream.toString() == expectedFileText()

        cleanup:
        fileStream.close()
    }

    /**
     * Initializes a HDFS file system and load testing file into the HDFS.
     *
     * @return an instance of HDFS file system
     */
    private FileSystem initHdfs() {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new Configuration()).build()
        hdfsFileSystem = cluster.getFileSystem()
        remoteHadoopPath = new Path(hdfsFileSystem.getWorkingDirectory(), FILENAME)

        remotePath = remoteHadoopPath.toString()
        localPath = LOCAL_DIR + FILENAME

        testingFile = new File(RESOURCE_PATH + FILENAME)

        // Make sure the original file exists as a resource and isn't in the target destination yet
        assert testingFile.exists()

        // Make sure the file isn't in test filesystem yet
        assert ! hdfsFileSystem.exists(remoteHadoopPath)

        // Copy the local file from resource directory to the HDFS filesystem
        DataOutputStream dataOutputStream = hdfsFileSystem.create(new Path(FILENAME))
        IOUtils.copy(new FileInputStream(testingFile), dataOutputStream)
        dataOutputStream.close()

        // Verify that the file is now visible on the filesystem
        assert hdfsFileSystem.exists(remoteHadoopPath)

        return hdfsFileSystem
    }

    /**
     * Returns the file content of the testing file sitting locally.
     *
     * @return the file content of the testing file sitting locally
     */
    String localFileText() {
        new File(localPath).text
    }

    /**
     * Returns the expected file content which is what's been loaded into HDFS
     *
     * @return the expected file content
     */
    String expectedFileText() {
        testingFile.text
    }
}
