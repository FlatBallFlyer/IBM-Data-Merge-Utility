package com.ibm.idmu.api;

import java.sql.Connection;
import java.util.Map;

/**
 *
 */
public interface DatabaseConnectionProvider {
    void create() throws DatabaseConnectionProviderException;
    void destroy() throws DatabaseConnectionProviderException;
    DatabaseConnectionProvider asNew();
    <T> T runWithPool(SqlOperation<T> sqlOperation) throws DatabaseConnectionProviderException;
    Connection acquireConnection() throws DatabaseConnectionProviderException;
    Map<String, Object> statistics() throws DatabaseConnectionProviderException;

    public static class DatabaseConnectionProviderException extends RuntimeException{
        /**
		 * 
		 */
		private static final long serialVersionUID = -1889891368175097509L;

		public DatabaseConnectionProviderException(String message) {
            super(message);
        }

        public DatabaseConnectionProviderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
