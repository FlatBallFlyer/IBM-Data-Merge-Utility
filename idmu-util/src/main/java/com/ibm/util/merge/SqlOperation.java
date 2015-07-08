package com.ibm.util.merge;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public interface SqlOperation<T> {
    T execute(Connection connection) throws SQLException;
}
