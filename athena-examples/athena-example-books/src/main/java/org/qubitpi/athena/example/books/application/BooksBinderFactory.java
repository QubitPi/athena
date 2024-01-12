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
package org.qubitpi.athena.example.books.application;

import static org.qubitpi.athena.config.ErrorMessageFormat.CONFIG_NOT_FOUND;

import org.qubitpi.athena.application.AbstractBinderFactory;
import org.qubitpi.athena.config.SystemConfig;
import org.qubitpi.athena.config.SystemConfigFactory;
import org.qubitpi.athena.filestore.FileStore;
import org.qubitpi.athena.filestore.swift.SwiftFileStore;
import org.qubitpi.athena.metadata.MetaData;
import org.qubitpi.athena.metastore.MetaStore;
import org.qubitpi.athena.metastore.graphql.GraphQLMetaStore;

import org.apache.commons.dbcp2.BasicDataSource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.DataFetcher;
import jakarta.validation.constraints.NotNull;

import jakarta.inject.Provider;
import javax.sql.DataSource;

/**
 * Book specialization of the Abstract Binder Factory, applying Book app configuration objects.
 */
public class BooksBinderFactory extends AbstractBinderFactory {

    /**
     * {@link DerbyDataSourceProvider} injects a connected local Derby instance.
     */
    public static class DerbyDataSourceProvider implements Provider<DataSource> {

        private static final DataSource DATA_SOURCE = initDataSource();

        /**
         * A one-time instantiation of Derby DataSource.
         *
         * @return a new instance
         */
        @NotNull
        private static DataSource initDataSource() {
            final BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
            basicDataSource.setUrl("jdbc:derby:memory:Athena;create=true");
            return basicDataSource;
        }

        @Override
        public DataSource get() {
            return DATA_SOURCE;
        }
    }

    /**
     * {@link MySQLDataSourceProvider} injects a connected local MySQL instance.
     */
    public static class MySQLDataSourceProvider implements Provider<DataSource> {

        private static final String ROOT = "root";

        private static final DataSource POOL_DATA_SOURCE = initDataSource();

        /**
         * A one-time instantiation of MySQL DataSource.
         *
         * @return a new instance
         */
        @NotNull
        private static DataSource initDataSource() {
            final BasicDataSource poolDataSource = new BasicDataSource();

            poolDataSource.setUsername(ROOT);
            poolDataSource.setPassword(ROOT);
            poolDataSource.setUrl("jdbc:mysql://db:3306/Athena?autoReconnect=true&useSSL=false");
            poolDataSource.setDriverClassName("com.mysql.jdbc.Driver");

            return poolDataSource;
        }

        @Override
        public DataSource get() {
            return POOL_DATA_SOURCE;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(BooksBinderFactory.class);
    private static final SystemConfig SYSTEM_CONFIG = SystemConfigFactory.getInstance();

    private static final String DATA_SOURCE_PROVIDER_KEY = "data_source_provider";

    private final String dataSourceProviderClass = SYSTEM_CONFIG.getStringProperty(
            SYSTEM_CONFIG.getPackageVariableName(DATA_SOURCE_PROVIDER_KEY)
    ).orElseThrow(() -> {
        LOG.error(CONFIG_NOT_FOUND.logFormat(DATA_SOURCE_PROVIDER_KEY));
        return new IllegalStateException(CONFIG_NOT_FOUND.format());
    });
    private final Provider<DataSource> dataSourceProvider = initProvider(dataSourceProviderClass);

    @Override
    protected Class<? extends FileStore> buildFileStore() {
        return SwiftFileStore.class;
    }

    @Override
    protected Class<? extends MetaStore> buildMetaStore() {
        return GraphQLMetaStore.class;
    }

    @Override
    protected DataFetcher<MetaData> buildQueryDataFetcher() {
        return new SQLQueryDataFetcher(getDataSource());
    }

    @Override
    protected DataFetcher<MetaData> buildMutationDataFetcher() {
        return new SQLMutationDataFetcher(getDataSource());
    }

    @Override
    protected void afterBinding(final AbstractBinder abstractBinder) {
        final Account account = buildAccount();

        final Container container = account.getContainer(SwiftFileStore.DEFAULT_CONTAINER);

        if (!container.exists()) {
            container.create();
            container.makePublic();
        }

        abstractBinder.bind(account).to(Account.class);
    }

    /**
     * Returns a connected local SQL DB instance.
     *
     * @return a new connected SQL database instance
     */
    @NotNull
    protected DataSource getDataSource() {
        return dataSourceProvider.get();
    }

    /**
     * Spins up a JDBC DataSource factory instance using the athena-example-books configs.
     *
     * @param dataSourceProviderClass  The fully qualified name of the JDBC DataSource factory class, i.e. canonical
     * name
     *
     * @return a new instance
     *
     * @throws IllegalStateException if an error occurs during the instantiation phase
     */
    @NotNull
    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private static Provider<DataSource> initProvider(final @NotNull String dataSourceProviderClass) {
        try {
            return Class.forName(dataSourceProviderClass).asSubclass(Provider.class).newInstance();
        } catch (final ClassNotFoundException exception) {
            final String message = String.format(
                    "Cannot locate DataSource provider class '%s'",
                    dataSourceProviderClass
            );
            throw new IllegalStateException(message, exception);
        } catch (final InstantiationException exception) {
            final String message = String.format("Filed to instantiate class '%s'", dataSourceProviderClass);
            throw new IllegalStateException(message, exception);
        } catch (final IllegalAccessException exception) {
            final String message = String.format(
                    "The class '%s' or its no-args constructor is not accessible",
                    dataSourceProviderClass
            );
            throw new IllegalStateException(message, exception);
        }
    }

    /**
     * Creates an in-memory implementation of the OpenStackClient.
     *
     * @return a new instance
     */
    @NotNull
    private static Account buildAccount() {
        return new AccountFactory()
                .setUsername("chris:chris1234")
                .setPassword("testing")
                .setAuthUrl("http://127.0.0.1:12345/auth/v1.0")
                .setAuthenticationMethod(AuthenticationMethod.BASIC)
                .setMock(true)
                .createAccount();
    }
}
