package com.ibm.tk;


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
