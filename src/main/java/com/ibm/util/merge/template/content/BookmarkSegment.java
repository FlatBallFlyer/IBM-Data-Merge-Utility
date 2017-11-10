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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * Bookmark represents a Bookmark Segment within the Content
 * 
 * @author Mike Storey
 *
 */
public class BookmarkSegment extends Segment {
	private static final Pattern NAME_PATTERN 		= Pattern.compile("bookmark\\W*?=\\W*?\"(.*?)\"");
	private static final Pattern GROUP_PATTERN 		= Pattern.compile("group\\W*?=\\W*?\"(.*?)\"");
	private static final Pattern TEMPLATE_PATTERN 	= Pattern.compile("template\\W*?=\\W*?\"(.*?)\"");
	private static final Pattern VARYBY_PATTERN 	= Pattern.compile("varyby\\W*?=\\W*?\"(.*?)\"");

	private String bookmarkName = "";
	private String templateGroup = "";
	private String templateName = "";
	private String varyByAttribute = "";

	public BookmarkSegment() {
		
	}
	
	/**
	 * Instantiate a Bookmark Object
	 * 
	 * @param source
	 * @throws Merge500
	 */
	public BookmarkSegment(String source) throws Merge500 {
		super();
		Matcher matcher;
		matcher = NAME_PATTERN.matcher(source);
		if (matcher.find()) {
			this.bookmarkName = matcher.group(1);
		} else {
			throw new Merge500("Malformed Bookmark found " + source);
		}

		matcher = TEMPLATE_PATTERN.matcher(source);
		if (matcher.find()) {
			this.templateName = matcher.group(1);
		} else {
			throw new Merge500("Malformed Bookmark found " + source);
		}

		matcher = GROUP_PATTERN.matcher(source);
		if (matcher.find()) {
			templateGroup = matcher.group(1);
		} else {
			throw new Merge500("Malformed Bookmark found " + source);
		}

		matcher = VARYBY_PATTERN.matcher(source);
		if (matcher.find()) {
			varyByAttribute = matcher.group(1);
		} 
	}


	@Override
	public Segment getMergable() {
		BookmarkSegment mergable = new BookmarkSegment();
		mergable.bookmarkName = this.bookmarkName;
		mergable.templateGroup = this.templateGroup;
		mergable.templateName = this.templateName;
		mergable.varyByAttribute = this.varyByAttribute;
		return mergable;
	}

	/**
	 * Get the template to insert 
	 * 
	 * @param value
	 * @return
	 * @throws MergeException
	 */
	public String getTemplateShorthand(DataElement value) throws MergeException {
		String reply = getDefaultShorthand();
		if (!this.varyByAttribute.isEmpty()) {
			if (value.isObject()) {
				if (!value.getAsObject().keySet().contains(varyByAttribute)) {
					throw new Merge500("Vary by Attribute not found in Insert Context:" + varyByAttribute);
				} else {
					reply += value.getAsObject().get(varyByAttribute).getAsPrimitive();
				}
			} else if (value.isPrimitive()) {
				reply += value.getAsPrimitive();
			} else if (value.isList()) {
				throw new Merge500("Insert Context is a list!");
			}
		}
		return reply;
	}
	
	@Override
	public String getValue() {
		return "";
	}

	/**
	 * @return Bookmark Name
	 */
	public String getBookmarkName() {
		return bookmarkName;
	}

	/**
	 * @return Template Group 
	 */
	public String getTemplateGroup() {
		return templateGroup;
	}

	/**
	 * @return Template Name
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @return Template Vary By Attribute
	 */
	public String getVaryByAttribute() {
		return varyByAttribute;
	}

	/**
	 * @return Template Group
	 */
	public String getDefaultShorthand() {
		return templateGroup + "." + templateName + ".";
	}
	
}
