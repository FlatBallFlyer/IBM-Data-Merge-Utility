package com.ibm.idmu.api;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An operation that requires access to a java.sql.Connection
 * Connection lifecycle management happens outside of the operation, so the Connection instance should not be closed
 * The generic T parameter identifies the result of the operation (use Void for null)
 */
public interface SqlOperation<T> {
    T execute(Connection connection) throws SQLException;
}
