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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.util.merge.exception.Merge500;

/**
 * Represents a replace tag in Content
 * <p>Tags are any wrapped strings that are not bookmarks, and conform to this pattern</p>
 * <p><b>tag = "<i>name</i>" encode = "<i>encode</i>" format = "<i>formatString</i>" parseFirst</b></p>
 * <p>All fields are optional - with the wrappers {} {foo} is the same as {tag=foo}</p>
 * <ul>
 * 	<li>tag name is the "From" value used in the Replace Processing</li>
 *  <li>encode will override the default template encoding for this one tag</li>
 *  <li>format is used to format values with Java String formatting features</li>
 *  <li>parseFirst if present will cause the replace To value to be parsed for embedded tags</li>
 * </ul>
 * <p>See the Replace directive for details on how Tags are used</p> 
 * 
 * @author Mike Storey
 *
 */
public class TagSegment extends Segment {
	private static final Pattern TAG_PATTERN 		= Pattern.compile("tag.*?=.*?\"(.*?)\"");
	private static final Pattern ENCODE_PATTERN 	= Pattern.compile(" encode.*?=.*?\"(.*?)\"");
	private static final Pattern FORMAT_PATTERN 	= Pattern.compile(" format.*?=.*?\"(.*?)\"");
	private static final Pattern PARSE_PATTERN 		= Pattern.compile(" parseFirst");

	private String tag = "";
	private String open = "";
	private String close = "";
	private String source = "";
	private int encode = ENCODE_DEFAULT;
	private int defaultEncoding = ENCODE_NONE;
	private boolean parseFirst = false;
	private String format = "";
	
	public TagSegment() {
		
	}
	
	/**
	 * Instantiate a new Tag object
	 * 
	 * @param open The open Wrapper
	 * @param close The close Wrapper
	 * @param source The Source String to parse
	 * @param defaultEncode The default encoding to configure
	 * @throws Merge500 on processing errors
	 */
	public TagSegment(String open, String close, String source, Integer defaultEncode) throws Merge500 {
		super();
		if (defaultEncode == Segment.ENCODE_DEFAULT) throw new Merge500("Invalid Default Encoding value ");
		this.defaultEncoding = defaultEncode;
		this.open = open;
		this.close = close;
		this.source = source;
		this.parseFirst = false;
		
		Matcher matcher;
		matcher = TAG_PATTERN.matcher(source);
		if (matcher.find()) {
			this.tag = matcher.group(1);
		} else {
			if (source.indexOf(' ') > 0) {
				this.tag = source.substring(0, source.indexOf(' '));
			} else {
				this.tag = source;
			}
		}

		matcher = ENCODE_PATTERN.matcher(source);
		if (matcher.find()) {
			String encodeString = matcher.group(1); 
			if (ENCODE_VALUES().containsKey(encodeString) ) {
				this.encode = ENCODE_VALUES().get(encodeString);
			} else {
				throw new Merge500("Invalid Encoding in " + source);
			}
		}
		if (this.encode == ENCODE_DEFAULT) this.encode = this.defaultEncoding;

		matcher = FORMAT_PATTERN.matcher(source);
		if (matcher.find()) {
			this.format = matcher.group(1);
		}

		matcher = PARSE_PATTERN.matcher(source);
		if (matcher.find()) {
			this.parseFirst = true;
		}
	}

	@Override
	public Segment getMergable() {
		TagSegment mergable = new TagSegment();
		mergable.tag = this.tag;
		mergable.open = this.open;
		mergable.close = this.close;
		mergable.source = this.source;
		mergable.encode = this.encode;
		mergable.defaultEncoding = this.defaultEncoding;
		mergable.parseFirst = this.parseFirst;
		mergable.format = this.format;
		return mergable;
	}
	
	/**
	 * Replace the value in the tag segment.
	 * 
	 * @param values The From/To Values
	 * @param require Require all values indicator
	 * @param nestLimit The nesting limit for replace tags
	 * @throws Merge500 on processing errors
	 */
	public void replace(HashMap<String,String> values, boolean require, int nestLimit) throws Merge500 {
		String value;
		if (values.containsKey(tag)) {
			value = values.get(tag);
		} else {
			if (require) throw new Merge500("Tag Not Found: " + tag + " in " + values.toString());
			return;
		}
		
		if (this.parseFirst) {
			if (nestLimit < 1) throw new Merge500("Nest Limit Reached");
			Content newContent = new Content(open, close, value, encode);
			for (TextSegment seg : newContent.getTexts()) {
				seg.encode(encode);
			}
			newContent.replace(values, require, --nestLimit);
			this.replaceWith(newContent);
		} else {
			TextSegment newSeg = new TextSegment(value);
			newSeg.encode(encode);
			this.replaceWith(newSeg);
		}
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return tag encoding
	 */
	public int getEncode() {
		return encode;
	}

	/**
	 * @return tag format string
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return tag parsefirst indicator
	 */
	public boolean isParseFirst() {
		return parseFirst;
	}
	
	/**
	 * @param parse parseFirst indicator
	 */
	public void setParseFirst(boolean parse) {
		parseFirst = parse;
	}
	
	@Override
	public String getValue() {
		return open + source + close;
	}

}
