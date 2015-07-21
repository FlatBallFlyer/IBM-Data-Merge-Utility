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
    public static final ArrayList<DirectiveName> directiveNames  = new ArrayList<DirectiveName>();
    
    public Directives() {
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_REQUIRE, 			"Require Tags"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_REPLACE_VALUE, 		"Replace Value"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_TAG_INSERT, 		"Insert Subs from Tag"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_SQL_INSERT, 		"Insert Subs from SQL"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_SQL_REPLACE_ROW, 	"Replace Row from SQL"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_SQL_REPLACE_COL, 	"Ceplace Col from SQL"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_CSV_INSERT, 		"Insert Subs from CSV"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_CSV_REPLACE_ROW, 	"Replace Row from CSV"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_CSV_REPLACE_COL, 	"Replace Col from CSV"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_HTML_INSERT, 		"Insert Subs from HTML"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_HTML_REPLACE_ROW, 	"Replace Row from HTML"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_HTML_REPLACE_COL, 	"Replace Col from HTML"));
    	Directives.directiveNames.add(new DirectiveName(Directives.TYPE_HTML_REPLACE_MARKUP, "Replace from HTML Markup"));
    }
    
    public ArrayList<DirectiveName>getDirectiveNames() {
    	return directiveNames;
    }
    
    public class DirectiveName {
    	@SuppressWarnings("unused")
		private int type;
    	@SuppressWarnings("unused")
		private String name;
    	public DirectiveName(int type, String name) {
    		this.name = name;
    		this.type = type;
    	}
    }
}
