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
package com.qubitpi.athena.filestore.hdfs.hadoop.httpfs

import static javax.ws.rs.core.Response.Status.CREATED
import static javax.ws.rs.core.Response.Status.OK

import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import org.asynchttpclient.Response

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Integration tests that runs against a real HttpFS server.
 * <p>
 * The tests are ignored on windows operating system, because windows does not allow in-memory HDFS to delete non-empty
 * directories in a normal way and, thus, throws the following external error:
 *
 * "java.io.IOException: Could not fully delete ..\aristotle\target\test\data\dfs\name-0-1"
 *
 * The tests are kept being ignored until there is a work-around
 */
@Ignore
//@IgnoreIf({System.getProperty("os.name").toLowerCase().contains("windows")})
class HttpFsServiceITSpec extends Specification {

    /**
     * Name of the file that is going to be stored into HDFS via HttpFS server.
     */
    private static final String FILENAME = "hadoop-data.txt"

    /**
     * The location of the testing file as a test resource.
     */
    private static final String RESOURCE_PATH = "src/test/resources/hadoop/"

    /**
     * The file object that is going to be loaded into HDFS.
     */
    File testingFile

    HttpFsService httpFsService

    def setupSpec() {
        // pull HDFS docker image & spinup a HDFS container
        Runtime
                .getRuntime()
                .exec("docker pull jack20191124jupiter/hadoop")
                .waitFor()
        Runtime
                .getRuntime()
                .exec("docker run -d --name=hdfs -it -p 50070:50070 -p 8020:8020 -p 50090:50090 -p 50091:50091 -p 50010:50010 -p 50075:50075 -p 50020:50020 -p 14000:14000 jack20191124jupiter/hadoop /etc/init.sh -d")
                .waitFor()
        sleep(30000) // wait for HDFS to start, HttpFS needs more extra time
    }

    def cleanupSpec() {
        // stop & remove container. delete the image
        Runtime
                .getRuntime()
                .exec("docker stop hdfs")
                .waitFor()
        sleep(10000)
        Runtime
                .getRuntime()
                .exec("docker rm hdfs")
                .waitFor()
        sleep(5000)
    }

    def setup() {
        testingFile = new File(RESOURCE_PATH + FILENAME)

        httpFsService = new HttpFsService(
                new HttpFsServiceConfig(
                        new DefaultAsyncHttpClient(
                                new DefaultAsyncHttpClientConfig.Builder()
                                        .setFollowRedirect(true)
                                        .build()
                        ),
                        "localhost"
        ))
    }

    def "Service PUT, GET, and DELETE files via HttpFS server"() {
        when: "we store testing file to HDFS via HttpFS PUT request"
        Response response = httpFsService.storeResource(FILENAME, testingFile.text).blockingFirst()

        then: "the testing file has been created successfully"
        response.statusCode == CREATED.statusCode

        when: "we downloaed the file from HDFS via HttpFS GET request"
        response = httpFsService.retrieveResource(FILENAME).blockingFirst()

        then: "the file we get matches the one on HDFS"
        response.statusCode == OK.statusCode
        response.responseBody == testingFile.text

        when: "we delete the file in HDFS via HttpFS DELETE request"
        response = httpFsService.deleteResource(FILENAME).blockingFirst()

        then: "the testing file has been deleted successfully"
        response.statusCode == OK.statusCode
    }
}
