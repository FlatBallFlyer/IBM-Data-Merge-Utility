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
package com.ibm.util.merge.template.directive;

import java.util.HashMap;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.Archive;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;

/**
 * Save the output of this template to an entry in the merge Archive file, and optionally clear the template content.
 * 
 * @author Mike Storey
 * @see com.ibm.util.merge.storage
 */
public class SaveFile extends AbstractDirective {
	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		return options;
	}

	private String filename = "file";
	private Boolean clearAfter = false;
	private transient Content fileNameContent;
	
	/**
	 * Instantiate a SaveFile directive with default values
	 */
	public SaveFile() {
		super();
		this.setType(AbstractDirective.TYPE_SAVE_FILE);
		this.clearAfter = false;
	}

	@Override
	public void cachePrepare(Template template, Config config) throws MergeException {
		super.cachePrepare(template,config);
		this.fileNameContent = new Content(this.getTemplate().getWrapper(), this.filename, TagSegment.ENCODE_NONE);
	}

	@Override
	public AbstractDirective getMergable(Merger context) throws MergeException {
		SaveFile mergable = new SaveFile();
		this.makeMergable(mergable, context);
		mergable.setFilename(filename);
		mergable.setClearAfter(clearAfter);
		mergable.setFilenameContent(this.fileNameContent.getMergable());
		return mergable;
	}

	@Override
	public void execute(Merger context) throws MergeException {
		Archive archive = context.getArchive();
		this.fileNameContent.replace(this.getTemplate().getReplaceStack(), true, this.getConfig().getNestLimit());
		archive.writeFile(this.fileNameContent.getValue(), this.getTemplate().getMergeContent().getValue(), "idmu-user", "idmu-group");
		if (this.clearAfter) {
			this.getTemplate().clearContent();
		}
	}

	/**
	 * @return clear replace stack after replace indicator
	 */
	public Boolean getClearAfter() {
		return clearAfter;
	}

	/**
	 * @return the File Name
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param clearAfter Clear Content after Saving indicator
	 */
	public void setClearAfter(Boolean clearAfter) {
		this.clearAfter = clearAfter;
	}

	/**
	 * @param filename - the name of the file to be saved
	 * @throws MergeException  on processing errors
	 */
	public void setFilename(String filename) throws MergeException {
		this.filename = filename;
	}

	/**
	 * get the FileName conetent object
	 * @param content
	 */
	private void setFilenameContent(Content content) {
		this.fileNameContent = content;
	}

}
