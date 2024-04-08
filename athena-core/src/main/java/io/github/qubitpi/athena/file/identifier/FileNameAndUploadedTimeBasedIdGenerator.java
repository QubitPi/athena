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

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * {@link FileNameAndUploadedTimeBasedIdGenerator} generates file ID as hashes using hashing algorithm.
 * <p>
 * {@link FileNameAndUploadedTimeBasedIdGenerator} outputs the file ID by generating a
 * {@link MessageDigest fixed-length hash value} based on the filename and the time (ms-precision) on which
 * {@link FileNameAndUploadedTimeBasedIdGenerator#apply(File)} is invoked an and then {@link Base64 base-64 encoding}
 * that hash value as the final file ID
 */
@Immutable
@ThreadSafe
public class FileNameAndUploadedTimeBasedIdGenerator implements FileIdGenerator {

    private final MessageDigest messageDigest;

    /**
     * Constructor.
     *
     * @param messageDigest  An object that provides the functionality of a message digest algorithm, such as SHA-1 or
     * SHA-256.
     *
     * @throws NullPointerException if {@code messageDigest} is {@code null}
     */
    protected FileNameAndUploadedTimeBasedIdGenerator(final @NotNull MessageDigest messageDigest) {
        this.messageDigest = Objects.requireNonNull(messageDigest);
    }

    /**
     * Returns a {@link FileIdGenerator} object that generates the ID using hash and base64 encoding.
     *
     * @param algorithm  The name of the algorithm requested. See the MessageDigest section in the
     * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest">Java
     * Cryptography Architecture Standard Algorithm Name Documentation</a> for information about standard algorithm
     * names.
     *
     * @return a new instance.
     *
     * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for the specified
     * algorithm.
     * @throws NullPointerException if {@code algorithm} is {@code null}
     *
     * @see java.security.Provider
     */
    @NotNull
    public static FileIdGenerator algorithm(final @NotNull String algorithm) throws NoSuchAlgorithmException {
        return new FileNameAndUploadedTimeBasedIdGenerator(
                MessageDigest.getInstance(Objects.requireNonNull(algorithm))
        );
    }

    @Override
    public synchronized String apply(final File file) {
        final String fileName = Objects.requireNonNull(file).getMetaData().getFileName();
        final Date now = new Date();

        getMessageDigest().update(fileName.getBytes(StandardCharsets.US_ASCII));
        getMessageDigest().update(now.toString().getBytes(StandardCharsets.US_ASCII));

        return new String(Base64.getEncoder().encode(getMessageDigest().digest()));
    }

    public MessageDigest getMessageDigest() {
        return messageDigest;
    }
}
