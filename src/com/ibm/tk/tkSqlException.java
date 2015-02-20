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

/**
 * @author Mike Storey
 * Simple custom Exception Class for wrapping SQL errors
 *
 */
public class tkSqlException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode="-Code Not Set-";
	private String queryString = "";
	private String sqlError = "";
	
	public tkSqlException(String message, String code, String query, String sqlError ){
        super(message);
        this.errorCode = code;
        this.queryString = query;
        this.sqlError = sqlError;
    }

    public String getErrorCode(){
        return this.errorCode;
    }
	public String getQueryString() {
		return queryString;
	}
	public String getSqlError() {
		return sqlError;
	}

}
