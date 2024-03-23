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

import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.DELETE
import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.GET
import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.PUT

import org.asynchttpclient.BoundRequestBuilder
import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClientConfig

import spock.lang.Specification
import spock.lang.Unroll

class HttpFsServiceConfigSpec extends Specification {

    static final HTTPFS_SERVER_HOST = "localhost"

    static final RESOURCE_IDENTIFIER = "/path/someResource"

    HttpFsServiceConfig config

    def setup() {
        config = new HttpFsServiceConfig(
                new DefaultAsyncHttpClient(
                        new DefaultAsyncHttpClientConfig.Builder()
                                .setFollowRedirect(true)
                                .setRequestTimeout(3600000)
                                .build()
                ),
                HTTPFS_SERVER_HOST
        )
    }

    def "Config provides complete URI for HttpFS server"() {
        expect:
        config.getHdfsUri(RESOURCE_IDENTIFIER).toString() == expectedUri().toString()
    }

    @Unroll
    def "Config provides complete #method.name() request to HttpFS server with params #method.toString()"() {
        when: "request is generated for a particular method"
        BoundRequestBuilder boundRequestBuilder = config.{method.name().toLowerCase()}(RESOURCE_IDENTIFIER)

        then: "request contains the complete URI for HttpFS server"
        boundRequestBuilder.uri.toString() == expectedUri().toString()

        and: "request contains all needed params"
        boundRequestBuilder.queryParams == params

        where:
        method | params
        GET    | GET.params
        PUT    | PUT.params
        DELETE | DELETE.params
    }

    /**
     * Returns the expected HttpFS URI without params.
     *
     * @return the expected HttpFS URI without params
     */
    def expectedUri() {
        return new URI("http://localhost:14000/webhdfs/v1/path/someResource")
    }
}
