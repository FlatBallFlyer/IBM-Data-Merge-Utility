package com.ibm.util.merge;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FilesystemPersistence {

    private Logger log = Logger.getLogger(FilesystemPersistence.class);

    private String templateFolder;

    public FilesystemPersistence(String templateFolder) {
        this.templateFolder = templateFolder;
    }

    /**********************************************************************************
     * Cache JSON templates found in the template folder.
     * Note: The template folder is initialized from Merge.java from the web.xml value  for
     * merge-templates-folder, if it is not initilized the default value is /tmp/templates
     *
     * @param folder that contains template files
     * @throws MergeException - Template Clone errors
     */
    public List<Template> loadAll() {
        List<Template> templates = new ArrayList<>();
        if (templateFolder == null || templateFolder.isEmpty()) {
            return templates;
        }
        int count = 0;
        File folder = new File(templateFolder);
        if (folder.listFiles() == null) {
            log.warn("Tempalte Folder data was not found! " + templateFolder);
            return templates;
        }
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                try {
                    String json = String.join("\n", Files.readAllLines(file.toPath()));
                    Template template = Template.fromJSON(json);
                    templates.add(template);
//                    cacheFromJson(json);
                } catch (JsonSyntaxException e) {
                    log.warn("Malformed JSON Template:" + file.getName(), e);
                } catch (FileNotFoundException e) {
                    log.info("Moving on after file read error on " + file.getName(), e);
                } catch (IOException e) {
                    log.warn("IOException Reading:" + file.getName(), e);
                }
            }
            count++;
        }
        log.warn("Loaded " + Integer.toString(count) + " templates from " + templateFolder);
        return templates;
    }

    /**********************************************************************************
     * save provided template to the Template Folder as JSON, and add it to the Cache
     *
     * @param Template template the Template to save
     * @return a cloned copy of the Template ready for Merge Processing
     * @throws MergeException on Template Clone Errors
     */
    public Template saveTemplateToJsonFolder(Template template) {
        String fileName = templateFolder + template.getFullName();
        File file = new File(fileName);
        BufferedWriter bw = null;
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write(template.asJson(true));
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write template " +template.getFullName()+" to JSON folder : " + file.getPath(), e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
        return template;
    }

    public void setTemplateFolder(String templateFolder) {
        this.templateFolder = templateFolder;
    }
}
