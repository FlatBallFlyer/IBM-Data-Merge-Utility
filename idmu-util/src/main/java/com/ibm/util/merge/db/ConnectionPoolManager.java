package com.ibm.util.merge.db;

import com.ibm.idmu.api.DatabaseConnectionProvider;
import com.ibm.idmu.api.PoolManager;
import com.ibm.idmu.api.SqlOperation;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ConnectionPoolManager implements PoolManager {

    private final Map<String, DatabaseConnectionProvider> connectionProviders = new ConcurrentHashMap<>();
    private String poolingDriverClasspath;

    public ConnectionPoolManager() {
        poolingDriverClasspath = "org.apache.commons.dbcp2.PoolingDriver";
        try {
            loadPoolingDriverImplementation();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("commons-dbcp2 not found on classpath", e);
        }
    }

    private void loadPoolingDriverImplementation() throws ClassNotFoundException {
        loadDriverClass(poolingDriverClasspath);
    }

    @Override
    public final void closePool(String poolName) throws SQLException {
        if(!isPoolName(poolName)) throw new IllegalArgumentException("There is no pool named " + poolName);
        DatabaseConnectionProvider p = connectionProviders.remove(poolName);
        p.destroy();
    }

    @Override
    public boolean isPoolName(String poolName) {
        return connectionProviders.containsKey(poolName);
    }


    @Override
    public final void createPool(String poolName, String jdbcConnectionUrl) throws Exception {
        if(isPoolName(poolName)) throw new IllegalArgumentException("poolName " + poolName + " already exists");
        JdbcDatabaseConnectionProvider p1 = new JdbcDatabaseConnectionProvider(poolName, jdbcConnectionUrl);
        p1.create();
        connectionProviders.put(poolName, p1);
    }
    @Override
    public final void createPool(String poolName, String jdbcConnectionUrl, String username, String password) throws Exception {
        if(isPoolName(poolName)) throw new IllegalArgumentException("poolName " + poolName + " already exists");
        JdbcDatabaseConnectionProvider p1 = new JdbcDatabaseConnectionProvider(poolName, jdbcConnectionUrl, username, password);
        p1.create();
        connectionProviders.put(poolName, p1);
    }
    @Override
    public final void createPool(String poolName, String jdbcConnectionUrl, Properties properties) throws Exception {
        if(isPoolName(poolName)) throw new IllegalArgumentException("poolName " + poolName + " already exists");
        JdbcDatabaseConnectionProvider p1 = new JdbcDatabaseConnectionProvider(poolName, jdbcConnectionUrl, properties);
        p1.create();
        connectionProviders.put(poolName, p1);
    }

    @Override
    public final <T> T runWithPool(String poolName, SqlOperation<T> sqlOperation) throws SQLException {
        DatabaseConnectionProvider provider = connectionProviders.get(poolName);
        if(provider == null) throw new IllegalArgumentException("Unknown poolName: "+ poolName);
        return provider.runWithPool(sqlOperation);
    }

    @Override
    public final void loadDriverClass(String driverClassPath) throws ClassNotFoundException {
        Class<?> driverClass = Class.forName(driverClassPath);
        System.out.println("Loaded class for " + driverClassPath + ": " + driverClass.getName());
    }

    @Override
    public Map<String, Object> statistics(String poolName) throws SQLException {
        return connectionProviders.get(poolName).statistics();
    }

    @Override
    public final void reset(){
        for (String poolName : connectionProviders.keySet()) {
            try {
                closePool(poolName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connectionProviders.clear();
    }
}
