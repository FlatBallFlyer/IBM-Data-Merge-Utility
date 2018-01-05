/*
 * 
c * Copyright 2015-2017 IBM
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

import com.ibm.util.merge.Cache;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Stat;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.AbstractDirective;

/**
 * <p>Template Content and a list of Directives that drive Merge functionality</p>
 * 
 * <p>Template content consist of text, with any number of Replace Tag or Book-mark segments 
 * that are identified by the template.wrapper which is a simple "open" and "close" pair 
 * that surround the tag and book-mark segments.</p>  
 * <p><b>Note:</b> Wrapper characters can not appear anywhere in the template 
 * content except to identify a Tag or Book-mark.</p>
 * <p>The template is a state-full object that goes through a four phase life-cycle</p>
 * <ul>
 * 	<li>Raw templates are constructed, or parsed from JSON and as such can
 *   have invalid values for some attributes. </li>
 *  <li> Cached templates have been validated and had transient values initialized. 
 *   The cache put/post methods utilize the cachePrepare() method to transform
 *   a "Raw" template into a Cache Ready template.</li>
 *  <li>Mergable templates are a clone of a cached template. Only in this state can a 
 *   template be "merged". The Cache uses the getMergable() method to get a mergable
 *   clone of the cached template.</li>
 *  <li>Merged templates have completed merge processing and can provide merged output.
 *  Note that calling getMergedOutput for a non-merged template will cause the merge
 *  to occur.</li>
 * </ul>
 * @author Mike Storey
 * @since: v4.0
 * @see com.ibm.util.merge.template.content.Content
 * @see com.ibm.util.merge.template.content.TagSegment
 * @see com.ibm.util.merge.template.content.BookmarkSegment
 * @see com.ibm.util.merge.template.directive.AbstractDirective
 * @see com.ibm.util.merge.data.DataManager
 */
public class Template {

	private final TemplateId id;
	private String content 				= "";
	private Wrapper wrapper				= new Wrapper();
	private int contentEncoding			= TagSegment.ENCODE_NONE;
	private String contentType			= ""; 
	private String contentDisposition	= "";
	private String contentRedirectUrl	= "";
	private String description			= "";
	private List<AbstractDirective> directives = new ArrayList<AbstractDirective>(); 
	
	/*
	 * Transient values - initialized in cachePrepare, copied in getMergavble
	 */
	private transient int state			= STATE_RAW;
	private transient Stat stats 		= new Stat();
	private transient Content mergeContent;
	private transient HashMap<String, String> replaceStack = new HashMap<String,String>();
	private transient Merger context;
	private transient Config config;
	
	/**
	 * Instantiate a void template - provided for testing only
	 * @throws MergeException  on processing errors
	 */
	public Template() throws MergeException {
		this(new TemplateId("void","void","void"));
	}
	
