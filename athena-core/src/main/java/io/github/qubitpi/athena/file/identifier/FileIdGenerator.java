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
package io.github.qubitpi.athena.file.identifier;

import io.github.qubitpi.athena.file.File;

import java.util.function.Function;

/**
 * {@link FileIdGenerator} is responsible for generating unique identifiers as strings for any types of files.
 * <p>
 * To generate ID of arbitrary file, use {@link #apply(Object)}
 * <p>
 * This is a functional interface and can therefore be used as the assignment target for a lambda expression or method
 * reference.
 */
@FunctionalInterface
public interface FileIdGenerator extends Function<File, String> {

    // intentionally left blank
}
