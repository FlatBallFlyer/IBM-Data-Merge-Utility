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
 * The template is a state-full object that goes through a three phase life
 * - Raw templates are constructed, or parsed from JSON and as such can
 *   have invalid values for some attributes. 
 * - Cached templates have been validated and had transient values initialized. 
 *   The cache put/post methods utilize the cachePrepare() method to transform
 *   a "Raw" template into a Cached template.
 * - Mergable templates are a clone of a cached template. Only in this state can a 
 *   template be "merged". The Merger getMergable method is used to get templates
 *   for merging.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Template {

	/**
	 * The unique Template ID
	 */
	private final TemplateId id;
	/**
	 * The content of the template. Template content consist of text, with any number 
	 * of Replace Tag or Book-mark segments that are identified by the template.wrapper.  
	 * Note that the wrapper characters can not appear anywhere in the template 
	 * content except to identify a Tag or Book-mark.
	 */
	private String content 				= "";
	/**
	 * The wrapper used in the content
	 */
	private Wrapper wrapper				= new Wrapper();
	/**
	 * Content Type - provided for rest content type reply
	 */
	private int contentType				= Template.CONTENT_TEXT; 
	/**
	 * Content Disposition - provided for rest "Download" dispositions
	 */
	private int contentDisposition		= Template.DISPOSITION_NORMAL;
	/**
	 * Content Encoding - drives default data encoding
	 */
	private int contentEncoding			= TagSegment.ENCODE_NONE;
	/**
	 * A file name for Download Content Dispositions
	 */
	private String contentFileName		= "";
	/**
	 * A redirect URL - Provided for Rest implementations
	 */
	private String contentRedirectUrl	= "";
	/**
	 * A short description of the template - used in exception handling and logging 
	 */
	private String description			= "";
	/**
	 * The directives to execute during a merge
	 */
	private List<AbstractDirective> directives = new ArrayList<AbstractDirective>(); 
	
	/*
	 * Transient values - initialized in cachePrepare, copied in getMergavble
	 */
	private transient Boolean merged 	= false;
	private transient Boolean mergable 	= false;
	private transient Stat stats 		= new Stat();
	private transient Content mergeContent;
	private transient HashMap<String, String> replaceStack = new HashMap<String,String>();
	private transient Merger context;
	
	/**
	 * Instantiate a Template with the given ID
	 * @param id The Template ID
	 * @throws MergeException  on processing errors
	 */
	public Template(TemplateId id) throws MergeException {
		this.id = id;
		this.mergable = false;
		this.merged = false;
		this.setContent("");
		this.replaceStack = new HashMap<String,String>();
	}
	
	/**
	 * Instantiate a Template with the given ID
	 * @throws MergeException  on processing errors
	 */
	public Template() throws MergeException {
		this(new TemplateId("void","void","void"));
	}
	
	/**
	 * Instantiate a Template with the given ID
	 * 
	 * @param group The template group
	 * @param name The template name
	 * @param variant The template variant
	 * @throws MergeException  on processing errors
	 */
	public Template(String group, String name, String variant) throws MergeException {
		this(new TemplateId(group,name,variant));
	}
	
	/**
	 * Instantiate a Template with the given ID and Content
	 * 
	 * @param group The template group
	 * @param name The template name
	 * @param variant The template variant
	 * @param content The template content
	 * @throws MergeException  on processing errors
	 */
	public Template(String group, String name, String variant, String content) throws MergeException {
		this(new TemplateId(group,name,variant));
		this.setContent(content);
	}
	
	/**
	 * Instantiate a Template with the given ID, content and wrapper specification
	 * @param group The template Group
	 * @param name The template Name
	 * @param variant The template Variant
	 * @param content The template content
	 * @param before The wrapper open string
	 * @param after The wrapper close string 
	 * @throws MergeException  on processing errors
	 */
	public Template(String group, String name, String variant, String content, String before, String after) throws MergeException {
		this(group,name,variant,content);
		this.wrapper.front = before;
		this.wrapper.back = after;
	}
	
	/**
	 * Validate all ordinal values, populate transient values.
	 * @throws MergeException on processing errors
	 */
	public void cachePrepare() throws MergeException {
		// TODO Validate Enums
		this.stats = new Stat();
		this.stats.name = this.getId().shorthand();
		this.stats.size = this.content.length();
		this.merged 	= false;
		this.mergable 	= false;
		this.replaceStack = new HashMap<String,String>();
		this.setContent(content);
		for (AbstractDirective directive : this.directives) {
			directive.cachePrepare(this);
		}
	}
	
	/**
	 * Gets a mergable copy of this template and an empty replace stack
	 *
	 * @param context The merge context
	 * @return the mergable
	 * @throws MergeException  on processing errors
	 */
	public Template getMergable(Merger context) throws MergeException {
		return getMergable(context, new HashMap<String,String>());
	}
	
	/**
	 * Gets a mergable copy of this template (Clone like action)
	 * 
	 * @param context The Merge Context
	 * @param replace The repalce stack
	 * @return The mergable template
	 * @throws MergeException on processing errors
	 */
	public Template getMergable(Merger context, HashMap<String,String> replace) throws MergeException {
		this.stats.hits++;
		Template mergable = new Template(this.id);
		mergable.setContext(context);
		mergable.setContentDisposition(contentDisposition);
		mergable.setContentEncoding(contentEncoding);
		mergable.setContentType(contentType);
		mergable.setDescription(description);
		mergable.setContentFileName(contentFileName);
		mergable.setContentRedirectUrl(contentRedirectUrl);
		mergable.content = this.content;
		mergable.wrapper = this.wrapper;
		mergable.mergable = true;
		mergable.merged = false;
		mergable.mergeContent = this.getMergeContent().getMergable();
		mergable.replaceStack.putAll(replace);
		for (AbstractDirective directive : this.directives) {
			mergable.addDirective(directive.getMergable());
		}
		return mergable;
	}

	/**
	 * Gets the merged output, calling the merge if needed
	 *
	 * @return the merged output
	 * @throws MergeException  on processing errors
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
	 * @param from From Value
	 * @param to To Value
	 */
	public void addReplace(String from, String to) {
		// Accessible during merge
		if (this.mergable && !this.merged) {
			this.replaceStack.put(from, to);
		}
	}
	
	/**
	 * Adds a from/to pair to the replace stack.
	 * @param values The values to add
	 */
	public void addReplace(HashMap<String,String> values) {
		// Accessible during merge
		if (this.mergable && !this.merged) {
			this.replaceStack.putAll(values);
		}
	}

	/**
	 * Remove the replace value for one or more tags.
	 * 
	 * @param tags Tags to set to ""
	 */
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
	 * @throws MergeException  on processing errors
	 */
	public void setContent(String content) throws MergeException {
		// Accessible only before merge
		if (!this.mergable) {
			this.content = content;
			stats.size = this.content.length();
			this.mergeContent = new Content(this.wrapper, this.content, this.getContentEncoding() );
		}
	}
	
	/**
	 * Clears the content.
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
	 * @throws MergeException  on processing errors
	 */
	public String getContentDisposition() throws MergeException {
		if (DISPOSITION_DOWNLOAD == contentDisposition) {
			Content fileName = new Content(this.wrapper, this.contentFileName, this.contentEncoding);
			fileName.replace(replaceStack, false, Config.nestLimit() );
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

	/**
	 * @return file name for output archive
	 */
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
	 * @param front the Front wrapper
	 * @param back The back wrapper
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

	/**
	 * @return redirect URL
	 */
	public String getContentRedirectUrl() {
		return contentRedirectUrl;
	}

	/**
	 * @param contentRedirectUrl The redirect URL
	 */
	public void setContentRedirectUrl(String contentRedirectUrl) {
		// Accessible only before merge
		if (!this.mergable) {
			this.contentRedirectUrl = contentRedirectUrl;
		}
	}

	/**
	 * @return The redirect URL
	 */
	public String getEncodedRedirectURL() {
		// TODO Replace Process?
		return null;
	}
	
	/**
	 * @return Statistics.
	 */
	public Stat getStats() {
		return stats;
	}

	/**
	 * @return merge context
	 */
	public Merger getContext() {
		return context;
	}

	/**
	 * Set merge context 
	 * 
	 * @param context The merge context
	 */
	public void setContext(Merger context) {
		this.context = context;
	}

	/*
	 * Class Constants and getOptions()
	 */
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
	public static final int DISPOSITION_ARCHIVE	 	= 3;
	public static final HashMap<Integer, String> DISPOSITION_VALUES() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(DISPOSITION_DOWNLOAD, 	"download");
		values.put(DISPOSITION_ARCHIVE, 	"download archive");
		values.put(DISPOSITION_NORMAL, 		"normal");
		return values;
	}

	/**
	 * @return option values and descriptions for the Tempalte Class
	 */
	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		options.put("Content Type", CONTENT_TYPES());
		options.put("Content Disposition", DISPOSITION_VALUES());
		return options;
	}
	
}
