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
package com.ibm.util.merge.template.directive.enrich.provider;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * Implements a local file system data provider that provides a hash of file-name to file-content values
 * 
 * @author flatballflyer
 *
 */
public class FileSystemProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient Parser parser = new Parser();
	
	public FileSystemProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		Content query = new Content(wrapper, command, TagSegment.ENCODE_NONE);
		query.replace(replace, false, Config.get().getNestLimit());
		DataObject result = new DataObject();

		File templateFolder = new File(this.getDbName());
        if (templateFolder.listFiles() == null) {
            throw new Merge500("File System Path Folder was not found:" + templateFolder);
        }
        
		String fileSelector = query.getValue();
        for (File file : templateFolder.listFiles()) {
            if (!file.isDirectory() && !file.getName().startsWith(".")) {
            	if (file.getName().matches(fileSelector.toString())) {
            		try {
            			String content = new String(Files.readAllBytes(file.toPath()));
            			result.put(file.getName(), new DataPrimitive(content));
            		} catch (FileNotFoundException e) {
            			// I Don't Think this can happen!
            		} catch (IOException e) {
            			// File Provider ignores files with i/o exceptions
            		}
            	}
            }
        }
        
		if (parseAs != Parser.PARSE_NONE) {
			for (String fileName : result.keySet()) {
				result.put(fileName, parser.parse(parseAs, result.get(fileName).getAsPrimitive()));
			}
		}

		return result;
	}
	
	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public String getDbName() {
		return this.dbName;
	}

	@Override
	public Merger getContext() {
		return this.context;
	}

	@Override
	public ProviderMeta getMetaInfo() {
		return new ProviderMeta(
				"Directory to read files from",
				"No further configuration needed",
				"A Java RegEx file selector",
				"file content is parsed in the return object",
				"returns an object of <String, Primitive> if not parsed, and String, Element if parsed");
	}
	
}
