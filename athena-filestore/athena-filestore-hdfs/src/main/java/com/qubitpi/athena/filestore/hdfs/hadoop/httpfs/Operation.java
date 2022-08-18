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

import static org.apache.hadoop.http.resource.JerseyResource.OP;

import org.asynchttpclient.Param;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum object that represents HTTP method.
 * <p>
 * Each enum comes with a list of HTTP params specific to that particular operation.
 */
public enum Operation {

    /**
     * Represents HTTP GET method with parameters {@code ?op=open}.
     */
    GET(Collections.singletonList(new Param(OP, "open"))),

    /**
     * Represents HTTP PUT method with parameters {@code ?op=create&overwrite=true}.
     */
    PUT(
            Arrays.asList(
                    new Param(OP, "CREATE"),
                    new Param("overwrite", Boolean.TRUE.toString()),
                    new Param("data", Boolean.TRUE.toString())
            )
    ),

    /**
     * Represents HTTP DELETE method with parameters {@code ?op=delete&recursive=true}.
     */
    DELETE(Arrays.asList(new Param(OP, "delete"), new Param("recursive", Boolean.TRUE.toString())));

    /**
     * List of parameters in Param model.
     */
    private final List<Param> params;

    /**
     * Constructs an {@code Operation} with specified parameter list.
     *
     * @param params  List of parameters in Param model
     */
    Operation(final List<Param> params) {
        this.params = Collections.unmodifiableList(
                Stream.concat(
                        params.stream(),
                        // TODO make value of user.name configurable
                        Stream.of(new Param("user.name", "root"))
                )
                .collect(Collectors.toList())
        );
    }

    /**
     * Returns the list of parameters in Param model.
     *
     * @return the list of parameters in Param model
     */
    public List<Param> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return String.format(
                "?%s",
                getParams()
                        .stream()
                        .map(param -> String.format("%s=%s", param.getName(), param.getValue()))
                        .collect(Collectors.joining("&"))
        );
    }
}
