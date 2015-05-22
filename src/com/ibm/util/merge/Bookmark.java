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

package com.ibm.util.merge;

import java.util.regex.*;

/**
 * A location within a template where sub-templates will be inserted. 
 *
 * @author  Mike Storey
 */
public class Bookmark implements Cloneable {
	private String element;
	private String name;
	private int start;
	private int size;

	/********************************************************************************
	 * <p>Bookmark constructor</p>
	 *
	 * @param  contents Bookmark String in the form &lt;tkBookmark name="THENAME"/&gt;
	 * @param  initialStart The location within the template of this bookmark
	 * @throws MergeException Invalid Bookmark
	 */
	public Bookmark (String contents, int initialStart) throws MergeException {
		Pattern p = Pattern.compile("=\"(.*)\"");
		Matcher m = p.matcher(contents);
		if (m.find()) {
			element = contents;
			name = m.group(1);
			start = initialStart;
			size = element.length();
		} else {
			throw new MergeException("Invalid Bookmark", "Bookmark: " + contents);
		}
	}
	
	/********************************************************************************
	 * <p>Bookmark Clone constructor</p>
	 *
	 * @param  from Bookmark to clone
	 * @throws CloneNotSupportedException 
	 */
	public Bookmark clone () throws CloneNotSupportedException {
		return (Bookmark) super.clone();
	}
	
	/**********************************************************************************
	 * <p>Offset method to call when the bookmark is shifted</p>
	 *
	 * @param  amount The number of bytes (positive or negative) to offset
	 */
	public void offest (int amount) {
		this.start += amount;
	}

	// - SIMPLE GETTERS BELOW HERE -
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return this.start;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return this.size;
	}

}
