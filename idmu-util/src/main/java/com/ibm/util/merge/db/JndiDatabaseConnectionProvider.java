package com.ibm.util.merge.db;

import com.ibm.idmu.api.DatabaseConnectionProvider;
import com.ibm.idmu.api.SqlOperation;

import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 */
class JndiDatabaseConnectionProvider implements Closeable, DatabaseConnectionProvider {
    private static final Logger log = Logger.getLogger(JndiDatabaseConnectionProvider.class);
    private String poolName;
    private String jndiDataSourceName;
    private DataSource jndiDataSource;
    private boolean creating = false;
    private boolean created = false;
    private boolean destroyed = false;

    public JndiDatabaseConnectionProvider(String poolName, String jndiDataSourceName) {
        this.poolName = poolName;
        this.jndiDataSourceName = jndiDataSourceName;
    }

    @Override
    public void create() throws DatabaseConnectionProviderException {
        creating = true;
    	try { // Get the naming context
        	Context initContext = new InitialContext();
        	this.jndiDataSource = (DataSource) initContext.lookup(this.jndiDataSourceName);
    	} catch (NamingException e) {
    		log.fatal("Naming Exception: " + this.jndiDataSourceName);
    		throw new RuntimeException("Naming Exception: " + this.jndiDataSourceName);
    	}
        created = true;
    }

    @Override
    public void destroy() throws DatabaseConnectionProviderException {
        destroyed = true;
    }

    @Override
    public DatabaseConnectionProvider asNew() {
        return new JndiDatabaseConnectionProvider(this.poolName, this.jndiDataSourceName);
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
                throw new SqlOperationExecutionException(sqlOperation, this.jndiDataSourceName, e);
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
    	Connection con;
    	try {
			con = this.jndiDataSource.getConnection();
		} catch (SQLException e) {
			log.fatal("JNDI Error acquiring connection for " + this.jndiDataSourceName);
			throw new DatabaseConnectionProviderException("Error acquiring connection for pool " + poolName + " using " + this.jndiDataSourceName, e);
		}
    	return con;
    }

    @Override
    public Map<String, Object> statistics() throws DatabaseConnectionProviderException {
        HashMap<String, Object> out = new HashMap<>();
        out.put("name", poolName);
        out.put("jndiName", jndiDataSourceName);
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
