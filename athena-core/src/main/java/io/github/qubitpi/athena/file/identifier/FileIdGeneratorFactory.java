/*
 * Copyright 2024 Jiaqi Liu
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.qubitpi.athena.application.AbstractBinderFactory;
import io.github.qubitpi.athena.config.SystemConfig;
import io.github.qubitpi.athena.config.SystemConfigFactory;
import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.security.NoSuchAlgorithmException;

/**
 * {@link FileIdGeneratorFactory} is an abstraction layer that hides the details of {@link FileIdGenerator} instance
 * creation.
 * <p>
 * Please use {@link #getInstance()} always to get instance of {@link FileIdGenerator} whenever needed.
 */
@Immutable
@ThreadSafe
public class FileIdGeneratorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBinderFactory.class);

    private static final SystemConfig SYSTEM_CONFIG = SystemConfigFactory.getInstance();

    private static final String FILE_ID_HASHING_ALGORITHM_KEY = "file_id_hashing_algorithm";
    private static final String FILE_ID_HASHING_ALGORITHM_DEFAULT = "MD5";

    private static final String FILE_ID_HASHING_ALGORITHM = SYSTEM_CONFIG.getStringProperty(
            SYSTEM_CONFIG.getPackageVariableName(FILE_ID_HASHING_ALGORITHM_KEY)
    ).orElse(FILE_ID_HASHING_ALGORITHM_DEFAULT);

    private static FileIdGenerator instance = null;

    /**
     * Returns a cached and fully initialized {@link FileIdGenerator} object with the "MD5" as the default hashing
     * algorithm.
     * <p>
     * One can override the hashing algorithm by setting a {@link SystemConfig config property} whose key is
     * {@code file_id_hashing_algorithm}.
     *
     * @return the same instance
     *
     * @throws IllegalStateException if the particular cryptographic algorithm is requested but is not available in the
     * environment.
     */
    @NotNull
    public static FileIdGenerator getInstance() {
        if (instance == null) {
            try {
                instance = FileNameAndUploadedTimeBasedIdGenerator.algorithm(FILE_ID_HASHING_ALGORITHM);
            } catch (final NoSuchAlgorithmException exception) {
                final String message = String.format(
                        "'%s' is not a valid message digest algorithm name",
                        FILE_ID_HASHING_ALGORITHM
                );
                LOG.error(message, exception);
                throw new IllegalStateException(message, exception);
            }
        }

        return instance;
    }
}
