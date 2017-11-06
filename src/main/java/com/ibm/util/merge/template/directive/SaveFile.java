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

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.Archive;

/**
 * The Save File directive is used to write the output of a template to the current merge archive.
 * 
 * @author Mike Storey
 *
 */
public class SaveFile extends AbstractDirective {
	String filename;
	Boolean clearAfter;
	
	/**
	 * Instantiate a SaveFile directive with default values
	 */
	public SaveFile() {
		super();
		this.setType(AbstractDirective.TYPE_SAVE_FILE);
		this.clearAfter = false;
	}

	@Override
	public void execute(Merger context) throws MergeException {
		Archive archive = context.getArchive();
		archive.writeFile(this.filename, this.template.getContent(), "idmu-user", "idmu-group");
		if (this.clearAfter) {
			this.template.clearContent();
		}
	}

	@Override
	public AbstractDirective getMergable() {
		SaveFile mergable = new SaveFile();
		this.makeMergable(mergable);
		mergable.setFilename(filename);
		mergable.setClearAfter(clearAfter);
		return mergable;
	}

	/**
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename - the name of the file to be saved
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return clear replace stack after replace indicator
	 */
	public Boolean getClearAfter() {
		return clearAfter;
	}

	/**
	 * @param clearAfter 
	 */
	public void setClearAfter(Boolean clearAfter) {
		this.clearAfter = clearAfter;
	}

}
