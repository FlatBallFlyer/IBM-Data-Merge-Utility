/*
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

package com.ibm.tk.directive;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.tk.tkException;

/**
 * A simple replace From -> To directive 
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Merge
 */
public class ReplaceValue {
	private static final Logger log = Logger.getLogger( ReplaceValue.class.getName() );
	private String fromValue;
	private String toValue;

	/**
	 * <p>Constructor</p>
	 *
	 * @param  From Value
	 * @param  To Value
	 * @return The new Directive object
	 * @throws SQLException - Bad dbRow, or missing fromValue/toValue in result
	 */
	public ReplaceValue (ResultSet dbRow) throws tkException {
		log.finest("Constructing ReplaceValue Directive");
		try {
			this.fromValue 	= dbRow.getString("fromValue");
			this.toValue 	= dbRow.getString("toValue");
		} catch (SQLException e) {
			throw new tkException("Replace Value Error: "+e.getMessage(), "Invalid Directive Data");
		}
	}
	
	/**
	 * <p>Get Hashtable From/To</p>
	 *
	 * @return Hashtable with From->To
	 */
	public void getValues(Map<String,String> replace) {
		log.finest("Adding " + this.fromValue + "->" + this.toValue);
		replace.put(this.fromValue, this.toValue);
	}
}
