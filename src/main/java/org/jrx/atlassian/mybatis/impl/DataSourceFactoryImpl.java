package org.jrx.atlassian.mybatis.impl;

import com.atlassian.hibernate.PluginHibernateSessionFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Optional;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationVersion;
import org.jrx.atlassian.mybatis.api.DataSourceFactory;
import org.jrx.atlassian.mybatis.api.DbInfoProvider;
import org.jrx.atlassian.mybatis.cl.BundleDelegatingClassLoader;
import org.jrx.atlassian.mybatis.ds.ConfluenceBasedDataSource;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dzharikhin on 20.11.2015.
 */
public class DataSourceFactoryImpl implements DataSourceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceFactoryImpl.class);

    private final PluginHibernateSessionFactory sessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final DbInfoProvider dbInfoProvider;

    @Autowired
    public DataSourceFactoryImpl(
        PluginHibernateSessionFactory sessionFactory,
        TransactionTemplate transactionTemplate,
        DbInfoProvider dbInfoProvider
    ) {
        this.sessionFactory = sessionFactory;
        this.transactionTemplate = transactionTemplate;
        this.dbInfoProvider = dbInfoProvider;
    }

    @Override
    public DataSource getDataSource() {
        return new ConfluenceBasedDataSource(sessionFactory);
    }

    @Override
    public DataSource getDataSource(final BundleContext bundleContext, final String versionTableName, final Properties properties) throws Exception {
        checkNotNull(bundleContext);
        checkNotNull(versionTableName);
        validateProperties(properties);
        final DataSource dataSource = getDataSource();
        final Optional<Exception> migrationResult = transactionTemplate.execute(
            new TransactionCallback<Optional<Exception>>() {
                @Override
                public Optional<Exception> doInTransaction() {
                    try {
                        LOG.debug("Started migration procedure");
                        final Flyway flyway = getConfiguredFlyway(
                            dataSource,
                            new BundleDelegatingClassLoader(bundleContext.getBundle()),
                            versionTableName,
                            properties
                        );
                        try {
                            flyway.baseline();
                        } catch (FlywayException e) {
                            LOG.info("Schema baselining status", e.getMessage());
                        }
                        flyway.migrate();
                        LOG.debug("Migration succeed");
                        return Optional.absent();
                    } catch (Exception e) {
                        return Optional.of(e);
                    }
                }
            }
        );
        if (migrationResult.isPresent()) {
            LOG.error("Migration failed with exception", migrationResult.get());
            throw migrationResult.get();
        }
        return dataSource;



    }

    private void validateProperties(Properties properties) {
        if (properties != null) {
            String locationString = properties.getProperty("flyway.locations");
            String[] locations = StringUtils.tokenizeToStringArray(locationString, ",");
            if (locations.length > 1) {
                throw new IllegalArgumentException(
                    "There should be only one location. Vendor specific subfolder will be" + "automatically added to initial location on migration"
                );
            }
        }
    }

    private Flyway getConfiguredFlyway(
        DataSource ds,
        ClassLoader classLoader,
        String versionTableName,
        Properties properties
    ) throws Exception {
        Flyway flyway = new Flyway();
        if (properties != null) {
            flyway.configure(properties);
        }
        if (properties == null || properties.get("flyway.baselineVersion") == null) {
            flyway.setBaselineVersion(MigrationVersion.fromVersion("0"));
        }
        flyway.setDataSource(ds);
        flyway.setClassLoader(classLoader);
        flyway.setTable(versionTableName);

        flyway.setSchemas(dbInfoProvider.getSchema(ds));
        flyway.setLocations(flyway.getLocations()[0] + File.separator + dbInfoProvider.getDatabaseId(ds));
        return flyway;
    }

}
