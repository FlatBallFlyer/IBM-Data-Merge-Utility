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
import java.util.HashMap;

import com.ibm.tk.Template;

/**
 * <p>This implements a cacheing factory for Templates</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Directive
 * @see     Merge
 */
final public class TemplateFactory {

	private static final HashMap<String,Template> templateCache = new HashMap<String,Template>();
	
    /**********************************************************************************
	 * <p>Template constructor</p>
	 *
	 * @param  Collection Name
	 * @param  Column Value
	 * @param  Template Name
	 * @throws tkException - Invalid Directive Type from Constructor
	 * @throws tkSqlException - Template Constructor Errors
	 * @return The new Template object
	 */
    public static Template getTemplate(String collection, String column, String name) throws tkSqlException, tkException {
    	String fullName = collection + ":" + column + ":" + name;
    	if ( !templateCache.containsKey(fullName) ) { 
    		Template newTemplate = new Template(collection, column, name);
    		templateCache.put(fullName, newTemplate);
    	}
    	return new Template(templateCache.get(fullName));
    }
    
    /**********************************************************************************
	 * <p>Reset the cache</p>
	 *
	 */
    public static void reset() {
    	templateCache.clear();
    }
    
}