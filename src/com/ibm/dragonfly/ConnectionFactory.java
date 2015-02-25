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

package com.ibm.dragonfly;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
	private static final HashMap<String,DataSource> dataDbHash = new HashMap<String,DataSource>();
	
    /**********************************************************************************
	 * <p>Template Connection Factory</p>
	 *
	 * @throws tkException - JNDI Connection Error
	 * @throws tkSqlException - Tempalte Table validation error
	 * @return The new template database connection 
	 */
    public static Connection getTemplateConnection() throws tkSqlException, tkException {
    	if (templateDB == null) {
        	try { // Get the naming context
            	Context initContext = new InitialContext();
            	templateDB = (DataSource) initContext.lookup("java:/comp/env/jdbc/dragonflyDB");
        	} catch (NamingException e) {
        		throw new tkException(e.getMessage(), "JNDI Connection Error connection to dragonflyDB");
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
			throw new tkSqlException("Error Connecting to Template Database", "Database Connection Error", "Connect to Database dragonflyDB", e.getMessage());
		}
    }

    /**********************************************************************************
	 * <p>Data Source connection factory</p>
	 *
	 * @param  JNDI Data Source name
	 * @throws tkException - JNDI Naming Errors
	 * @throws tkSqlException - Database Connection Errors
	 * @return The new Data Source connection 
	 */
    public static Connection getDataConnection(String jndiSource) throws tkSqlException, tkException {
    	// If the data source is not in the cache, create it and add it to the cache
    	if ( !dataDbHash.containsKey(jndiSource) ) { 
        	try {
            	Context initContext = new InitialContext();
            	DataSource newSource = (DataSource) initContext.lookup("java:/comp/env/jdbc/" + jndiSource);
            	dataDbHash.put(jndiSource, newSource);
        	} catch (NamingException e) {
        		throw new tkException(e.getMessage(), "JNDI Connection Error " + jndiSource);
        	}
    	}
    	
    	// Cet a connection from the JNDI pool.
		try {
			return dataDbHash.get(jndiSource).getConnection();
		} catch (SQLException e) {
			throw new tkSqlException("Error Connecting to Data Database " + jndiSource, "Database Connection Error", "Connect to Database " + jndiSource, e.getMessage());
		}
    }

}