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

import org.asynchttpclient.Response
import org.bitbucket.jack20191124.hadoop.httpfs.exception.ResourceAccessFailException
import org.bitbucket.jack20191124.hadoop.httpfs.exception.ResourceDeletionFailException
import org.bitbucket.jack20191124.hadoop.httpfs.exception.ResourceStoreFailException

import io.reactivex.observers.TestObserver
import spock.lang.Specification
import spock.lang.Unroll

class HttpFsServiceSpec extends Specification {

    HttpFsService httpFsService
    String resourceName = "resource"
    String resource = "This is the resource we are storing in HDFS"

    def setup() {
        httpFsService =  new HttpFsService(Mock(HttpFsServiceConfig))
    }

    @Unroll
    def "#error.getSimpleName() is returned from a bad #method.name()"() {
        given: "a observer that will subscribe to an error"
        TestObserver<String> testObserver = new TestObserver<>()

        when: "the observer subscribes the error"
        httpFsService.buildErrorBodyObservable(method, resourceName).subscribe(testObserver)

        then: "corresponding error type has been subscribed based on HTTP method"
        testObserver.assertError(error)

        where:
        method || error
        GET    || ResourceAccessFailException
        PUT    || ResourceStoreFailException
        DELETE || ResourceDeletionFailException
    }

    @Unroll
    def "#operation.name() with a #httpCode response subscribes to a response body of '#expectedResponse'"() {
        given: "a mock response with specified response body and response code, and an observer of the response"
        Response response = Stub(Response) {
            getResponseBody() >> resource
            getStatusCode() >> httpCode
        }
        TestObserver<String> observer = new TestObserver<>()

        when: "the observer subscribes the response"
        httpFsService.switchOnStatusCode(response, resourceName, operation).map { localResponse ->
            response.responseBody
        }.subscribe(observer)

        then: "the observer asynchronously subscribes the specified response body"
        observer.onNext([expectedResponse])

        where:
        httpCode | operation || expectedResponse
        200      | GET       || "This is the resource we are storing in HDFS"
        200      | DELETE    || "This is the resource we are storing in HDFS"
        201      | PUT       || "This is the resource we are storing in HDFS"
    }

    @Unroll
    def "Status code #errorCode from a #operation.name() operation triggers #error.getSimpleName()"() {
        given: "a mock response with specified 404 response code, and an observer of the 404"
        Response response = Stub(Response) {
            getStatusCode() >> errorCode
        }
        TestObserver<String> testObserver = new TestObserver<>()

        when: "the observer subscribes the error"
        httpFsService.switchOnStatusCode(response, resourceName, operation).subscribe(testObserver)

        then: "the observer asynchronously subscribes the error"
        testObserver.assertError(error)

        where:
        errorCode | operation || error
        404       | GET       || ResourceAccessFailException
        500       | PUT       || ResourceStoreFailException
    }
}
