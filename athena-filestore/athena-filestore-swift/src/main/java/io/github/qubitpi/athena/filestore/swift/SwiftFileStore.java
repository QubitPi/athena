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
package io.github.qubitpi.athena.filestore.swift;

import io.github.qubitpi.athena.file.File;
import io.github.qubitpi.athena.file.identifier.FileIdGenerator;
import io.github.qubitpi.athena.filestore.FileStore;

import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;

import jakarta.validation.constraints.NotNull;
import net.jcip.annotations.NotThreadSafe;

import java.io.InputStream;
import java.util.Objects;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * An OpenStack Swift implementation of {@link FileStore}.
 */
@Singleton
@NotThreadSafe
public class SwiftFileStore implements FileStore {

    /**
     * The container name where all files are going to be stored in.
     */
    public static final String DEFAULT_CONTAINER = "default-container";

    private final Account account;
    private final FileIdGenerator fileIdGenerator;

    /**
     * DI constructor.
     *
     * @param account  A Swift client allowing access to the various containers underneath it. Note that you need to
     * call {@link Account#getContainer(String)} to work on Containers and then pass the {@link Account} into this
     * constructor. The method returns a stub for dealing with Containers, but does not create a container in the Object
     * Store. The creation only takes place when you run the {@link Container#create()} method on a Container.
     * Information on the container will not be retrieved until the time you actually call on that information - ie,
     * information is lazily loaded.
     * @param fileIdGenerator  An object that provides file unique identifiers
     *
     * @throws NullPointerException if any constructor argument is {@code null}
     */
    @Inject
    public SwiftFileStore(final @NotNull Account account, final @NotNull FileIdGenerator fileIdGenerator) {
        this.account = Objects.requireNonNull(account);
        this.fileIdGenerator = Objects.requireNonNull(fileIdGenerator);
    }

    @Override
    public String upload(final File file) {
        Objects.requireNonNull(file);
        final String fileId = fileIdGenerator.apply(file);

        account
                .getContainer(DEFAULT_CONTAINER)
                .getObject(fileId)
                .uploadObject(file.getFileContent());

        return fileId;
    }

    @Override
    public InputStream download(final String fileId) {
        return account
                .getContainer(DEFAULT_CONTAINER)
                .getObject(Objects.requireNonNull(fileId))
                .downloadObjectAsInputStream();
    }
}
