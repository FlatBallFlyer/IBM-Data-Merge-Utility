/*
 * Copyright 2015 IBM
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

package com.ibm.tk;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

/**
 * This class represents a bookmark which is an insertion point in a template. 
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Merge
 */
public class Bookmark {
	private static final Logger log = Logger.getLogger( Bookmark.class.getName() );
	private String element;
	private String name;
	private int start;
	private int size;

	/********************************************************************************
	 * <p>Bookmark constructor</p>
	 *
	 * @param  Bookmark String in the form <tkBookmark name="THENAME"/>
	 * @param  The location within the template of this bookmark
	 * @throws tkException - Invalid Bookmark
	 * @return The new bookmark object
	 */
	public Bookmark (String contents, int initialStart) throws tkException {
		Pattern p = Pattern.compile("=\"(.*)\"");
		Matcher m = p.matcher(contents);
		if (m.find()) {
			element = contents;
			name = m.group(1);
			start = initialStart;
			size = element.length();
		} else {
			log.log(Level.SEVERE, "Malformed Bookmark " + contents);
			throw new tkException("Invalid Bookmark found: " + contents, "Invalid Bookmark");
		}
	}
	
	/********************************************************************************
	 * <p>Bookmark Clone constructor</p>
	 *
	 * @param  Bookmark to clone
	 * @throws tkException - Invalid Bookmark
	 * @return The new bookmark object
	 */
	public Bookmark (Bookmark from) {
		this.element = from.element;
		this.name = from.name;
		this.start = from.start;
		this.size = from.size;
	}
	
	/**********************************************************************************
	 * <p>Offset method to call when the bookmark is shifted</p>
	 *
	 * @param  The number of bytes (positive or negative) to offset
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

}
