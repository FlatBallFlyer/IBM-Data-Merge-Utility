package com.ibm.idmu.api;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public interface PoolManager {
    void closePool(String poolName) throws SQLException;
    boolean isPoolName(String poolName);
    void createPool(String poolName, String jdbcConnectionUrl) throws Exception;
    void createPool(String poolName, String jdbcConnectionUrl, String username, String password) throws Exception;
    void createPool(String poolName, String jdbcConnectionUrl, Properties properties) throws Exception;
    <T> T runWithPool(String poolName, SqlOperation<T> sqlOperation) throws SQLException;
    void loadDriverClass(String driverClassPath) throws ClassNotFoundException;
    Map<String, Object> statistics(String poolName) throws SQLException;
    void reset();
}