	/**
	 * Instantiate a Template with the given ID
	 * @param id The Template ID
	 * @throws MergeException  on processing errors
	 */
	public Template(TemplateId id) throws MergeException {
		this.id = id;
		this.setContent("");
		this.replaceStack = new HashMap<String,String>();
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
	 * @param cache The cache we're being added to.
	 * @throws MergeException on processing errors
	 */
	public void cachePrepare(Cache cache) throws MergeException {
		if (this.state != STATE_RAW) {
			throw new Merge500("Can only prepare a RAW template");
		}
		this.config = cache.getConfig();
		this.stats = new Stat();
		this.stats.name = this.getId().shorthand();
		this.stats.size = this.content.length();
		this.mergeContent = new Content(this.wrapper, this.content, this.contentEncoding);
		this.replaceStack = new HashMap<String,String>();
		this.context = null;
		for (AbstractDirective directive : this.directives) {
			directive.cachePrepare(this, config);
		}
		this.state = STATE_CACHED;
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
	 * Gets a mergable copy of this template with the replace stack pre-loaded
	 * 
	 * @param context The Merge Context
	 * @param replace The repalce stack
	 * @return The mergable template
	 * @throws MergeException on processing errors
	 */
	public Template getMergable(Merger context, HashMap<String,String> replace) throws MergeException {
		if (this.state != STATE_CACHED) {
			throw new Merge500("Can only get a mergable template from cached template");
		}
		
		Template mergable = new Template(this.id);
		mergable.setConfig(config);
		mergable.setContext(context);
		mergable.setContentDisposition(contentDisposition);
		mergable.setContentEncoding(contentEncoding);
		mergable.setContentType(contentType);
		mergable.setDescription(description);
		mergable.setContentRedirectUrl(contentRedirectUrl);
		mergable.setContent(this.content);
		mergable.setWrapper(this.wrapper);
		mergable.mergeContent = this.getMergeContent().getMergable();
		mergable.replaceStack.putAll(replace);
		for (AbstractDirective directive : this.directives) {
			mergable.addDirective(directive.getMergable(context));
		}
		mergable.state = STATE_MERGABLE;
		return mergable;
	}

	/**
	 * Gets the merged output, calling the merge if needed
	 *
	 * @return the merged output
	 * @throws MergeException  on processing errors
	 */
	public Content getMergedOutput() throws MergeException {
		if (this.state < STATE_MERGABLE) { 
			throw new Merge500("Template is not in a mergable state");
		}
		
		if (this.state == STATE_MERGABLE) {
			Long start = System.currentTimeMillis();
			for (AbstractDirective directive : this.directives ) {
				try {
					directive.execute(this.context);
				} catch (MergeException e) {
					e.setDirective(directive);
					e.setTemplate(this);
					throw e;
				}
			}
			this.state = STATE_MERGED;
			this.context.getCahce().postStats(this.id.shorthand(), System.currentTimeMillis() - start);
		}
		return this.mergeContent;
	}

	/**
	 * Adds the directive.
	 *
	 * @param directive the directive
	 */
	public void addDirective(AbstractDirective directive) {
		if (this.state == STATE_RAW) {
			directive.setTemplate(this);
			directives.add(directive);
		}
	}

	/**
	 * Adds a from/to pair to the replace stack.
	 *
	 * @param from From Value
	 * @param to To Value
	 */
	public void addReplace(String from, String to) {
		if (this.state == STATE_MERGABLE) {
			this.replaceStack.put(from, to);
		}
	}
	
	/**
	 * Adds a from/to pair to the replace stack.
	 * @param values The values to add
	 */
	public void addReplace(HashMap<String,String> values) {
		if (this.state == STATE_MERGABLE) {
			this.replaceStack.putAll(values);
		}
	}

	/**
	 * Remove the replace value for one or more tags.
	 * 
	 * @param tags Tags to set to ""
	 */
	public void blankReplace(HashSet<String> tags) {
		if (this.state == STATE_MERGABLE) {
			for (String tag : tags) {
				this.replaceStack.put(tag, "");
			}
		}
	}

	/**
	 * Clears the content.
	 */
	public void clearContent() {
		if (this.state == STATE_MERGABLE) {
			this.mergeContent = new Content();
			this.content = "";
		}
	}
	
	/**
	 * Set the Content Wrapper strings
	 * @param wrapper the value
	 */
	private void setWrapper(Wrapper wrapper) {
		if (this.state == STATE_RAW) {
			this.wrapper = wrapper;
		}
	}

	/**
	 * Sets the wrapper.
	 *
	 * @param front the Front wrapper
	 * @param back The back wrapper
	 */
	public void setWrapper(String front, String back) {
		if (this.state == STATE_RAW) {
			this.wrapper = new Wrapper();
			this.wrapper.front = front;
			this.wrapper.back = back;
		}
	}

	private void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(String content) {
		if (this.state == STATE_RAW) {
			this.content = content;
		}
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the new content type
	 */
	public void setContentType(String contentType) {
		if (this.state == STATE_RAW) {
			this.contentType = contentType;
		}
	}

	/**
	 * Sets the content disposition.
	 *
	 * @param contentDisposition the new content disposition
	 */
	public void setContentDisposition(String contentDisposition) {
		if (this.state == STATE_RAW) {
			this.contentDisposition = contentDisposition;
		}
	}

	/**
	 * Sets the content encoding.
	 *
	 * @param contentEncoding the new content encoding
	 */
	public void setContentEncoding(int contentEncoding) {
		if (this.state == STATE_RAW) {
			if (TagSegment.ENCODE_VALUES().containsValue(contentEncoding)) {
				this.contentEncoding = contentEncoding;
			}
		}
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		if (this.state == STATE_RAW) {
			this.description = description;
		}
	}

	/**
	 * @param contentRedirectUrl The redirect URL
	 */
	public void setContentRedirectUrl(String contentRedirectUrl) {
		if (this.state == STATE_RAW) {
			this.contentRedirectUrl = contentRedirectUrl;
		}
	}

	/**
	 * Set merge context 
	 * 
	 * @param context The merge context
	 */
	public void setContext(Merger context) {
		this.context = context;
	}

	/**
	 * Update cached template stats - NOTE: Not Synchronized, subject to inaccuracy 
	 * @param response The response time for a merge action
	 */
	public void postStats(Long response) {
		if (this.state == STATE_CACHED) {
			this.stats.post(response);
		}
	}

	///////////////////////////////////////////////////////////
	// Simple Getters 
	///////////////////////////////////////////////////////////
	/**
	 * Gets the Merge Content
	 *
	 * @return the content
	 */
	public Content getMergeContent() {
		return this.mergeContent;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public TemplateId getId() {
		return id;
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
	 * Gets the wrapper.
	 *
	 * @return the wrapper
	 */
	public Wrapper getWrapper() {
		return wrapper;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Gets the content disposition.
	 *
	 * @return the content disposition
	 * @throws MergeException  on processing errors
	 */
	public String getContentDisposition() throws MergeException {
		if (this.state == STATE_MERGED) {
			Content disposition = new Content(this.wrapper, this.contentDisposition, this.contentEncoding);
			disposition.replace(replaceStack, false, config.getNestLimit());
			return disposition.getValue();
		} else {
			return this.contentDisposition;
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
	 * @return redirect URL
	 */
	public String getContentRedirectUrl() {
		return contentRedirectUrl;
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
	 * @return Statistics.
	 */
	public Stat getStats() {
		return stats;
	}

	/**
	 * @return State.
	 */
	public int getState() {
		return state;
	}

	///////////////////////////////////////////////////////////
	// Static Values 
	///////////////////////////////////////////////////////////
	/**
	 * @return option values and descriptions for the Tempalte Class
	 */
	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		return options;
	}

	public static final int STATE_RAW 		= 0;
	public static final int STATE_CACHED 	= 1;
	public static final int STATE_MERGABLE 	= 3;
	public static final int STATE_MERGED 	= 4;
	
}
