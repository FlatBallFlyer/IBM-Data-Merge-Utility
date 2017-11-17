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

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.Archive;
import com.ibm.util.merge.template.Template;

/**
 * Save the output of this template to an entry in the merge Archive file
 * 
 * @author Mike Storey
 *
 */
public class SaveFile extends AbstractDirective {
	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		return options;
	}

	private String filename = "";
	private Boolean clearAfter = true;
	
	/**
	 * Instantiate a SaveFile directive with default values
	 */
	public SaveFile() {
		super();
		this.setType(AbstractDirective.TYPE_SAVE_FILE);
		this.clearAfter = false;
	}

	@Override
	public void cachePrepare(Template template) throws MergeException {
		super.cachePrepare(template);
	}

	@Override
	public AbstractDirective getMergable() throws MergeException {
		SaveFile mergable = new SaveFile();
		this.makeMergable(mergable);
		mergable.setFilename(filename);
		mergable.setClearAfter(clearAfter);
		return mergable;
	}

	@Override
	public void execute(Merger context) throws MergeException {
		Archive archive = context.getArchive();
		archive.writeFile(this.filename, this.getTemplate().getMergeContent().getValue(), "idmu-user", "idmu-group");
		if (this.clearAfter) {
			this.getTemplate().clearContent();
		}
	}

	/**
	 * @return the File Name
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename - the name of the file to be saved
	 * @throws MergeException  on processing errors
	 */
	public void setFilename(String filename) throws MergeException {
		this.filename = filename;
	}

	/**
	 * @return clear replace stack after replace indicator
	 */
	public Boolean getClearAfter() {
		return clearAfter;
	}

	/**
	 * @param clearAfter Clear Content after Saving indicator
	 */
	public void setClearAfter(Boolean clearAfter) {
		this.clearAfter = clearAfter;
	}

}
