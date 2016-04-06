package org.jrx.atlassian.mybatis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dzharikhin on 10.12.2015.
 */
public class JdbcUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcUtil.class);

    public static String executeStringResultQuery(Connection connection, String query, Object... params) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
                LOG.warn("ResultSet for query={} is empty", query);
                return null;
            }

        } catch (SQLException e) {
            LOG.error("Error on executing query={}", query, e);
            throw e;
        }
    }
}
