/*
 * Copyright 2015 IBM
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

package com.ibm.tk;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;



/**
 * <p>This implements a connection factory for the TemplateDB and DataDB JNDI Data Sources</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Directive
 * @see     Merge
 */
final public class ConnectionFactory {
	private static final Logger log = Logger.getLogger( ConnectionFactory.class.getName() );
	private static DataSource templateDB;
	private static DataSource dataDB;
	
    public static Connection getTemplateConnection() throws tkSqlException, tkException {
    	if (templateDB == null) {
        	try { // Get the naming context
            	Context initContext = new InitialContext();
            	templateDB = (DataSource) initContext.lookup("java:/comp/env/jdbc/TemplateDB");
        	} catch (NamingException e) {
        		throw new tkException(e.getMessage(), "JNDI Connection Error");
        	}
        	
        	try { // Validate Template DB
    			Connection con = templateDB.getConnection();
    			DatabaseMetaData dbmeta = con.getMetaData();
    			ResultSet rs = dbmeta.getTables(null, null, "%", null);
    			while (rs.next()) {
    				log.info("Table Found: " + rs.getString(1) + "." + rs.getString(3) );
    				// todo, validate all template tables are in the db.
    			}			
    			    			
        	} catch (SQLException e) {
        		throw new tkSqlException("Error getting table metadata", "Template Datasource Error", "meta.getTables", e.getMessage());
			}
    	}
    	
		try {
			Connection con = templateDB.getConnection();
			return con;
		} catch (SQLException e) {
			throw new tkSqlException("Error Connecting to Template Database", "Database Connection Error", "Connect to Database", e.getMessage());
		}
    }

    public static Connection getDataConnection() throws tkSqlException, tkException {
    	if (dataDB == null) {
        	try {
            	Context initContext = new InitialContext();
            	dataDB = (DataSource) initContext.lookup("java:/comp/env/jdbc/DataDB");
        	} catch (NamingException e) {
        		throw new tkException(e.getMessage(), "JNDI Connection Error");
        	}
    	}
    	
		try {
			Connection con = dataDB.getConnection();
			return con;
		} catch (SQLException e) {
			throw new tkSqlException("Error Connecting to Data Database", "Database Connection Error", "Connect to Database", e.getMessage());
		}
    }

}