/*
 * Copyright (c) 2015 IBM 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
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
