package com.ibm.util.merge.db;

import com.ibm.idmu.api.DatabaseConnectionProvider;
import com.ibm.idmu.api.SqlOperation;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
class JdbcDatabaseConnectionProvider implements Closeable, DatabaseConnectionProvider {
    private String poolName;
    private String jdbcConnectionUrl;
    private Properties connectionProperties;
    private String username;
    private String password;
    PoolingDriver poolingDriver;
    ConnectionFactory connectionFactory;
    PoolableConnectionFactory poolableConnectionFactory;
    ObjectPool<PoolableConnection> connectionPool;
    private boolean creating = false;
    private boolean created = false;
    private boolean destroyed = false;

    public JdbcDatabaseConnectionProvider(String poolName, String jdbcConnectionUrl) {
        this.poolName = poolName;
        this.jdbcConnectionUrl = jdbcConnectionUrl;
    }

    public JdbcDatabaseConnectionProvider(String poolName, String jdbcConnectionUrl, String username, String password) {
        this.poolName = poolName;
        this.jdbcConnectionUrl = jdbcConnectionUrl;
        this.username = username;
        this.password = password;
    }

    public JdbcDatabaseConnectionProvider(String poolName, String jdbcConnectionUrl, Properties connectionProperties) {
        this.poolName = poolName;
        this.jdbcConnectionUrl = jdbcConnectionUrl;
        this.connectionProperties = connectionProperties;
    }

    PoolingDriver getPoolingDriver() throws DatabaseConnectionProviderException {
        String url = "jdbc:apache:commons:dbcp:";
        try {
            return (PoolingDriver) DriverManager.getDriver(url);
        } catch (SQLException e) {
            throw new DatabaseConnectionProviderException("Could not load pooling driver " + url, e);
        }
    }

    @Override
    public void create() throws DatabaseConnectionProviderException {
        creating = true;
        PoolingDriver driver = getPoolingDriver();
        if (driver == null) throw new IllegalArgumentException("driver cannot be null");
        this.poolingDriver = driver;
        constructConnectionFactory();
        constructPoolableConnectionFactory();
        constructPool();
        registerPool(driver);
        created = true;
    }

    protected void registerPool(PoolingDriver driver) {
        driver.registerPool(poolName, connectionPool);
    }

    protected void constructPool() {
        connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
    }

    protected void constructPoolableConnectionFactory() {
        poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
    }

    protected void constructConnectionFactory() {
        if (username != null) {
            connectionFactory = new DriverManagerConnectionFactory(jdbcConnectionUrl, username, password == null ? "" : password);
        } else {
            connectionFactory = new DriverManagerConnectionFactory(jdbcConnectionUrl, connectionProperties);
        }
    }

    @Override
    public void destroy() throws DatabaseConnectionProviderException {
        destroyed = true;
        try {
            poolingDriver.closePool(poolName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        poolingDriver = null;
        connectionFactory = null;
        poolableConnectionFactory = null;
        connectionPool = null;
    }

    @Override
    public DatabaseConnectionProvider asNew() {
        return new JdbcDatabaseConnectionProvider(poolName, jdbcConnectionUrl);
    }

    @Override
    public <T> T runWithPool(SqlOperation<T> sqlOperation) throws DatabaseConnectionProviderException {
        ensureReady();
        Connection conn = null;
        try {
            conn = acquireConnection();
            T out = null;
            try {
                out = sqlOperation.execute(conn);
            } catch (Exception e) {
                throw new SqlOperationExecutionException(sqlOperation, jdbcConnectionUrl, e);
            }
            return out;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void ensureReady() {
        if (creating && !created) {
            throw new IllegalStateException("still creating or (most probably) failed to create");
        }
        if (!created) {
            throw new IllegalStateException("not yet created, invoke create()");
        }
        if (destroyed) {
            throw new IllegalStateException("destroyed, not reusable. Look at freshInstance()");
        }
    }

    @Override
    public Connection acquireConnection() throws DatabaseConnectionProviderException {
        String poolUrl = "jdbc:apache:commons:dbcp:" + poolName;
        try {
            return DriverManager.getConnection(poolUrl);
        } catch (Exception e) {
            throw new DatabaseConnectionProviderException("Error acquiring connection for pool " + poolName + " using " + poolUrl, e);
        }
    }

    @Override
    public Map<String, Object> statistics() throws DatabaseConnectionProviderException {
        HashMap<String, Object> out = new HashMap<>();
        out.put("name", poolName);
        out.put("jdbcUrl", jdbcConnectionUrl);
        out.put("active", connectionPool.getNumActive());
        out.put("idle", connectionPool.getNumIdle());
        return out;
    }

    @Override
    public void close() throws IOException {
        destroy();
    }

    public class SqlOperationExecutionException extends DatabaseConnectionProviderException{
		private static final long serialVersionUID = 1L;
		public <T> SqlOperationExecutionException(SqlOperation<T> sqlOperation, String jdbcConnectionUrl, Exception e) {
            super("Error during execution of sql operation against " + jdbcConnectionUrl + " : " + sqlOperation, e);

        }
    }
}
