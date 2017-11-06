package com.ibm.util.merge.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single member of a data path
 * 
 * @author Mike Storey
 *
 */
public class PathPart {
	public String part = "";
	public int index = 0;
	public boolean isList = false;
	private static final Pattern PART_PATTERN = Pattern.compile("(\\[(\\d*)\\])");
	
	/**
	 * Instantiates a path part
	 * 
	 * @param part
	 * @param index
	 * @param list
	 */
	public PathPart(String part, int index, boolean list) {
		this.part = part;
		this.index = index;
		this.isList = list;
	};
	
	/**
	 * Instantiates a path part
	 * 
	 * @param raw
	 */
	public PathPart(String raw) {
        Matcher m = PART_PATTERN.matcher(raw);
        if (m.matches()) {
        	part = m.group(1);
        	index = (m.group(2).isEmpty() ? 0 : Integer.valueOf(m.group(2)));
        	isList = true;
        } else {
            part = raw;
            index = 0;
            isList = false;
        }
	}
	
	/**
	 * @return String value of path part.
	 */
	public String asString() {
		String asString;
		if (isList) {
			asString = "[" + Integer.toString(index) + "]";
		} else {
			asString = this.part;
		}
		return asString;
	}
}
