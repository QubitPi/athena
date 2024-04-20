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
package io.github.qubitpi.athena.example.books.application;

import io.github.qubitpi.athena.application.ApplicationState;
import io.github.qubitpi.athena.application.BinderFactory;
import io.github.qubitpi.athena.application.JerseyTestBinder;

/**
 * TestBinder with Book application configuration specializaation.
 */
public class BookJerseyTestBinder extends JerseyTestBinder {

    /**
     * Constructor.
     *
     * @param doStart  Whether or not to start the application immediately after this constructor is invoked
     * @param resourceClasses  Resource classes to load into the application
     */
    public BookJerseyTestBinder(final boolean doStart, final Class<?>... resourceClasses) {
        super(doStart, resourceClasses);
    }

    /**
     * Constructor.
     *
     * @param doStart  Whether or not to start the application immediately after this constructor is invoked
     * @param applicationState  A set of mocked objects that are to be injected during testing phase
     * @param resourceClasses  Resource classes to load into the application
     */
    public BookJerseyTestBinder(
            final boolean doStart,
            final ApplicationState applicationState,
            final Class<?>... resourceClasses
    ) {
        super(doStart, applicationState, resourceClasses);
    }

    @Override
    protected BinderFactory buildBinderFactory(final ApplicationState applicationState) {
        return new BooksBinderFactory();
    }
}
