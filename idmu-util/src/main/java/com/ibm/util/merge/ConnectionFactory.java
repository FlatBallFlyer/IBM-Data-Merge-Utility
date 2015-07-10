/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ibm.util.merge;

import com.ibm.idmu.api.SqlOperation;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.idmu.api.PoolManager;

import java.sql.SQLException;
import java.util.Properties;

/**
 * A connection factory for JNDI Data Sources that cache's Database Connections for 
 * the ProviderSql data provider throughout the course of a Merge.
 * Each SqlOperation runs with it's own connection which is closed afterwards
 *
 * @author  Mike Storey
 */
public final class ConnectionFactory {

	private final PoolManager poolManager;

	public ConnectionFactory() {
		this(new ConnectionPoolManager());
	}

	public ConnectionFactory(PoolManager poolManager) {
		if(!poolManager.isPoolName("TIA")){
			System.out.println("Adding dev datasources");
			try {
				poolManager.loadDriverClass("com.mysql.jdbc.Driver");
				poolManager.createPool("tiaDB", "jdbc:mysql://localhost:3306/TIA", "root", "kanaal2");
				poolManager.createPool("testgenDB", "jdbc:mysql://localhost:3306/testgen", "root", "kanaal2");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		this.poolManager = poolManager;
	}

	public <T>T runSqlOperation(String poolName, SqlOperation<T> operation){
		try {
			return poolManager.runWithPool(poolName, operation);
		} catch (SQLException e) {
			throw new SqlOperationException(poolName, operation, e);
		}

	}

	public void closePool(String poolName) throws SQLException {
		poolManager.closePool(poolName);
	}

	public boolean isPoolName(String poolName) {
		return poolManager.isPoolName(poolName);
	}

	public void createPool(String poolName, String jdbcConnectionUrl) throws Exception {
		poolManager.createPool(poolName, jdbcConnectionUrl);
	}

	public void createPool(String poolName, String jdbcConnectionUrl, String username, String password) throws Exception {
		poolManager.createPool(poolName, jdbcConnectionUrl, username, password);
	}

	public void createPool(String poolName, String jdbcConnectionUrl, Properties properties) throws Exception {
		poolManager.createPool(poolName, jdbcConnectionUrl, properties);
	}

	public void loadDriverClass(String driverClassPath) throws ClassNotFoundException {
		poolManager.loadDriverClass(driverClassPath);
	}

	public void reset() {
		poolManager.reset();
	}

	public static class SqlOperationException extends RuntimeException{
		private String poolName;
		private SqlOperation operation;

		public SqlOperationException(String poolName, SqlOperation operation, Exception e) {
			super("Error executing SqlOperation with poolname " + poolName, e);
			this.poolName = poolName;
			this.operation = operation;
		}

		public String getPoolName() {
			return poolName;
		}

		public SqlOperation getOperation() {
			return operation;
		}
	}
}