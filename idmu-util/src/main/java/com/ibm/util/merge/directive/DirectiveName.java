package com.ibm.util.merge.directive;

public class DirectiveName {
	private final int type;
	private final String name;
	public DirectiveName(int type, String name) {
		this.name = name;
		this.type = type;
	}
	public int getType() {
		return type;
	}
	public String getName() {
		return name;
	}
}
