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
package com.ibm.util.merge.template.content;

import java.util.HashMap;


/**
 * Abstract Content Segment - Implements dual-linked-list
 * 
 * @author Mike Storey
 *
 */
public abstract class Segment {
	public static final int ENCODE_NONE 	= 1;
	public static final int ENCODE_HTML 	= 2;
	public static final int ENCODE_SQL 		= 3;
	public static final int ENCODE_JSON 	= 4;
	public static final int ENCODE_XML 		= 5;
	public static final int ENCODE_DEFAULT 	= 6;
	public static final HashMap<String, Integer> ENCODE_VALUES() {
		HashMap<String, Integer> values = new HashMap<String, Integer>();
		values.put("none", 		ENCODE_NONE);
		values.put("html", 		ENCODE_HTML	);
		values.put("sql", 		ENCODE_SQL	);
		values.put("json", 		ENCODE_JSON	);
		values.put("xml", 		ENCODE_XML	);
		values.put("default",	ENCODE_DEFAULT);
		return values;
	}


	private Segment previous;
	private Segment next;
	
	/**
	 * Instantiate a Segment
	 */
	public Segment() {
	}
	
	/**
	 * Insert a Segment ahead of this
	 * 
	 * @param newSeg
	 */
	public void insert(Segment newSeg) {
		newSeg.next = this;
		newSeg.previous = this.previous;
		if (null != this.previous) {
			this.previous.setNext(newSeg);
		}
		this.previous = newSeg;
	}
	
	/**
	 * Insert Content ahead of this
	 * 
	 * @param newSeg
	 */
	public void insert(Content newSeg) {
		for (BookmarkSegment seg : newSeg.getBookmarks()) {
			seg.remove();
		}
		newSeg.getFirst().setPrevious(this.previous);
		newSeg.getLast().setNext(this);
		if (this.previous != null) {
			this.previous.setNext(newSeg.getFirst());
		}
		this.previous = newSeg.getLast();
	}
	
	/**
	 * Append a segment after this
	 * 
	 * @param newSeg
	 */
	public void append(Segment newSeg) {
		newSeg.next = this.next;
		newSeg.previous = this;
		if (this.next != null) {
			this.next.setPrevious(newSeg);
		}
		this.next = newSeg;
	}

	/**
	 * Append content after this
	 * 
	 * @param newSeg
	 */
	public void append(Content newSeg) {
		for (BookmarkSegment seg : newSeg.getBookmarks()) {
			seg.remove();
		}
		newSeg.getFirst().setPrevious(this);
		newSeg.getLast().setNext(this.next);
		if (this.next != null) {
			this.next.setPrevious(newSeg.getLast());
		}
		this.next = newSeg.getFirst();
	}
	
	/**
	 * Remove myself from the content
	 */
	public void remove() {
		this.getPrevious().setNext(this.next);
		this.getNext().setPrevious(this.previous);
		this.next = null;
		this.previous = null;
	}
	
	/**
	 * Replace with new Segment
	 * 
	 * @param 
	 */
	public void replaceWith(Segment newSeg) {
		if (null != this.getPrevious()) {
			this.getPrevious().setNext(newSeg);
		}
		if (null != this.getNext()) {
			this.getNext().setPrevious(newSeg);
		}
		newSeg.setNext(this.getNext());
		newSeg.setPrevious(this.getPrevious());
	}
	
	/**
	 * Replace with provided segment
	 * 
	 * @param newContent
	 */
	public void replaceWith(Content newContent) {
		if (null != this.getPrevious()) this.getPrevious().setNext(newContent.getFirst());
		if (null != this.getNext()) this.getNext().setPrevious(newContent.getLast());
		newContent.getFirst().setPrevious(this.getPrevious());
		newContent.getLast().setNext(this.getNext());
	}
	
	/**
	 * @return Previous element
	 */
	public Segment getPrevious() {
		return previous;
	}

	/**
	 * @return Next segment
	 */
	public Segment getNext() {
		return next;
	}

	/**
	 * Set previous segment
	 * 
	 * @param previous
	 */
	public void setPrevious(Segment previous) {
		if (previous != null) {
			this.previous = previous;
		}
	}

	/**
	 * Set next segment
	 * 
	 * @param next
	 */
	public void setNext(Segment next) {
		if (next != null) {
			this.next = next;
		}
	}

	/**
	 * @return the String value of the segment
	 */
	abstract public String getValue();
	
}

