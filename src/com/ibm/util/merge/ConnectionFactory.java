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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;



/**
 * A connection factory for JNDI Data Sources Supports TemplateDB and DataDB connections.
 *
 * @author  Mike Storey
 */
public final class ConnectionFactory {
	private static final Logger log = Logger.getLogger( ConnectionFactory.class.getName() );
	private static final String DBROOT = "java:/comp/env/jdbc/";
	private static final String DBNAME = "dragonflyDB";
	private static DataSource templateDB;
	private static final ConcurrentHashMap<String,Connection> dataDbHash = new ConcurrentHashMap<String,Connection>();
	
    /**********************************************************************************
	 * <p>Template Connection Factory</p>
	 *
	 * @throws MergeException JNDI Connection Error
	 * @throws DragonFlySqlException Tempalte Table validation error
	 * @return The new template database connection 
	 */
    public static Connection getTemplateConnection() throws MergeException {
    	if (templateDB == null) {
        	try { // Get the naming context
            	Context initContext = new InitialContext();
            	templateDB = (DataSource) initContext.lookup(DBROOT + DBNAME);
        	} catch (NamingException e) {
        		throw new MergeException(e.getMessage(), "JNDI Connection Error connection to " + DBNAME );
        	}
        	
        	try { // Validate Template DB
    			Connection con = templateDB.getConnection();
    			DatabaseMetaData dbmeta = con.getMetaData();
    			ResultSet rs = dbmeta.getTables(null, null, "%", null);
    			while (rs.next()) {
    				log.info("Table Found: " + rs.getString(1) + "." + rs.getString(3) );
    			}			
    			    			
        	} catch (SQLException e) {
        		throw new MergeException("Error getting table metadata", "Template Datasource Error");
			}
    	}
    	
		try {
			Connection con = templateDB.getConnection();
			return con;
		} catch (SQLException e) {
			throw new MergeException("Error Connecting to Template Database", "Database Connection Error");
		}
    }

    /**********************************************************************************
	 * <p>Data Source connection factory, creates and cache's connections that are valid
	 * throughout the life of a merge. Connections are released by calling close</p>
	 *
	 * @param  jndiSource JNDI Data Source name
	 * @param guid - a Guid associed with a template
	 * @throws MergeException JNDI Naming Errors
	 * @throws DragonFlySqlException Database Connection Errors
	 * @return Connection The new Data Source connection 
	 */
    public static Connection getDataConnection(String jndiSource, String guid) throws MergeException {
    	String key = jndiSource + ":" + guid;
    	// If the data source is not in the cache, create it and add it to the cache
    	if ( !dataDbHash.containsKey(key) ) { 
        	try {
            	Context initContext = new InitialContext();
            	DataSource newSource = (DataSource) initContext.lookup(DBROOT + jndiSource);
            	Connection con = newSource.getConnection();
            	dataDbHash.putIfAbsent(key, con);
        	} catch (NamingException e) {
        		throw new MergeException(e, "JNDI Connection Error ", DBROOT + jndiSource);
        	} catch (SQLException e) {
        		throw new MergeException(e, "Failed to get Connection", DBROOT + jndiSource);
			}
    	}
    	
    	// Return the Connection
		return dataDbHash.get(key);
    }
    
    /**********************************************************************************
	 * <p>Release a set of connections for a guid</p>
	 *
	 * @param  guid Guid of template to release files for.
	 */
    public static void close(String guid) {
    	for (Map.Entry<String, Connection> entry : dataDbHash.entrySet()) {
    		if (entry.getKey().endsWith(guid)) {
				try {
					entry.getValue().close();
				} catch (SQLException e) {
					log.error("Error Colsing Connection: ", e);
				}
    			dataDbHash.remove(entry);
    		}
		}    	
    }
    
}