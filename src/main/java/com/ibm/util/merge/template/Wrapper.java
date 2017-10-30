package com.ibm.util.merge.template;

public class Wrapper {
	public String front;
	public String back;
	
	public Wrapper() {
		this.front = "{";
		this.back = "}";
	}
	
	public Wrapper(String front, String back) {
		this.front = front;
		this.back = back;
	}

	/**
	 * Wrapp.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String wrapp(String value) {
		return this.front + value + this.back;
	}

	
}