package org.jrx.atlassian.mybatis.api;

/**
 * Created by dzharikhin on 20.11.2015.
 */
public class DataSourceReadyEvent {

    private final long bundleId;

    public DataSourceReadyEvent(long bundleId) {
        this.bundleId = bundleId;
    }

    public long getKey() {
        return bundleId;
    }
}
