/**
 * 
 */
package com.ibm.tk;



/**
 * @author flatballflyer
 *
 */
public class tkException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode="-Code Not Set-";
	
	public tkException(String message, String errorCode){
        super(message);
        this.errorCode=errorCode;
    }

    public String getErrorCode(){
        return this.errorCode;
    }
}
