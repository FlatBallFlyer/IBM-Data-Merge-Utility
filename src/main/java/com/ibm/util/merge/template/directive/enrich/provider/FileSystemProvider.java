package com.ibm.util.merge.template.directive.enrich.provider;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
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

public class FileSystemProvider extends AbstractProvider {
	
	public FileSystemProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);
	}

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws Merge500 {
		Content query = new Content(wrapper, enrichCommand, TagSegment.ENCODE_NONE);
		query.replace(replace, false, context.getConfig().getNestLimit());
		DataList result = new DataList();

		File templateFolder = new File(this.getSource());
        if (templateFolder.listFiles() == null) {
            throw new Merge500("File System Path Folder was not found:" + templateFolder);
        }
        
		HashMap<String, String> fileContents = new HashMap<String, String>();
		String fileSelector = query.getValue();

        for (File file : templateFolder.listFiles()) {
            if (!file.isDirectory() && !file.getName().startsWith(".")) {
            	if (file.getName().matches(fileSelector.toString())) {
            		try {
            			String content = new String(Files.readAllBytes(file.toPath()));
            			fileContents.put(file.getName(), content);
            		} catch (FileNotFoundException e) {
            			// I Don't Think this can happen!
            		} catch (IOException e) {
            			// File Provider ignores files with i/o exceptions
            		}
            	}
            }
        }

		for (String file : fileContents.keySet()) {
			result.add(new DataPrimitive(fileContents.get(file)));
		}
		return result;
	}
	
}
