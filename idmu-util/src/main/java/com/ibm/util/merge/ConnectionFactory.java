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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A connection factory for JNDI Data Sources that cache's Database Connections for 
 * the ProviderSql data provider throughout the course of a Merge.
 * Each SqlOperation runs with it's own connection which is closed afterwards
 *
 * @author  Mike Storey
 */
public final class ConnectionFactory {

	private final String jndiPrefix;

	public ConnectionFactory() {
		jndiPrefix = "java:/comp/env/jdbc/";
	}

	public ConnectionFactory(String jndiPrefix) {
		this.jndiPrefix = jndiPrefix;
	}

	public <T>T runSqlOperation(String jndiSource, SqlOperation<T> operation){
		Connection con = null;
		try {
			DataSource dataSource = lookupDataSource(jndiSource);
			con = dataSource.getConnection();
			T out = operation.execute(con);
			return out;
		} catch (NamingException e) {
			throw new DataSourceLookupException(jndiSource, e);
		} catch (SQLException e) {
			throw new SqlOperationException(jndiSource, operation, e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	protected DataSource lookupDataSource(String jndiSourceName) throws NamingException {
		Context initContext = new InitialContext();
		return (DataSource) initContext.lookup(jndiPrefix + jndiSourceName);
	}

	public static class DataSourceLookupException extends RuntimeException{
		private String jndiSource;

		public DataSourceLookupException(String jndiSource, NamingException e) {
			super("Error looking up datasource " + jndiSource, e);
			this.jndiSource = jndiSource;
		}

		public String getJndiSource() {
			return jndiSource;
		}
	}

	public static class SqlOperationException extends RuntimeException{
		private String jndiSource;
		private SqlOperation operation;

		public SqlOperationException(String jndiSource, SqlOperation operation, SQLException e) {
			super("Error executing SqlOperation with datasource " + jndiSource, e);
			this.jndiSource = jndiSource;
			this.operation = operation;
		}

		public String getJndiSource() {
			return jndiSource;
		}

		public SqlOperation getOperation() {
			return operation;
		}
	}
}