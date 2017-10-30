package com.ibm.util.merge.template.directive.enrich.provider;

import org.apache.commons.io.IOUtils;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge403;
import com.ibm.util.merge.exception.Merge404;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.FileSystemSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class FileSystemProvider extends AbstractProvider {
	
	public FileSystemProvider(AbstractSource source) throws MergeException {
		super(source);
		this.setType(AbstractProvider.PROVIDER_FILE);
	}

	@Override
	public DataElement get(Template template) throws MergeException {
		FileSystemSource source = (FileSystemSource) this.getSource();
		File templateFolder = new File(source.getFileSystemPath());
        if (templateFolder.listFiles() == null) {
            throw new Merge500("File System Path Folder was not found:" + templateFolder);
        }
        
		HashMap<String, String> fileContents = new HashMap<String, String>();
		Content command = new Content(template.getWrapper(), this.getSource().getGetCommand(), TagSegment.ENCODE_NONE);
		command.replace(template.getReplaceStack(), false, Config.MAX_NEST);
		String fileSelector = command.getValue();

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

		DataList result = new DataList();
		for (String file : fileContents.keySet()) {
			if (source.getParseAs() == ParseData.PARSE_NONE) {
				result.add(new DataPrimitive(fileContents.get(file)));
			} else {
				result.add(parser.parse(this.getSource().getParseAs(), fileContents.get(file))); 
			}
		}
		return result;
	}
	
	@Override
	public void put(Template template) throws MergeException {
		FileSystemSource source = (FileSystemSource) this.getSource();
		String templateFolder = source.getFileSystemPath();
		String fileName = templateFolder.concat(File.separator).concat(template.getId().shorthand()).concat(".json");
		File file = new File(fileName);
		if (!file.exists()) throw new Merge404("Template Not Found:" + fileName);
		writeFile(file, template);
	}
	
	@Override
	public void post(Template template) throws MergeException {
		FileSystemSource source = (FileSystemSource) this.getSource();
		String templateFolder = source.getFileSystemPath();
		String fileName = templateFolder.concat(File.separator).concat(template.getId().shorthand()).concat(".json");
		File file = new File(fileName);
		if (file.exists()) throw new Merge403("Duplicate Template Found:" + fileName);
		writeFile(file, template);
	}

	private void writeFile(File file, Template template) throws MergeException {
		BufferedWriter bw = null;
        try {
    		if (!file.exists()) file.createNewFile();
            File path = file.getAbsoluteFile();
            FileWriter fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(proxy.toJson(template));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new Merge500("Could not write template " + template.getId().shorthand() +" to JSON folder : " + file.getPath() + ":" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(bw);
        }
		
	}
	
	@Override
	public void delete(Template template) throws MergeException {
		FileSystemSource source = (FileSystemSource) this.getSource();
		String templateFolder = source.getFileSystemPath();
		String fileName = templateFolder.concat(File.separator).concat(template.getId().shorthand()).concat(".json");
		File file = new File(fileName);
		if (!file.exists()) throw new Merge404("Template not found:" + fileName);
        if(!file.delete()){
            throw new Merge500("Could not delete template:" + fileName);
        }
    }

}
