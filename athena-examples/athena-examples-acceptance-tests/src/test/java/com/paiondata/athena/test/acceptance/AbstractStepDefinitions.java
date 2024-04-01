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
package com.paiondata.athena.test.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link AbstractStepDefinitions} contains utility methods for common operations, such as reading test resources and
 * sending HTTP POST requests.
 * <p>
 * All effective step definition class should extend this class so that tests can be developed quickly and efficiently.
 */
abstract class AbstractStepDefinitions {

    /**
     * JSON utility.
     */
    protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * JAX-RS resource path for uploading file.
     */
    protected static final String FILE_UPLOAD_PATH = "/file/upload";

    /**
     * JAX-RS resource path for downloading file.
     */
    protected static final String FILE_DOWNLOAD_PATH = "/file/download";

    /**
     * GraphQL resource path for querying file metadata.
     */
    protected static final String METADATA_ENDPOINT_PATH = "/metadata/graphql";

    /**
     * Loads a resource file, under "payload" resource directory, as a {@code String} object given that resource file
     * name.
     *
     * @param resourceName  The specified resource file name
     *
     * @return the resource file content as a single {@code String}
     *
     * @throws NullPointerException if {@code resourceName} is {@code null}
     * @throws IllegalStateException if an I/O error occurs reading from the resource file stream
     * @throws IllegalArgumentException  if resource path is not formatted strictly according to RFC2396 and cannot be
     * converted to a URI.
     */
    @NotNull
    protected String payload(final @NotNull String resourceName) {
        return resource("payload", resourceName);
    }

    /**
     * Loads a resource file, under "path-param" resource directory, as a {@code String} object given that resource file
     * name.
     *
     * @param resourceName  The specified resource file name
     *
     * @return the resource file content as a single {@code String}
     *
     * @throws NullPointerException if {@code resourceName} is {@code null}
     * @throws IllegalStateException if an I/O error occurs reading from the resource file stream
     * @throws IllegalArgumentException  if resource path is not formatted strictly according to RFC2396 and cannot be
     * converted to a URI.
     */
    @NotNull
    protected String pathParam(final @NotNull String resourceName) {
        return resource("path-param", resourceName);
    }

    /**
     * Loads a resource file, under "file" resource directory, as a {@code String} object given that resource file
     * name.
     *
     * @param resourceName  The specified resource file name
     *
     * @return the resource file content as a single {@code String}
     *
     * @throws NullPointerException if {@code resourceName} is {@code null}
     * @throws IllegalStateException if an I/O error occurs reading from the resource file stream
     * @throws IllegalArgumentException  if resource path is not formatted strictly according to RFC2396 and cannot be
     * converted to a URI.
     */
    @NotNull
    protected String file(final @NotNull String resourceName) {
        return resource("file", resourceName);
    }

    /**
     * Loads a resource file content as a {@code String} object according to a provided resource path.
     * <p>
     * The resource path is defined by two components:
     * <ol>
     *     <li> a relative path under "resource" folder
     *     <li> the name of the resource file
     * </ol>
     * For example, when we would like to read
     * "src/test/resources/payload/metadata/multiple-fields-metadata-request.json", then the relative path is
     * "payload/metadata" and the name of the resource file is "multiple-fields-metadata-request.json"
     *
     * @param resourceDirPath  The relative path under "resource" folder
     * @param resourceFilename  The specified resource file name
     *
     * @return the resource file content as a single {@code String}
     *
     * @throws NullPointerException if {@code resourceFilename} is {@code null}
     * @throws IllegalStateException if an I/O error occurs reading from the resource file stream
     * @throws IllegalArgumentException  if resource path is not formatted strictly according to RFC2396 and cannot be
     * converted to a URI.
     */
    @NotNull
    protected String resource(final @NotNull String resourceDirPath, final @NotNull String resourceFilename) {
        Objects.requireNonNull(resourceDirPath);
        Objects.requireNonNull(resourceFilename);

        final String resource = String.format(
                "%s/%s",
                resourceDirPath.endsWith("/")
                        ? resourceDirPath.substring(0, resourceDirPath.length() - 1)
                        : resourceDirPath,
                resourceFilename
        );

        try {
            return new String(
                    Files.readAllBytes(
                            Paths.get(
                                    Objects.requireNonNull(
                                            this.getClass()
                                                    .getClassLoader()
                                                    .getResource(resource)
                                    )
                                            .toURI()
                            )
                    )
            );
        } catch (final IOException exception) {
            final String message = String.format("Error reading file stream from '%s'", resource);
            throw new IllegalStateException(message, exception);
        } catch (final URISyntaxException exception) {
            final String message = String.format("'%s' is not a valid URI fragment", resource);
            throw new IllegalArgumentException(message, exception);
        }
    }

