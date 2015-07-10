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

    PoolingDriver getPoolingDriver() throws SQLException {
        return (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
    }
    @Override
    public void create() throws SQLException {
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
        if(username != null){
            connectionFactory = new DriverManagerConnectionFactory(jdbcConnectionUrl, username, password == null ? "" : password);
        }else{
            connectionFactory = new DriverManagerConnectionFactory(jdbcConnectionUrl, connectionProperties);
        }
    }

    @Override
    public void destroy() throws SQLException {
        destroyed = true;
        poolingDriver.closePool(poolName);
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
    public <T> T runWithPool(SqlOperation<T> sqlOperation) throws SQLException {
        ensureReady();
        Connection conn = null;
        try {
            conn = acquireConnection();
            T out = sqlOperation.execute(conn);
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
    public Connection acquireConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:apache:commons:dbcp:" + poolName);
    }

    @Override
    public Map<String, Object> statistics() throws SQLException {
        HashMap<String, Object> out = new HashMap<>();
        out.put("name", poolName);
        out.put("jdbcUrl", jdbcConnectionUrl);

        out.put("active", connectionPool.getNumActive());
        out.put("idle", connectionPool.getNumIdle());
        return out;
    }

    @Override
    public void close() throws IOException {
        try {
            destroy();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
