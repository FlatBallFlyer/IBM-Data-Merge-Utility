/*
 * Copyright (c) 2015 IBM 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package com.ibm.tk;

import java.sql.Connection;
import java.sql.SQLException;

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

	private static DataSource templateDB;
	private static DataSource dataDB;
	
    public static Connection getTemplateConnection() throws tkSqlException, tkException {
    	if (templateDB == null) {
        	try {
            	Context initContext = new InitialContext();
            	templateDB = (DataSource) initContext.lookup("java:/comp/env/jdbc/TemplateDB");
        	} catch (NamingException e) {
        		throw new tkException(e.getMessage(), "JNDI Connection Error");
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