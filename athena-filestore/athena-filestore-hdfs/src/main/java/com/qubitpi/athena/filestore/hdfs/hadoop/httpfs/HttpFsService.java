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

import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.DELETE;
import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.GET;
import static org.bitbucket.jack20191124.hadoop.httpfs.Operation.PUT;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Response;
import org.bitbucket.jack20191124.hadoop.httpfs.exception.HttpFsException;
import org.bitbucket.jack20191124.hadoop.httpfs.exception.ResourceAccessFailException;
import org.bitbucket.jack20191124.hadoop.httpfs.exception.ResourceDeletionFailException;
import org.bitbucket.jack20191124.hadoop.httpfs.exception.ResourceStoreFailException;
import org.bitbucket.jack20191124.hadoop.httpfs.exception.UnauthorizedException;

import io.reactivex.Observable;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response.Status;

/**
 * A HDFS client that fetches files from HDFS via HttpFS.
 * <p>
 * HttpFS is a server that provides a REST HTTP gateway supporting all HDFS File System operations (read and write). And
 * it is interoperable with the <b>webhdfs</b> REST HTTP API. HttpFS can be used to transfer data between clusters
 * running different versions of Hadoop (overcoming RPC versioning issues), for example using Hadoop DistCP. HttpFS can
 * be used to access data in HDFS on a cluster behind a firewall (the HttpFS server acts as a gateway and is the only
 * system that is allowed to cross the firewall into the cluster). HttpFS also provides Hadoop proxy user support.
 */
public class HttpFsService {

    /**
     * Error message of exception for request that is not authorized on HttpFS server.
     */
    private static final String UNAUTHORIZED_ERROR_FORMAT = "Not authorized to access the resource for given id '%s'.";

    /**
     * Error message of exception for GET request to HttpFS server that receives error.
     */
    private static final String RESOURCE_RETRIEVAL_FAILURE_FORMAT =
            "Unable to retrieve the resource for given resource name: '%s'. %s";

    /**
     * Error message of exception for PUT request to HttpFS server that receives error.
     */
    private static final String RESOURCE_STORAGE_FAILURE_FORMAT =
            "Unable to store the resource for resource name '%s'. %s";

    /**
     * Error message of exception for DELETE request to HttpFS server that receives error.
     */
    private static final String RESOURCE_DELETION_ERROR_FORMAT =
            "Unable to delete the resource for resource name '%s'. %s";

    /**
     * Error message of exception for requests, that are not GET, PUT, or DELETE, to HttpFS server that receives error.
     */
    private static final String UNKNOWN_EXCEPTION_HTTPFS = "Unknown error during '%s' %s transaction with HDFS.";

    /**
     * An object containing HttpFS service config details.
     */
    private final HttpFsServiceConfig config;

    /**
     * Constructs a new {@code HttpFsService} with a specified service config.
     *
     * @param config  An object containing HDFS client config details
     */
    public HttpFsService(final HttpFsServiceConfig config) {
        this.config = config;
    }

    /**
     * Retrieve the resource from HDFS for given resource name.
     *
     * @param resourceName identifier for the resource stored in HDFS
     *
     * @return observable with resource or error
     */
    public Observable<Response> retrieveResource(final String resourceName) {
        try {
            return Observable.fromFuture(config.get(resourceName).execute())
                    .switchMap(
                            response -> switchOnStatusCode(
                                    response,
                                    resourceName,
                                    GET
                            )
                    );
        } catch (final URISyntaxException exception) {
            return buildErrorBodyObservable(GET, resourceName);
        }
    }

    /**
     * Retrieve the resource from HDFS for given resource name with the provided async handler.
     *
     * @param resourceName identifier for the resource stored in object store
     * @param asyncHandler handler to process the async request
     * @param <T>  The type of the result returned by the object store request
     *
     * @return resource Observable for the given resource identifier or error Observable
     */
    public <T> Observable<T> retrieveResource(final String resourceName, final AsyncHandler<T> asyncHandler) {
        try {
            return Observable.fromFuture(config.get(resourceName).execute(asyncHandler));
        } catch (final URISyntaxException exception) {
            return buildErrorBodyObservable(GET, resourceName);
        }
    }

