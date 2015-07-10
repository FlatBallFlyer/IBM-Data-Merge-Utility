package com.ibm.idmu.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 */
public interface DatabaseConnectionProvider {
    void create() throws SQLException;
    void destroy() throws SQLException;
    DatabaseConnectionProvider asNew();
    <T> T runWithPool(SqlOperation<T> sqlOperation) throws SQLException;
    Connection acquireConnection() throws SQLException;
    Map<String, Object> statistics() throws SQLException;
}
