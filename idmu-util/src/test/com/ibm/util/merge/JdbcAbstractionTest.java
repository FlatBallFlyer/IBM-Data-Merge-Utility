package com.ibm.util.merge;

import com.ibm.idmu.api.SqlOperation;
import com.ibm.util.merge.db.ConnectionPoolManager;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class JdbcAbstractionTest {
    private ConnectionPoolManager cpm;

    @Before
    public void setUp() throws Exception {
        //a single instance is managed
        cpm = new ConnectionPoolManager();
    }

    @Test
    public void canCreate() {
        String poolName = "myPoolName";
        String jdbcConnectionUrl = "jdbc:mysql://localhost:3306/TIA";
        String username = "myuser";
        String password = "mypwd";

        //we create the pool
        cpm.createPool(poolName, jdbcConnectionUrl, username, password);

        //we have something we want to run against a certain pool : the operation
        //you would probably create something like AddTemplateSqlOperation which implements SqlOperation
        SqlOperation<String> operation = new SqlOperation<String>() {
            @Override
            public String execute(Connection connection) throws SQLException {
                Statement st = null;
                ResultSet resultSet = null;
                try {
                    st = connection.createStatement();
                    st.executeQuery("SELECT 1");
                    resultSet = st.getResultSet();
                    //this dummy doesn't do anything
                    return "1";
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    //result sets and statements should be closed
                    if (resultSet != null) resultSet.close();
                    if (st != null) st.close();
                }
            }
        };

        //run the operation against the specified pool, which returns the operation result
        String result = cpm.runWithPool(poolName, operation);
        //we only get here if there are no exceptions
        assertEquals("1", result);
    }
}
