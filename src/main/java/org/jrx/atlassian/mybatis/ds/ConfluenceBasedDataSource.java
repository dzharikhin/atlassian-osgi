package org.jrx.atlassian.mybatis.ds;

import com.atlassian.hibernate.PluginHibernateSessionFactory;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Created by dzharikhin on 07.12.2015.
 */
public class ConfluenceBasedDataSource implements DataSource {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConfluenceBasedDataSource.class);

    private final PluginHibernateSessionFactory sessionFactory;

    public ConfluenceBasedDataSource(PluginHibernateSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Session session = sessionFactory.getSession();
        try {
            return new NonCloseableConnection(session.connection());
        } catch (HibernateException e) {
            LOG.error("Exception on getting connection", e);
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new IllegalStateException("Not allowed to get connection for non-default credentials");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {}

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
