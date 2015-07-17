package com.ibm.util.merge.directive;

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
}
