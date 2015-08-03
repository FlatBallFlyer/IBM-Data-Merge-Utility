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
package com.ibm.util.merge.directive;

import java.util.ArrayList;


/**
 *
 */
public class Directives {
    // Directive Types
    public static final int TYPE_REQUIRE 				= 0;
    public static final int TYPE_REPLACE_VALUE 			= 1;
    public static final int TYPE_TAG_INSERT	 			= 2;
    public static final int	TYPE_SQL_INSERT				= 10;
    public static final int TYPE_SQL_REPLACE_ROW 		= 11;
    public static final int TYPE_SQL_REPLACE_COL 		= 12;
    public static final int	TYPE_CSV_INSERT				= 21;
    public static final int TYPE_CSV_REPLACE_ROW 		= 22;
    public static final int TYPE_CSV_REPLACE_COL 		= 23;
    public static final int	TYPE_HTML_INSERT			= 31;
    public static final int TYPE_HTML_REPLACE_ROW 		= 32;
    public static final int TYPE_HTML_REPLACE_COL 		= 33;
    public static final int TYPE_HTML_REPLACE_MARKUP 	= 34;
    // Planned directive Types (Not Implemented)
    public static final int	TYPE_JSON_INSERT			= 41;
    public static final int TYPE_JSON_REPLACE_ROW 		= 42;
    public static final int TYPE_JSON_REPLACE_COL 		= 43;
    public static final int	TYPE_XML_INSERT				= 51;
    public static final int TYPE_XML_REPLACE_ROW 		= 52;
    public static final int TYPE_XML_REPLACE_COL 		= 53;
    public static final int	TYPE_MONGO_INSERT			= 61;
    public static final int TYPE_MONGO_REPLACE_ROW 		= 62;
    public static final int TYPE_MONGO_REPLACE_COL 		= 63;
    
    // List of directive names
    public final ArrayList<AbstractDirective> directives  = new ArrayList<AbstractDirective>();
    
    public Directives() {
    	directives.add(new Require());
    	directives.add(new ReplaceValue());
    	directives.add(new InsertSubsTag());
    	directives.add(new InsertSubsSql());
    	directives.add(new ReplaceRowSql());
    	directives.add(new ReplaceColSql());
    	directives.add(new InsertSubsCsv());
    	directives.add(new ReplaceRowCsv());
    	directives.add(new ReplaceColCsv());
//    	directives.add(new InsertSubsHtml());
//    	directives.add(new ReplaceRowHtml());
//    	directives.add(new ReplaceColHtml());
//    	directives.add(new ReplaceMarkupHtml());
    }
    
    public ArrayList<AbstractDirective>getDirectives() {
    	return directives;
    }
}
