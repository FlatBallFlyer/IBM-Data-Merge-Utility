/*
 * 
 * Copyright 2015-2017 IBM
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
	 * @param part The part name
	 * @param index The index of the part
	 * @param list List Indicator for part
	 */
	public PathPart(String part, int index, boolean list) {
		this.part = part;
		this.index = index;
		this.isList = list;
	};
	
	/**
	 * Instantiates a path part
	 * 
	 * @param raw The string to parse
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
