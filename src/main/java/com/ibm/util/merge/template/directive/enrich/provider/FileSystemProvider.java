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
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.Enrich;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

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
 */
public class FileSystemProvider implements ProviderInterface {
	private transient File basePath;
	private transient final String source;
	
	/**
	 * Provider Constructor
	 * @param source The source environment name
	 * @param parameter - ignored
	 */
	public FileSystemProvider(String source, String parameter) {
		this.source = source;
	}

	private void connect(Config config) throws Merge500 {
		// implements lazy connection
		if (this.basePath != null) return;
		
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
	public DataElement provide(Enrich context) throws MergeException {
		connect(context.getConfig());
		
		Content query = new Content(context.getTemplate().getWrapper(), context.getEnrichCommand(), TagSegment.ENCODE_NONE);
		query.replace(context.getTemplate().getReplaceStack(), false, context.getConfig().getNestLimit());
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
        
		if (context.getParseAs() != Config.PARSE_NONE) {
			for (String fileName : result.keySet()) {
				DataElement element = context.getConfig().parseString(
						context.getParseAs(), 
						result.get(fileName).getAsPrimitive(), 
						context.getParseOptions(),
						context.getTemplate()); 
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
	public ProviderMeta getMetaInfo() {
		return new ProviderMeta(
				"N/A",
				"The following environment variables are expected\n"
				+ "{SourceName}.PATH - The Path where files are to be read from.",
				"A Java RegEx file selector",
				"file content is parsed in the return object",
				"returns an object of <String, Primitive> if not parsed, and String, Element if parsed");
	}
	
}
