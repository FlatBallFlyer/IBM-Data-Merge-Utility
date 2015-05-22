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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.Provider;

/**
 * A merge directive which drive the merge process for a template 
 *
 * @author  Mike Storey
 * @Entity
 * @Table( name = "DIRECTIVE" )
 */
public abstract class Directive implements Cloneable{
	// Directive Table Column Names
	public static final String COL_DIRECTIVE_TYPE 			= "type";
	public static final String COL_DIRECTIVE_DESCRIPTION	= "description";
	public static final String COL_DIRECTIVE_SOFT_FAIL		= "softFail";
	// Unique to Reqiore Directive
	public static final String COL_REQUIRE_TAGS		 		= "from";
	// Unique to Replace Value Directives
	public static final String COL_REPLACE_FROM_VALUE 		= "from";
	public static final String COL_REPLACE_TO_VALUE 		= "to";
	// Unique to Replace Column Directives
	public static final String COL_REPLACE_COLUMN_FROM 		= "from";
	public static final String COL_REPLACE_COLUMN_TO 		= "to";
	
	// Unique to Insert Directives
	public static final String COL_INSERT_FROM_COLLECTION	= "from";
	public static final String COL_INSERT_FROM_COLUMN		= "to";
	public static final String COL_INSERT_NOT_LAST	 		= "not_last";
	public static final String COL_INSERT_ONLY_LAST 		= "only_last";

	// ProfiderIf
	public static final String COL_IF_TAG			 		= "values";
	
	// Unique to HTTP Providers (CSV, HTML)
	public static final String COL_HTTP_URL 				= "source";
	public static final String COL_HTTP_STATIC_DATA 		= "values";
	// Unique to JDBC Providers
	public static final String COL_JDBC_SOURCE 				= "source";
	public static final String COL_JDBC_COLUMNS 			= "values";
	public static final String COL_JDBC_TABLES 				= "location";
	public static final String COL_JDBC_WHERE 				= "condition";
	
	// Directive Types
	public static final int TYPE_REQUIRE 				= 0;
	public static final int TYPE_REPLACE_VALUE 			= 1;
	public static final int TYPE_IF_INSERT	 			= 2;
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
	public static final int	TYPE_JSON_INSERT			= 41;
	public static final int TYPE_JSON_REPLACE_ROW 		= 42;
	public static final int TYPE_JSON_REPLACE_COL 		= 43;
	public static final int	TYPE_XML_INSERT				= 51;
	public static final int TYPE_XML_REPLACE_ROW 		= 52;
	public static final int TYPE_XML_REPLACE_COL 		= 53;
	public static final int	TYPE_MONGO_INSERT			= 61;
	public static final int TYPE_MONGO_REPLACE_ROW 		= 62;
	public static final int TYPE_MONGO_REPLACE_COL 		= 63;
	
	// Attributes
	protected Template template;
	protected Provider provider;
	protected long idDirective;
	protected boolean softFail;
	protected int type;
	protected String description;
	
	/********************************************************************************
	 * Abstract method to "Execute" the directive in the context of a template.
	 *
	 * @param  tempalte - the Target Template for the merge process
	 * @throws MergeException execution errors
	 */
	public abstract void executeDirective() throws MergeException;

	/********************************************************************************
	 * Constructor from Database Row
	 *
	 * @param  dbRow - SQL Resultset Row
	 * @throws MergeException SQL Errors
	 */
	public Directive(ResultSet dbRow, Template newOwner) throws MergeException {
		try {
			this.provider 		= null;
			this.template		= newOwner;
			this.softFail		= dbRow.getBoolean(	Directive.COL_DIRECTIVE_SOFT_FAIL);
			this.type			= dbRow.getInt(		Directive.COL_DIRECTIVE_TYPE);
			this.description 	= dbRow.getString(	Directive.COL_DIRECTIVE_DESCRIPTION);
		} catch (SQLException e) {
			throw new MergeException(e, "Directive Constructor Sql Exception", "");
		}
	}
	
	/********************************************************************************
	 * Cone constructor
	 * @throws CloneNotSupportedException
	 */
	public Directive clone(Template newOwner) throws CloneNotSupportedException {
		Directive newDirective = (Directive) super.clone();
		newDirective.template = newOwner;
		newDirective.provider = (Provider) this.provider.clone(this);
		return newDirective;
	}
	
	/********************************************************************************
	 * Constructor from Database Row
	 *
	 * @param  template Target Template
	 * @return Soft Fail indicator (From Directive or Template)
	 */
	public boolean softFail() {
		return (this.softFail | this.template.softFail()) ? true : false;
	}
	
	/**
	 * @return the Template Fullname + the Directive Description
	 */
	public String getFullName() {
		return template.getFullName() + ":Directive-" + this.description;
	}
	
	/**
	 * @return the Template which this directive is attached to.
	 */
	public Template getTemplate() {
		return this.template;
	}
	
	/**
	 * @return
	 * @Column(name = "softFail)";
	 */
	public boolean isSoftFail() {
		return softFail;
	}

	/**
	 * @return the type
	 * @Column(name = "type)";
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the description
	 * @Column(name = "description)";
	 */
	public String getDescription() {
		return description;
	}
	
}
