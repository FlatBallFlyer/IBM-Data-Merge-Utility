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

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A connection factory for JNDI Data Sources that cache's Database Connections for 
 * the ProviderSql data provider throughout the course of a Merge. The same database
 * connection is shared by all sub-template processing that takes place under the
 * primary merge process. Database connections are cached based on the Merge GUID.
 *
 * @author  Mike Storey
 */
public final class ConnectionFactory {
	private static final Logger log = Logger.getLogger( ConnectionFactory.class.getName() );
	private static final String DBROOT = "java:/comp/env/jdbc/";
	private final ConcurrentHashMap<String,Connection> dataDbHash = new ConcurrentHashMap<>();
	
    /**********************************************************************************
	 * Data Source connection factory, creates and cache's connections that are valid
	 * throughout the life of a merge. Connections are released by calling releaseConnection
	 *
	 * @param  jndiSource JNDI Data Source name
	 * @param guid - a Guid associed with a template merge process
	 * @throws MergeException JNDI Naming Errors
	 * @throws MergeException Database Connection Errors
	 * @return Connection The new Data Source connection 
	 */
    public Connection getDataConnection(String jndiSource, String guid) throws MergeException {
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
	 * <p>Release a connection for a guid</p>
	 *
	 * @param  guid Guid of template to release files for.
	 */
    public void releaseConnection(String guid) {
    	for (Map.Entry<String, Connection> entry : dataDbHash.entrySet()) {
    		if (entry.getKey().endsWith(guid)) {
				try {
					entry.getValue().close();
				} catch (SQLException e) {
					log.error("Error Colsing Connection: ", e);
				} finally {
					dataDbHash.remove(entry);
				}
    		}
		}    	
    }
    
    /**********************************************************************************
	 * Get the size of the DB Connection Hash
	 */
    public int size() {
    	return dataDbHash.size();
    }
}