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
package com.qubitpi.athena.filestore.hdfs.hadoop.httpfs;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.DELETE;
import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.GET;
import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.PUT;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.UriBuilder;

/**
 * {@link HttpFsService} configuration holder class.
 * <p>
 * Application that uses {@link HttpFsService} needs to provide a HttpFS server host name and an {@link AsyncHttpClient}
 * instance.
 * <p>
 * The default port is 14000.
 *
 * <h1>AsyncHttpClient</h1>
 * The {@link HttpFsService} use {@link AsyncHttpClient} to talk to the HttpFS server. Here is an example method for
 * application to get an {@link AsyncHttpClient} instance:
 * <pre>
 * {@code
 *     AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(
 *             new DefaultAsyncHttpClientConfig.Builder()
 *                     .setFollowRedirect(true)
 *                     .setRequestTimeout(3600000)
 *                     .build()
 *     )
 * }
 * </pre>
 * Note that a request timeout is set. The async download hangs after this amount of time. If your file is very large,
 * you want download to finish before it hits timeout.
 *
 * <h1>Create HdfsServiceConfig instance</h1>
 * Call its constructor {@link #HttpFsServiceConfig(AsyncHttpClient, String)} like
 * <pre>
 * {@code
 *     HttpFsServiceConfig httpFsServiceConfig = new HttpFsServiceConfig(asyncHttpClient, ycaCert);
 * }
 * </pre>
 * TODO - inject custom request header
 */
public class HttpFsServiceConfig {

    /**
     * URI scheme of HttpFS server URI.
     *
     * TODO - make this an variable, i.e. HTTP or HTTPS
     */
    private static final String URI_SCHEME = "http";

    /**
     * HttpFS server port.
     */
    private static final int HTTPFS_SERVER_PORT = 14000;

    /**
     * An asynchronous http client object that sends service request to the HttpFS server.
     */
    private final AsyncHttpClient asyncHttpClient;

    /**
     * Host name of HttpFs server that that {@link HttpFsService} communicates with.
     */
    private final String httpFsServerHost;

    /**
     * Constructs a new {@code HttpFsServiceConfig} with provided HttpFS server client and server hostname.
     *
     * @param asyncHttpClient  An asynchronous http client object that sends service request to the HttpFS server
     * @param httpFsServerHost  Host name of HttpFs server that that {@link HttpFsService} communicates with
     */
    public HttpFsServiceConfig(final AsyncHttpClient asyncHttpClient, final String httpFsServerHost) {
        this.asyncHttpClient = asyncHttpClient;
        this.httpFsServerHost = httpFsServerHost;
    }

    /**
     * Returns asynchronous http client object that is used to send service request to the HttpFS server.
     *
     * @return the asynchronous http client object that is used to send service request to the HttpFS server
     */
    public AsyncHttpClient getAsyncHttpClient() {
        return asyncHttpClient;
    }

    /**
     * Returns the host name of HttpFs server that that {@link HttpFsService} communicates with.
     *
     * @return the host name of HttpFs server that that {@link HttpFsService} communicates with
     */
    public String getHttpFsServerHost() {
        return httpFsServerHost;
    }

    /**
     * Returns a request builder for resource retrival.
     *
     * @param resourceName  Identifier for the resource to store in HDFS
     *
     * @return request builder for resource retrieval
     *
     * @throws URISyntaxException if there is a problem while constructing URI
     */
    protected BoundRequestBuilder get(final String resourceName) throws URISyntaxException {
        return getAsyncHttpClient()
                .prepareGet(getHdfsUri(resourceName).toString())
                .setQueryParams(GET.getParams());
    }

    /**
     * Returns a request builder for resource storage.
     *
     * @param resourceName  Identifier for the resource to store in HDFS
     *
     * @return request builder for store operation
     *
     * @throws URISyntaxException if there is a problem while constructing URI
     */
    protected BoundRequestBuilder put(final String resourceName) throws URISyntaxException {
        return getAsyncHttpClient()
                .preparePut(getHdfsUri(resourceName).toString())
                .setQueryParams(PUT.getParams())
                .setHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM);
    }

    /**
     * Returns a request builder for resource deletion.
     *
     * @param resourceName  Identifier for the resource to delete in HDFS
     *
     * @return request builder for deletion
     *
     * @throws URISyntaxException if there is a problem while constructing URI
     */
    protected BoundRequestBuilder delete(final String resourceName) throws URISyntaxException {
        return getAsyncHttpClient()
                .prepareDelete(getHdfsUri(resourceName).toString())
                .setQueryParams(DELETE.getParams());
    }

    /**
     * Returns an URI that locates a resource on HDFS.
     *
     * @param resourceName  A complete resource path(after port number) of a resource on the HDFS
     *
     * @return instance of valid URI
     *
     * @throws URISyntaxException if there is an error in URI string
     */
    private URI getHdfsUri(final String resourceName) throws URISyntaxException {
        // TODO - replace string concat with URI bulder pattern
        return UriBuilder
                .fromPath(URI_SCHEME + "://" + getHttpFsServerHost() + ":" + HTTPFS_SERVER_PORT)
                .path("/webhdfs/v1/")
                .path(resourceName)
                .build();
    }
}
