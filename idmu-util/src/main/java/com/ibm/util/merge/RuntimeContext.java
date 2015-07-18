package com.ibm.util.merge;

import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.storage.Archive;
import com.ibm.util.merge.storage.TarArchive;
import com.ibm.util.merge.storage.ZipArchive;
import com.ibm.util.merge.template.Template;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class RuntimeContext {
    private static final Logger log = Logger.getLogger(RuntimeContext.class);
    private final TemplateFactory templateFactory;
    private HashMap<String, Connection> connections = new HashMap<String, Connection>();
    private final ConnectionFactory connectionFactory;
    private Date initialized = null;
    private Boolean isZipFile = false;
    private Archive archive;
    private String archiveFileName = "";

    public RuntimeContext(TemplateFactory templateFactory, ConnectionPoolManager poolManager, HashMap<String,String> replace) {
        this.templateFactory = templateFactory;
        connectionFactory = new ConnectionFactory(poolManager);

        // Determine output type
        if (replace.containsKey(Template.TAG_OUTPUT_TYPE)) {
        	this.isZipFile = replace.get(Template.TAG_OUTPUT_TYPE).equals(Template.TYPE_ZIP);
        }
        
        // Determine output Filename
        if (replace.containsKey(Template.TAG_OUTPUTFILE)) {
        	this.archiveFileName = replace.get(Template.TAG_OUTPUTFILE);
        } else {
        	this.archiveFileName = UUID.randomUUID().toString() + (this.isZipFile ? ".zip" : ".tar");
        	replace.put(Template.TAG_OUTPUT_TYPE, this.archiveFileName);
        }
        
        // Initialize Archive
        if (this.isZipFile) {
        	this.archive = new ZipArchive(this.archiveFileName);
        } else {
        	this.archive = new TarArchive(this.archiveFileName);
        }
        
        initialized = new Date();
        log.info("Instantiated");
    }

    public void writeFile(String entryName, String content) {
        if (entryName.equals("/dev/null")) return;
        if (entryName.isEmpty()) return;
        
		// TODO: Determine User and Group attributes to use
    	archive.writeFile(entryName, content, "", "");
    }
    
    public Connection getConnection(String dataSource) {
    	if (this.connections.containsKey(dataSource)) {
    		return connections.get(dataSource);
    	} else {
    		// TODO: Get Connection
    		Connection connection = null;
    		connections.put(dataSource, connection);
    	}
    }
    
    public void finalize() {
    	archive.closeOutputStream();
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            entry.getValue().close();
        }
    }

    public Date getInitialized() {
        return initialized;
    }

    public TemplateFactory getTemplateFactory() {
        return templateFactory;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
    
    /**
     * @param error
     * @return
     */
    public String getHtmlErrorMessage(MergeException error) {
        String message;
        Template errorTemplate;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Template.wrap("MESSAGE"), error.getError());
        parameters.put(Template.wrap("CONTEXT"), error.getContext());
        parameters.put(Template.wrap("TRACE"), error.getStackTrace().toString());
        try {
            errorTemplate = getTemplateFactory().getTemplate("system.errHtml." + error.getErrorFromClass(), "system.errHtml.", parameters);
            errorTemplate.merge(this);
            final String returnValue;
            if (!errorTemplate.canWrite()) {
                returnValue = "";
            } else {
                returnValue = errorTemplate.getContent();
            }
            getTemplateFactory().getFs().doWrite(errorTemplate);
            message = returnValue;
        } catch (MergeException e) {
            message = "INVALID ERROR TEMPLATE! \n" +
                    "Message: " + error.getError() + "\n" +
                    "Context: " + error.getContext() + "\n";
        }
        return message;
    }

    /**
     * @param error
     * @param throwable
     * @return
     */
    public String getJsonErrorMessage(MergeException error) {
        String message;
        Template errorTemplate;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Template.wrap("MESSAGE"), error.getError());
        parameters.put(Template.wrap("CONTEXT"), error.getContext());
        parameters.put(Template.wrap("TRACE"), error.getStackTrace().toString());
        try {
            errorTemplate = getTemplateFactory().getTemplate("system.errJson." + error.getErrorFromClass(), "system.errJson.", parameters);
            errorTemplate.merge(this);
            final String returnValue;
            if (!errorTemplate.canWrite()) {
                returnValue = "";
            } else {
                returnValue = errorTemplate.getContent();
            }
            getTemplateFactory().getFs().doWrite(errorTemplate);
//			errorTemplate.doWrite(rtc.getZipFactory());
            message = returnValue;
//			errorTemplate.packageOutput(zf, cf);
        } catch (MergeException e) {
            message = "INVALID ERROR TEMPLATE! \n" +
                    "Message: " + error.getError() + "\n" +
                    "Context: " + error.getContext() + "\n";
        }
        return message;
    }
}
