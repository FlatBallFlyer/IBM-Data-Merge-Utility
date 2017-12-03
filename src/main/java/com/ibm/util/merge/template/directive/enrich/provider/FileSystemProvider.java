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
 * <p>A file system provider that reads all the files which match a file name pattern into an Object of FileName-FileContents.</p>
 * <p>Provide Parameters usage</p>
 * <ul>
 * 		<li>String command - A Java RegEx expression used as a file name selector, can contain replace tags</li>
 * 		<li>int parseAs - Value from {@link com.ibm.util.merge.Config#PARSE_OPTIONS} If not NONE, contents are parsed, otherwise they are returned as a single primitive</li>
 * 		<li>Wrapper wrapper - Wrapper for tags in command</li>
 * 		<li>HashMap&lt;String,String&gt; replace - The Replace HashMap used to process tags in command</li>
 * 		<li>Merger context - Merger managing the merge</li>
 * </ul>
 * <p>Configuration Environment Variables</p>
 * <ul>
 * 		<li>{SourceName}.PATH - The Path where files are to be read from.</li>
 * </ul>
 * 
 * @author Mike Storey
 * @see #FileSystemProvider(String, String, Merger)
 */
public class FileSystemProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient final Config config;
	private transient File basePath;

	/**
	 * Provider Constructor
	 * 
	 * @param source - The Source Prefix for Environment Variables
	 * @param dbName - Not Used by this provider
	 * @param context - The Merger hosting this provider
	 */
	public FileSystemProvider(String source, String dbName, Merger context) {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
		this.config = context.getConfig();
	}

	/**
	 * lazy loader of Provider Configuration values
	 * @throws Merge500 on configuration and connection errors
	 */
	public void loadBasePath() throws Merge500 {
		// Get the credentials
		String path = "";
		try {
			path = config.getEnv(source + ".PATH");
		} catch (MergeException e) {
			throw new Merge500("Malformed or Missing File Source Credentials found for:" + source + ":" + path);
		}

		basePath = new File(path);
        if (this.basePath.listFiles() == null) {
            throw new Merge500("File System Path Folder was not found:" + basePath.getAbsolutePath());
        }
	}

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		if (this.basePath == null) {
			loadBasePath();
		}
		Content query = new Content(wrapper, command, TagSegment.ENCODE_NONE);
		query.replace(replace, false, config.getNestLimit());
		DataObject result = new DataObject();

		String fileSelector = query.getValue();
        for (File file : this.basePath.listFiles()) {
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
        
		if (parseAs != Config.PARSE_NONE) {
			for (String fileName : result.keySet()) {
				DataElement element = config.parseString(parseAs, result.get(fileName).getAsPrimitive()); 
				result.put(fileName, element);
			}
		}

		return result;
	}
	
	public File getBasePath() {
		return this.basePath;
	}
	@Override
	public void close() {
		// nothing to close
		return;
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
