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
/*
 * 
 */
package com.ibm.util.merge.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Stat;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.AbstractDirective;

/**
 * The Class Template - represents a Template with a collection of 
 * merge directives, and provides the core "Merge" functionality.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Template {

	public static final int CONTENT_HTML 	= 1;
	public static final int CONTENT_JSON 	= 2;
	public static final int CONTENT_XML 	= 3;
	public static final int CONTENT_TEXT 	= 4;
	public static final HashMap<Integer, String> CONTENT_TYPES() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(CONTENT_HTML, 	"html");
		values.put(CONTENT_JSON, 	"json");
		values.put(CONTENT_XML, 	"xml");
		values.put(CONTENT_TEXT,	"text");
		return values;
	}

	public static final int DISPOSITION_DOWNLOAD 	= 1;
	public static final int DISPOSITION_NORMAL	 	= 2;
	public static final HashMap<Integer, String> DISPOSITION_VALUES() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(DISPOSITION_DOWNLOAD, 	"download");
		values.put(DISPOSITION_NORMAL, 		"normal");
		return values;
	}

	private final TemplateId id;
	private String content 				= "";
	private int contentType				= Template.CONTENT_TEXT; 
	private int contentDisposition		= Template.DISPOSITION_NORMAL;
	private int contentEncoding			= TagSegment.ENCODE_NONE;
	private String contentFileName		= "";
	private String contentRedirectUrl	= "";
	private String description			= "";
	private Wrapper wrapper				= new Wrapper();
	private List<AbstractDirective> directives = new ArrayList<AbstractDirective>(); 
	
	private transient Boolean merged 	= false;
	private transient Boolean mergable 	= false;
	private transient Stat stats 		= new Stat();
	private transient Content mergeContent;
	private transient HashMap<String, String> replaceStack = new HashMap<String,String>();
	private transient Merger context;
	
	public Template(TemplateId id) {
		this.id = id;
		this.mergable = false;
		this.merged = false;
		this.replaceStack = new HashMap<String,String>();
	}
	
	public Template() {
		this(new TemplateId("void","void","void"));
	}
	
	public Template(String group, String name, String variant) {
		this(new TemplateId(group,name,variant));
	}
	
	public Template(String group, String name, String variant, String content) {
		this(new TemplateId(group,name,variant));
		this.setContent(content);
	}
	
	public Template(String group, String name, String variant, String content, String before, String after) {
		this(group,name,variant,content);
		this.wrapper.front = before;
		this.wrapper.back = after;
	}
	
	/**
	 * Gets the mergable.
	 *
	 * @param replace the replace
	 * @return the mergable
	 * @throws MergeException 
	 */
	public Template getMergable(Merger context) throws MergeException {
		return getMergable(context, new HashMap<String,String>());
	}
	
	public Template getMergable(Merger context, HashMap<String,String> replace) throws MergeException {
		this.stats.hits++;
		Template mergable = new Template(this.id);
		mergable.setContext(context);
		mergable.setContent(this.content.toString());
		mergable.setContentDisposition(contentDisposition);
		mergable.setContentEncoding(contentEncoding);
		mergable.setContentType(contentType);
		mergable.setDescription(description);
		mergable.setContentFileName(contentFileName);
		mergable.setContentRedirectUrl(contentRedirectUrl);
		mergable.replaceStack.putAll(replace);
		mergable.wrapper = this.wrapper;
		mergable.mergable = true;
		mergable.merged = false;
		mergable.mergeContent = new Content(this.wrapper, this.content, this.contentEncoding);
		for (AbstractDirective directive : this.directives) {
			mergable.addDirective(directive.getMergable());
		}
		return mergable;
	}

	/**
	 * Gets the merged output.
	 *
	 * @param context the context
	 * @return the merged output
	 */
	public Content getMergedOutput() throws MergeException {
		if (this.mergable) {
			if (!this.merged) {
				// Process Directives
				for (AbstractDirective directive : this.directives ) {
					directive.execute(this.context);
				}
				this.merged = true;
				this.directives.clear();
				this.replaceStack.clear();
			}
			return this.mergeContent;
		} else {
			throw new Merge500("Can not merge a non-mergable tempalte");
		}
	}

	/**
	 * Gets the replace stack.
	 *
	 * @return the replace stack
	 */
	public HashMap<String, String> getReplaceStack() {
		return replaceStack;
	}

	/**
	 * Adds a from/to pair to the replace stack.
	 *
	 * @return 
	 */
	public void addReplace(String from, String to) {
		// Accessible during merge
		if (this.mergable && !this.merged) {
			this.replaceStack.put(from, to);
		}
	}
	
	/**
	 * Adds a from/to pair to the replace stack.
	 *
	 * @return 
	 */
	public void addReplace(HashMap<String,String> values) {
		// Accessible during merge
		if (this.mergable && !this.merged) {
			this.replaceStack.putAll(values);
		}
	}

	public void blankReplace(HashSet<String> tags) {
		for (String tag : tags) {
			this.replaceStack.put(tag, "");
		}
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content.toString();
	}
	
	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public Content getMergeContent() {
		return this.mergeContent;
	}
	
	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 * @throws MergeException 
	 */
	public void setContent(String content) {
		// Accessible only before merge
		if (!this.mergable) {
			this.content = content;
			stats.size = this.content.length();
		}
	}
	
	/**
	 * Clears the content.
	 *
	 * @throws MergeException 
	 */
	public void clearContent() {
		// Accessible only during merge
		if (this.mergable && !this.merged) {
			this.content = "";
			stats.size = this.content.length();
		}
	}
	
	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public int getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the new content type
	 */
	public void setContentType(int contentType) {
		// Accessible only before merge
		if (!this.mergable) {
			if (CONTENT_TYPES().containsKey(contentType)) {
				this.contentType = contentType;
			}
		}
	}

	/**
	 * Gets the content disposition.
	 *
	 * @return the content disposition
	 * @throws Merge500 
	 */
	public String getContentDisposition() throws Merge500 {
		if (DISPOSITION_DOWNLOAD == contentDisposition) {
			Content fileName = new Content(this.wrapper, this.contentFileName, this.contentEncoding);
			fileName.replace(replaceStack, false, Config.MAX_NEST);
			return "attachment;filename=\"" + fileName.getValue() + "\"";
		} else {
			return "";
		}
	}

	/**
	 * Sets the content disposition.
	 *
	 * @param contentDisposition the new content disposition
	 */
	public void setContentDisposition(int contentDisposition) {
		// Accessible only before merge
		if (!this.mergable) {
			if (DISPOSITION_VALUES().containsKey(contentDisposition)) {
				this.contentDisposition = contentDisposition;
			}
		}
	}

	public String getContentFileName() {
		return contentFileName;
	}

	public void setContentFileName(String contentFileName) {
		// Accessible only before merge
		if (!this.mergable) {
			this.contentFileName = contentFileName;
		}
	}

	/**
	 * Gets the content encoding.
	 *
	 * @return the content encoding
	 */
	public int getContentEncoding() {
		return contentEncoding;
	}

	/**
	 * Sets the content encoding.
	 *
	 * @param contentEncoding the new content encoding
	 */
	public void setContentEncoding(int contentEncoding) {
		// Accessible only before merge
		if (!this.mergable) {
			if (TagSegment.ENCODE_VALUES().containsValue(contentEncoding)) {
				this.contentEncoding = contentEncoding;
			}
		}
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		// Accessible only before merge
		if (!this.mergable) {
			this.description = description;
		}
	}

	/**
	 * Gets the wrapper.
	 *
	 * @return the wrapper
	 */
	public Wrapper getWrapper() {
		return wrapper;
	}

	/**
	 * Sets the wrapper.
	 *
	 * @param wrapper the new wrapper
	 */
	public void setWrapper(String front, String back) {
		// Accessible only before merge
		if (!this.mergable) {
			this.wrapper = new Wrapper();
			this.wrapper.front = front;
			this.wrapper.back = back;
		}
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public TemplateId getId() {
		return id;
	}

	/**
	 * Gets the directives.
	 *
	 * @return the directives
	 */
	public List<AbstractDirective> getDirectives() {
		return directives;
	}

	/**
	 * Adds the directive.
	 *
	 * @param directive the directive
	 */
	public void addDirective(AbstractDirective directive) {
		directive.setTemplate(this);
		directives.add(directive);
	}

	/**
	 * Gets the merged.
	 *
	 * @return the merged
	 */
	public Boolean isMerged() {
		return merged;
	}

	/**
	 * Gets the merged.
	 *
	 * @return the merged
	 */
	public Boolean isMergable() {
		return mergable;
	}

	public String getContentRedirectUrl() {
		return contentRedirectUrl;
	}

	public void setContentRedirectUrl(String contentRedirectUrl) {
		// Accessible only before merge
		if (!this.mergable) {
			this.contentRedirectUrl = contentRedirectUrl;
		}
	}

	public String getEncodedRedirectURL() {
		// TODO Replace Process?
		return null;
	}
	
	public void initStats() {
		this.stats = new Stat();
		this.stats.name = this.getId().shorthand();
	}

	public Stat getStats() {
		return stats;
	}

	public Merger getContext() {
		return context;
	}

	public void setContext(Merger context) {
		this.context = context;
	}

}
