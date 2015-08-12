/*
 * Copyright 2015, 2015 IBM
 * 
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
package com.ibm.util.merge.persistence;

import com.google.gson.JsonSyntaxException;
import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.template.Template;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FilesystemPersistence implements TemplatePersistence {

    private Logger log = Logger.getLogger(FilesystemPersistence.class);
    private final File templateFolder;
    private final JsonProxy jsonProxy;

    public FilesystemPersistence(File templateFolder, JsonProxy jsonProxy) {
        if(templateFolder == null) throw new IllegalArgumentException("Missing templateFolder");
        if(jsonProxy == null) throw new IllegalArgumentException("Missing jsonProxy");
        this.templateFolder = templateFolder;
        this.jsonProxy = jsonProxy;
    }

    /**********************************************************************************
     * Cache JSON templates found in the template folder.
     * Note: The template folder is initialized from Merge.java from the web.xml value  for
     * merge-templates-folder, if it is not initilized the default value is /tmp/templates
     *
     * @param folder that contains template files
     */
	@Override
    public List<Template> loadAllTemplates() {
		List<Template> templates = new ArrayList<>();
        int count = 0;
        if (templateFolder.listFiles() == null) {
            throw new RuntimeException("Template Folder data was not found! " + templateFolder);
        }
        for (File file : templateFolder.listFiles()) {
            log.debug("Inspect potential template file: " + file);
            if (!file.isDirectory() && !file.getName().startsWith(".")) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()));
                    Template template = jsonProxy.fromJSON(json, Template.class);
                    templates.add(template);
                    log.info("Loaded template " + template.getFullName() + " : " + file.getAbsolutePath());
                    count++;
                } catch (JsonSyntaxException e) {
                    log.warn("Malformed JSON Template:" + file.getName(), e);
                } catch (FileNotFoundException e) {
                    log.info("Moving on after file read error on " + file.getName(), e);
                } catch (IOException e) {
                    log.warn("IOException Reading:" + file.getName(), e);
                }
            }


        }
        log.info("Loaded " + Integer.toString(count) + " templates from " + templateFolder);
        return templates;
    }

    /**********************************************************************************
     * save provided template to the Template Folder as JSON, and add it to the Cache
     *
     * @param Template template the Template to save
     * @return a cloned copy of the Template ready for Merge Processing
     */
	@Override
    public void saveTemplate(Template template) {
		deleteTemplate(template);
        String fileName = templateFolder + File.separator + template.getFullName() + ".json";
        File file = new File(fileName);
        BufferedWriter bw = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            File path = file.getAbsoluteFile();
            log.info("Saving " + template.getFullName() + " to "+ path.getAbsolutePath());
            FileWriter fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(jsonProxy.toJson(template));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write template " +template.getFullName()+" to JSON folder : " + file.getPath(), e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
        return;
    }

	@Override
    public void deleteTemplate(Template template) {
        String fileName = templateFolder + File.separator + template.getFullName() + ".json";
        File file = new File(fileName);
        if(file.exists()){
            if(!file.delete()){
                throw new RuntimeException("Could not delete template " + template.getFullName() + " at path " + file.getAbsolutePath());
            }else{
                log.info("Deleted template " + template.getFullName() + " at path " + file.getAbsolutePath());
            }
        }else{
            log.error("File for template " + template.getFullName() + " does not exist at path " + file.getAbsolutePath());
        }
    }

//    public static class TemplatePersistenceException extends RuntimeException {
//    	// TO DO Serial Version UID
//		private static final long serialVersionUID = 1L;
//		private final Template template;
//        private final File outputFilePath;
//        private final String archiveEntryName;
//
//        public TemplatePersistenceException(Template template, File outputFilePath, String archiveEntryName, IOException e) {
//            super("Error persisting template " + template.getFullName() + " to zip file at " + outputFilePath + " with entry name " + archiveEntryName, e);
//            this.template = template;
//            this.outputFilePath = outputFilePath;
//            this.archiveEntryName = archiveEntryName;
//        }
//
//        public Template getTemplate() {
//            return template;
//        }
//
//        public File getOutputFilePath() {
//            return outputFilePath;
//        }
//
//        public String getArchiveEntryName() {
//            return archiveEntryName;
//        }
//    }

//    public static class OutputDirectoryPathDoesNotExistException extends RuntimeException {
//    	// TO DO Serial Version UID
//		private static final long serialVersionUID = 1L;
//		private String outputDirPath;
//
//        public OutputDirectoryPathDoesNotExistException(String outputDirPath) {
//            super("The output directory does not exist: " + outputDirPath);
//            this.outputDirPath = outputDirPath;
//        }
//
//        public String getOutputDirPath() {
//            return outputDirPath;
//        }
//    }
//
//    public static class NonDirectoryAtOutputDirectoryPathException extends RuntimeException {
//    	// TO DO Serial Version UID
//		private static final long serialVersionUID = 1L;
//		private String outputDirPath;
//
//        public NonDirectoryAtOutputDirectoryPathException(String outputDirPath) {
//            super("Path does not denote a directory: " + outputDirPath);
//            this.outputDirPath = outputDirPath;
//        }
//
//        public String getOutputDirPath() {
//            return outputDirPath;
//        }
//    }

}
