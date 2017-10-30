package com.ibm.util.merge.exception;

// Not Found exception
public class Merge404 extends MergeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Merge404(String error) {
		super(error);
	}

	@Override
	public String getErrorString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

}
