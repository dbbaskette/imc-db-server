package com.insurancemegacorp.dbserver.config;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@Component
public class DatabaseInstanceManager {
    
    private final Map<String, DataSource> dataSourceMap;

    public DatabaseInstanceManager(Map<String, DataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }

    public DataSource getDataSource(String instanceName) {
        DataSource dataSource = dataSourceMap.get(instanceName);
        if (dataSource == null) {
            throw new IllegalArgumentException("Database instance not found: " + instanceName);
        }
        return dataSource;
    }

    public Set<String> getAvailableInstances() {
        return dataSourceMap.keySet();
    }

    public boolean isInstanceAvailable(String instanceName) {
        return dataSourceMap.containsKey(instanceName);
    }

    public boolean testConnection(String instanceName) {
        try {
            DataSource dataSource = getDataSource(instanceName);
            try (Connection connection = dataSource.getConnection()) {
                return connection.isValid(5);
            }
        } catch (SQLException e) {
            return false;
        }
    }
}