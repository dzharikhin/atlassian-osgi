package org.jrx.atlassian.mybatis.util;

import org.jrx.atlassian.mybatis.api.DbInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jrx.atlassian.mybatis.util.JdbcUtil.executeStringResultQuery;

/**
 * Created by dzharikhin on 09.12.2015.
 */
public class DbInfoProviderImpl implements DbInfoProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DbInfoProviderImpl.class);

    protected final Collection<DbAdapter> supportedEngineAdapters;

    @Autowired
    public DbInfoProviderImpl(Collection<DbAdapter> supportedEngineAdapters) {
        this.supportedEngineAdapters = supportedEngineAdapters;
    }

    @Override
    public String getDatabaseId(final DataSource dataSource) throws SQLException {
        return getDbAdapter(dataSource).getDbName();
    }

    protected DbAdapter getDbAdapter(DataSource dataSource) throws SQLException {
        checkNotNull(dataSource, "dataSource cannot be null");
        final String driverName = getDriverName(dataSource);
        if (this.supportedEngineAdapters != null) {
            for (DbAdapter adapter : supportedEngineAdapters) {
                for (String searchPattern : adapter.getDriverSearchPatterns()) {
                    if (driverName.toLowerCase().contains(searchPattern)) {
                        return adapter;
                    }
                }

            }
        }
        List<String> list = new ArrayList<>();
        list.toArray(new String[list.size()]);
        LOG.warn("DB vendor name is unknown, driverName={}", driverName);
        throw new SQLException("DB vendor for driver" + driverName + " is unknown");
    }

    protected String getDriverName(DataSource dataSource) throws SQLException {
        Connection con = dataSource.getConnection();
        DatabaseMetaData metaData = con.getMetaData();
        return metaData.getDriverName();
    }

    @Override
    public void setProperties(Properties p) {
        //ignore - bad designed interface
    }

    @Override
    public String getSchema(DataSource dataSource) throws SQLException {
        return executeStringResultQuery(dataSource.getConnection(), getDbAdapter(dataSource).getCurrentSchemaSql());
    }
}
