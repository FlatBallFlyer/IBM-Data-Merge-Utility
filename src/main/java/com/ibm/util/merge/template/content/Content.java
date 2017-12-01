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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.template.Wrapper;

/**
 * Template Content - the root of a circular dual linked list
 * <p>Content is a dual linked list of Segments that are either Text, a Replace Tag or a Book Mark. 
 * Segments are parsed at construction based on the Content Wrapper provided. Note that the wrappers
 * caon only appear in the content to identify a Tag or Bookmark segment, invalid content will throw
 * an exception on creation.</p>
 * 
 * <p><i>These optimizations replace string pattern matching and global search replace functions
 * with code that maintains and preserves string pointers and optimizes Java GC</i></p>
 * 
 * @author Mike Storey
 * @see com.ibm.util.merge.template.content.TagSegment
 * @see com.ibm.util.merge.template.content.BookmarkSegment
 *
 */
public class Content extends Segment {
	static final String BOOKMARK = "bookmark";
	
	private String source = "";
	private String open = "";
	private String close = "";

	public Content() {
		this.setNext(this);
		this.setPrevious(this);
	}
	
	/**
	 * Instantiate a content object
	 * 
	 * @param wrapper Content wrapper used
	 * @param content Content to parse
	 * @param encodeDefault Encoding default
	 * @throws Merge500 on processing errors
	 */
	public Content(Wrapper wrapper, String content, int encodeDefault) throws Merge500 {
		initialize(wrapper.front, wrapper.back, content, encodeDefault);
	}
	
	/**
	 * Instantiate a content object
	 * 
	 * @param open The open wrapper
	 * @param close The close wrapper
	 * @param content The content to parse
	 * @param encodeDefault The encoding default
	 * @throws Merge500 on processing errors
	 */
	public Content(String open, String close, String content, int encodeDefault) throws Merge500 {
		initialize(open, close, content, encodeDefault);
	}
	
	@Override
	public Content getMergable() {
		Content mergable = new Content();
		mergable.source = this.source;
		mergable.open = this.open;
		mergable.close = this.close;
		Segment segment = this.getFirst();
		while (segment != this) {
			mergable.insert(segment.getMergable());
			segment = segment.getNext();
		}
		return mergable;
	}
	/**
	 * Common initialization - Parse content
	 * @param open The open wrapper
	 * @param close The close wrapper
	 * @param content The content to parse
	 * @param encodeDefault The encoding default
	 * @throws Merge500 on processing errors
	 */
	private void initialize(String open, String close, String content, int encodeDefault) throws Merge500 {
		this.source = content;
		this.open = open;
		this.close = close;
		this.setNext(this);
		this.setPrevious(this);

		int startIndex = 0;
		int endIndex = content.indexOf(open, startIndex);

		while (endIndex >= 0) {
			if (endIndex > startIndex) this.insert(new TextSegment(content.substring(startIndex,endIndex)));
			startIndex = endIndex + open.length();
			endIndex = content.indexOf(close, startIndex);
			if (endIndex < 0) throw new Merge500("Invalid Content - Missing End Tag!");
			String segment = content.substring(startIndex, endIndex);
			if (segment.startsWith(BOOKMARK)) {
				BookmarkSegment newSeg = new BookmarkSegment(segment);
				this.insert(newSeg);
			} else {
				TagSegment newSeg = new TagSegment(open, close, segment, encodeDefault);
				this.insert(newSeg);
			}
			startIndex = endIndex + close.length();
			endIndex = content.indexOf(open,startIndex);
		}

		if (startIndex < content.length()) {
			String value = content.substring(startIndex);
			this.insert(new TextSegment(value));
		}
		
	}
	
	@Override
	public String getValue() {
		StringBuilder value = new StringBuilder();
		Segment seg = this.getFirst();
		while (seg != this) {
			value.append(seg.getValue());
			seg = seg.getNext();
		}
		return value.toString();
	}
	
	/**
	 * Write the content to an output stream
	 * 
	 * @param stream The stream to write to
	 * @throws Merge500 on processing errors
	 */
	public void streamValue(OutputStream stream) throws Merge500 {
		Segment seg = this.getFirst();
		while (seg != this) {
			try {
				stream.write(seg.getValue().getBytes());
			} catch (IOException e) {
				throw new Merge500(e.getMessage());
			}
			seg = seg.getNext();
		}
	}
	
	/**
	 * Replace all Tag's with values from Replace
	 * 
	 * @param replace From/To values for replace
	 * @param require Require all tags
	 * @param nestLimit Tag Replace nesting limit
	 * @throws Merge500 on processing errors
	 */
	public void replace(HashMap<String,String> replace, boolean require, int nestLimit) throws Merge500 {
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof TagSegment) {
				((TagSegment) seg).replace(replace, require, nestLimit);
			}
			seg = seg.getNext();
		}
	}

	/**
	 * Remove all bookmarks (called prior to inserting sub-template
	 * 
	 * @throws Merge500 on Processing Errors
	 */
	public void removeBookmarks() throws Merge500 {
		Segment seg = this.getFirst();
		while (seg != this) {
			Segment segNext = seg.getNext();
			if (seg instanceof BookmarkSegment) {
				seg.remove();
			}
			seg = segNext;
		}
	}

	/**
	 * Get list of Tag segments
	 * @return Tag List
	 */
	public ArrayList<TagSegment> getTags() {
		ArrayList<TagSegment> tags = new ArrayList<TagSegment>();
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof TagSegment) {
				tags.add((TagSegment) seg);
			}
			seg = seg.getNext();
		}
		return tags;
	}

	/**
	 * Get list of Bookmark Segments
	 * @return Bookmark List
	 */
	public ArrayList<BookmarkSegment> getBookmarks() {
		ArrayList<BookmarkSegment> bookmarks = new ArrayList<BookmarkSegment>();
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof BookmarkSegment) {
				bookmarks.add((BookmarkSegment) seg);
			}
			seg = seg.getNext();
		}
		return bookmarks;
	}

	/**
	 * Get list of Text Segments
	 * @return Segment List
	 */
	public ArrayList<TextSegment> getTexts() {
		ArrayList<TextSegment> texts = new ArrayList<TextSegment>();
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof TextSegment) {
				texts.add((TextSegment) seg);
			}
			seg = seg.getNext();
		}
		return texts;
	}

	/**
	 * @return Source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return Opening Wrapper
	 */
	public String getOpen() {
		return open;
	}

	/**
	 * @return Closing Wrapper
	 */
	public String getClose() {
		return close;
	}

	/**
	 * @return First Element
	 */
	public Segment getFirst() {
		return this.getNext();
	}
	
	/**
	 * @return Last Element
	 */
	public Segment getLast() {
		return this.getPrevious();
	}
}
