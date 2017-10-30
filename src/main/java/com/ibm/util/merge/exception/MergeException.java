package com.ibm.util.merge.exception;

public abstract class MergeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9055769776571167065L;

	public MergeException(String error) {
		super(error);
	}
	public abstract String getErrorString();
	public abstract int getStatus();
	public abstract String getContentType();
}
