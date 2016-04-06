package org.jrx.atlassian.mybatis.api;

import org.apache.ibatis.mapping.DatabaseIdProvider;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by dzharikhin on 10.12.2015.
 */
public interface DbInfoProvider extends DatabaseIdProvider {
    String getSchema(DataSource dataSource) throws SQLException;
}
