package org.jrx.atlassian.mybatis.util;

import java.util.Collection;

/**
 * Created by dzharikhin on 10.12.2015.
 */
public class DbAdapter {

    private final String dbName;
    private final String currentSchemaSql;
    private final Collection<String> driverSearchPatterns;


    public DbAdapter(String dbName, String currentSchemaSql, Collection<String> driverSearchPatterns) {
        this.dbName = dbName;
        this.currentSchemaSql = currentSchemaSql;
        this.driverSearchPatterns = driverSearchPatterns;
    }

    public Collection<String> getDriverSearchPatterns() {
        return driverSearchPatterns;
    }

    public String getDbName() {
        return dbName;
    }

    public String getCurrentSchemaSql() {
        return currentSchemaSql;
    }
}
