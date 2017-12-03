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
 * A Bookmark Segment marks a location within the Content where sub-templates can be inserted
 * <p>Bookmarks are wrapped strings that start with "bookmark" and conform to this pattern</p>
 * <p><b>bookmark = "<i>name</i>" group = "<i>group</i>" template = "<i>template</i>" varyby = "<i>varyBy Attribute</i>" insertAlways</b></p>
 * <p>All fields are required except the varyby field and insertAlways indicator</p>
 * <p>The insert always indicator will allow bookmarks to insert sub-templates when the varyby attr is missing or not-primitive
 * without this parameter an exception is thrown on missing or non-primitive varyby attribute values.</p>
 * 
 * @author Mike Storey
 * @see com.ibm.util.merge.template.directive.Insert
 *
 */
public class BookmarkSegment extends Segment {
	private static final Pattern NAME_PATTERN 		= Pattern.compile("bookmark\\W*?=\\W*?\"(.*?)\"");
	private static final Pattern GROUP_PATTERN 		= Pattern.compile("group\\W*?=\\W*?\"(.*?)\"");
	private static final Pattern TEMPLATE_PATTERN 	= Pattern.compile("template\\W*?=\\W*?\"(.*?)\"");
	private static final Pattern VARYBY_PATTERN 		= Pattern.compile("varyby\\W*?=\\W*?\"(.*?)\"");
	private static final Pattern INSERT_PATTERN		= Pattern.compile("insertAlways");

	private String bookmarkName = "";
	private String templateGroup = "";
	private String templateName = "";
	private String varyByAttribute = "";
	private boolean insertAlways;

	public BookmarkSegment() {
		
	}
	
	/**
	 * Instantiate a Bookmark Object
	 * 
	 * @param source The text to be parsed
	 * @throws Merge500 on processing errors
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

		matcher = INSERT_PATTERN.matcher(source);
		insertAlways = matcher.find();
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
	 * @param value Current data context
	 * @return the Template Shorthand to insert
	 * @throws MergeException on processing errors
	 */
	public String getTemplateShorthand(DataElement value) throws MergeException {
		String reply = getDefaultShorthand();
		if (!this.varyByAttribute.isEmpty()) {
			if (value.isObject()) {
				if (!value.getAsObject().keySet().contains(varyByAttribute)) {
					if (insertAlways) {
						reply += "";
					} else {
						throw new Merge500("Vary by Attribute not found in Insert Context:" + varyByAttribute);
					}
				} else if (!value.getAsObject().get(varyByAttribute).isPrimitive()) {
					if (insertAlways) {
						reply += "";
					} else {
						throw new Merge500("Vary by Attribute not found in Insert Context:" + varyByAttribute);
					}
				} else {
					reply += value.getAsObject().get(varyByAttribute).getAsPrimitive();
				}
			} else if (value.isPrimitive()) {
				reply += value.getAsPrimitive();
			} else if (value.isList()) {
				if (insertAlways) {
					reply += "";
				} else {
					throw new Merge500("Insert Context is a list!");
				}
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
