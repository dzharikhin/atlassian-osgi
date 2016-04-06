package org.jrx.atlassian.mybatis.api;

import org.jrx.atlassian.mybatis.ds.ConfluenceBasedDataSource;
import org.osgi.framework.BundleContext;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by dzharikhin on 20.11.2015.
 */
public interface DataSourceFactory {
     /**
      * method to just get DataSource - with no migrations behind it
      * @return Delegate DS implementation based on hibernate session connection {@link ConfluenceBasedDataSource}
      * @throws Exception if something failed
      */
     DataSource getDataSource() throws Exception;
     /**
      * method to get DataSource only if migration is executed successfully
      * @param bundleContext context of bundle-requester
      * @param versionTableName name of table where flyway will store its migration related info
      * @param properties properties for additional Flyway configuration {@link http://flywaydb.org/documentation/maven/migrate.html}
      * @return Delegate DS implementation based on hibernate session connection {@link ConfluenceBasedDataSource}
      * @throws Exception if something failed
      */
     DataSource getDataSource(BundleContext bundleContext, String versionTableName, Properties properties) throws Exception;
}