    /**
     * Stores the given resource to HDFS associating with its resource name.
     *
     * @param resourceName identifier for the resource stored in object store
     * @param resource resource content to be stored in object store
     *
     * @return resource Observable for the given resource identifier or error Observable
     */
    public Observable<Response> storeResource(final String resourceName, final String resource) {
        try {
            final BoundRequestBuilder boundRequestBuilder = config.put(resourceName);
            boundRequestBuilder.setBody(new ByteArrayInputStream(resource.getBytes()));
            return Observable.fromFuture(boundRequestBuilder.execute())
                    .switchMap(
                            response -> switchOnStatusCode(
                                    response,
                                    resourceName,
                                    PUT
                            )
                    );
        } catch (final URISyntaxException exception) {
            return buildErrorBodyObservable(PUT, resourceName);
        }
    }

    /**
     * Deletes the resource from HDFS for given resource name.
     *
     * @param resourceName identifier for the resource stored in HDFS
     *
     * @return observable with resource or error
     */
    public Observable<Response> deleteResource(final String resourceName) {
        try {
            return Observable.fromFuture(config.delete(resourceName).execute())
                    .switchMap(
                            response -> switchOnStatusCode(
                                    response,
                                    resourceName,
                                    DELETE
                            )
                    );
        } catch (final URISyntaxException exception) {
            return buildErrorBodyObservable(DELETE, resourceName);
        }
    }

    /**
     * Returns a new {@code HttpFsServiceConfig} containing HDFS client config details.
     *
     * @return the new {@code HttpFsServiceConfig} containing HDFS client config details
     */
    public HttpFsServiceConfig getConfig() {
        return new HttpFsServiceConfig(config.getAsyncHttpClient(), config.getHttpFsServerHost());
    }

    /**
     * Returns Observable according to different response types.
     *
     * @param response  Response object which contains the response from HDFS proxy
     * @param resourceName  Identifier for the resource stored in HDFS
     * @param operation  HTTP operation/method to identify the operation to perform. GET to retrieve and PUT to store
     *
     * @return Observable for the given resource identifier or error Observable
     */
    private Observable<Response> switchOnStatusCode(
            final Response response,
            final String resourceName,
            final Operation operation
    ) {
        final Status status = Status.fromStatusCode(response.getStatusCode());
        switch (status) {
            case OK: case CREATED:
                return Observable.just(response);

            case UNAUTHORIZED:
                return Observable.error(
                        new UnauthorizedException(String.format(UNAUTHORIZED_ERROR_FORMAT, resourceName))
                );

            case NOT_FOUND:
                return buildErrorBodyObservable(operation, resourceName, "Resource doesn't exist");

            default:
                return buildErrorBodyObservable(operation, resourceName, String.format("Status is %s", status));
        }
    }

    /**
     * Returns error Observable according to different HTTP methods.
     *
     * @param operation  HTTP operation/method. Expected values are PUT, GET, and DELETE
     * @param resourceName  Identifier for the resource stored in HDFS
     * @param <T>  The type of Observable to return, can be inferred
     *
     * @return  Observable error object with respect to HTTP method
     */
    private <T> Observable<T> buildErrorBodyObservable(final Operation operation, final String resourceName) {
        return buildErrorBodyObservable(operation, resourceName, "");
    }

    /**
     * Returns error Observable according to different HTTP methods.
     *
     * @param operation  HTTP operation/method. Expected values are PUT, GET, and DELETE
     * @param resourceName  Identifier for the resource stored in HDFS
     * @param info  Any additional info to associate with the error message
     * @param <T>  The type of Observable to return, can be inferred
     *
     * @return  Observable error object with respect to HTTP method
     */
    private <T> Observable<T> buildErrorBodyObservable(
            final Operation operation,
            final String resourceName,
            final String info
    ) {
        switch (operation) {
            case GET:
                return Observable.error(
                        new ResourceAccessFailException(
                                String.format(RESOURCE_RETRIEVAL_FAILURE_FORMAT, resourceName, info)
                        )
                );

            case PUT:
                return Observable.error(
                        new ResourceStoreFailException(
                                String.format(RESOURCE_STORAGE_FAILURE_FORMAT, resourceName, info)
                        )
                );

            case DELETE:
                return Observable.error(
                        new ResourceDeletionFailException(
                                String.format(RESOURCE_DELETION_ERROR_FORMAT, resourceName, info)
                        )
                );

            default:
                return Observable.error(
                        new HttpFsException(String.format(UNKNOWN_EXCEPTION_HTTPFS, resourceName, operation))
                );
        }
    }
}
