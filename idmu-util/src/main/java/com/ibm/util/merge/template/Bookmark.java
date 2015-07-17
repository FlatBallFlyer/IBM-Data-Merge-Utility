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

package com.ibm.util.merge.template;

import com.ibm.util.merge.MergeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A location within a template where sub-templates will be inserted. 
 *
 * @author  Mike Storey
 */
public class Bookmark implements Cloneable {
	private static final Pattern NAME_PATTERN 		= Pattern.compile("name.*?=.*?\"(.*?)\"");
	private static final Pattern COLLECTION_PATTERN = Pattern.compile("collection.*?=.*?\"(.*?)\"");
	private static final Pattern COLUMN_PATTERN 	= Pattern.compile("column.*?=.*?\"(.*?)\"");
	private String element		= "";
	private String name			= "";
	private String collection	= "";
	private String column		= "";
	private int start			= 0;
	private int size			= 0;

	/********************************************************************************
	 * <p>Bookmark constructor</p>
	 *
	 * @param  contents Bookmark String in the form &lt;tkBookmark name="THENAME"/&gt;
	 * @param  initialStart The location within the template of this bookmark
	 * @throws MergeException Invalid Bookmark
	 */
	public Bookmark (String contents, int initialStart) {
		element = contents;
		start = initialStart;
		size = element.length();
		
		Matcher nameMatcher = NAME_PATTERN.matcher(contents);
		if (nameMatcher.find()) {
			name = nameMatcher.group(1);
		} else {
			throw new IllegalArgumentException("Invalid Bookmark contents, could not find name: " + contents);
		}

		Matcher collectionMatcher = COLLECTION_PATTERN.matcher(contents);
		if (collectionMatcher.find()) {
			collection = collectionMatcher.group(1);
		} else {
			throw new IllegalArgumentException("Invalid Bookmark contents, could not find collection: " + contents);
		}

		Matcher columnMatcher = COLUMN_PATTERN.matcher(contents);
		if (columnMatcher.find()) {
			column = columnMatcher.group(1);
		} 
	}
	
	/********************************************************************************
	 * <p>Bookmark Clone constructor</p>
	 *
	 * @param  from Bookmark to clone
	 * @throws CloneNotSupportedException 
	 */
	@Override
	public Bookmark clone () throws CloneNotSupportedException {
		return (Bookmark) super.clone();
	}
	
	/**********************************************************************************
	 * <p>Offset method to call when the bookmark is shifted</p>
	 *
	 * @param  amount The number of bytes (positive or negative) to offset
	 */
	public void offest (int amount) {
		start += amount;
	}

	// - SIMPLE GETTERS BELOW HERE -
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	public String getCollection() {
		return collection;
	}

	public String getColumn() {
		return column;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public void setColumn(String column) {
		this.column = column;
	}

}