    /**
     * Sends a GET request to a provided URL using specified set of GET parameters.
     *
     * @param url  The specified URL, for example, {@code http://192.168.1.101:8080/api/v2/users}; cannot be
     * {@code null}
     * @param parameters  A mapping of request parameter's key-value pairs; cannot be null
     *
     * @return the <a href="https://datatracker.ietf.org/doc/html/rfc2616#page-43">response entity</a>
     *
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalStateException if there was a HTTP error
     * @throws URISyntaxException if the {@code parameters} or {@code url} is in invalid format
     *
     * @deprecated Please use RestAssured framework instead
     */
    @NotNull
    @Deprecated
    protected JsonNode httpGet(final @NotNull String url, final @NotNull Map<String, String> parameters)
            throws URISyntaxException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(parameters);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet(url);

            final URI uri = new URIBuilder(httpGet.getURI())
                    .addParameters(
                            parameters
                                    .entrySet()
                                    .stream()
                                    .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                                    .collect(Collectors.toList())
                    )
                    .build();
            httpGet.setURI(uri);

            return JSON_MAPPER.readTree(EntityUtils.toString(client.execute(httpGet).getEntity()));
        } catch (final IOException exception) {
            throw new IllegalStateException("HTTP GET error.", exception);
        }
    }

    /**
     * Sends a POST request to a provided URL using a specified payload.
     * <p>
     * This methods adds the following default headers so that callers do not need to specify them
     * <ul>
     *     <li> "Accept": "application/json"
     *     <li> "Content-Type": "application/json"
     * </ul>
     *
     * @param url  The specified URL, for example, {@code http://192.168.1.101:8080/api/v2/users}
     * @param payloadFile  A relative path under test resource folder. For example, assumming the following resource
     * folder structure
     * <pre>
     * src/
     * ├─ test/
     * │  ├─ java/
     * │  ├─ resources/
     * │  │  ├─ payload/
     * │  │  │  ├─ create-new-user.json
     * </pre>
     * To post the {@code create-new-user.json} using this method, the {@code payloadFile} should be
     * {@code payload/create-new-user.json}. {@code payloadFile} cannot be null
     * @param extraHeaders a mapping of extra header name to header values not mentioned above; cannot be null
     *
     * @return the <a href="https://datatracker.ietf.org/doc/html/rfc2616#page-43">response entity</a>
     *
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalStateException if there was a HTTP error
     * @throws URISyntaxException if the {@code payloadFile} does not form a valid resource path
     *
     * @deprecated Please use RestAssured framework instead
     */
    @NotNull
    @Deprecated
    @SuppressWarnings({"ConstantConditions", "JavaDoc"})
    protected JsonNode httpPost(
            final @NotNull String url,
            final @NotNull String payloadFile,
            final @NotNull Map<String, String> extraHeaders
    ) throws URISyntaxException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(payloadFile);
        Objects.requireNonNull(extraHeaders);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            extraHeaders.forEach(httpPost::setHeader);

            httpPost.setEntity(
                    new StringEntity(
                            new String(
                                    Files.readAllBytes(
                                            Paths.get(
                                                    this.getClass()
                                                            .getClassLoader()
                                                            .getResource(payloadFile)
                                                            .toURI()
                                            )
                                    )
                            )
                    )
            );

            return JSON_MAPPER.readTree(EntityUtils.toString(client.execute(httpPost).getEntity()));
        } catch (final IOException exception) {
            throw new IllegalStateException("HTTP POST error.", exception);
        }
    }
}
