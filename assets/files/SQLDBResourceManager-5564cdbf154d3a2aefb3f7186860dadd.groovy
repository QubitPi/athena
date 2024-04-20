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
package io.github.qubitpi.athena.example.books.application

import org.flywaydb.core.Flyway

/**
 * A SQL DB resource manager that maintains DB-related configs of integration tests. It also offers capability to load
 * testing data into a Derby testing DB.
 */
class SQLDBResourceManager {

    private static final Flyway MIGRATOR = buildMigrator();

    /**
     * Migrates database schema to local testing database.
     */
    static void migrateDatabase() {
        cleanupDatabase()
        MIGRATOR.migrate()
    }

    /**
     * Removes schema
     */
    static void cleanupDatabase() {
        MIGRATOR.clean()
    }


    /**
     * Instantiate and returns an instance of Flyway migration instance that loads testing data into the local Derby
     *
     * @return the Flyway migration instance
     */
    @SuppressWarnings('GroovyAccessibility')
    private static Flyway buildMigrator() {
        Flyway flyway = new Flyway()

        flyway.setDataSource(new BooksBinderFactory().dataSourceProvider.get())
        flyway.setValidateOnMigrate(true)
        flyway.setBaselineOnMigrate(true)
        flyway.setLocations("filesystem:./src/test/resources/db/migration/V1__book_data")

        return flyway
    }
}
