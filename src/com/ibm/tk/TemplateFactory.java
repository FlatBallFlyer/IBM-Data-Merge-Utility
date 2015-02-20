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