package com.ibm.util.merge;

import com.ibm.util.merge.storage.Archive;
import com.ibm.util.merge.storage.TarArchive;
import com.ibm.util.merge.storage.ZipArchive;
import com.ibm.util.merge.template.Template;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class MergeContext {
    private static final String DBROOT = "java:/comp/env/jdbc/";
    private static final Logger log = Logger.getLogger(MergeContext.class);
    private final TemplateFactory templateFactory;
    private HashMap<String, Connection> connections = new HashMap<String, Connection>();
    private Date initialized = null;
    private Boolean isZipFile = false;
    private Archive archive;
    private String archiveFileName = "";
    private String archiveChkSums = "";

    public MergeContext(TemplateFactory factory, HashMap<String,String> replace) {
        this.templateFactory = factory;

        // Determine output type
        if (replace.containsKey(Template.TAG_OUTPUT_TYPE)) {
        	this.isZipFile = replace.get(Template.TAG_OUTPUT_TYPE).equals(Template.TYPE_ZIP);
        }
        
        // Determine output Filename
        if (replace.containsKey(Template.TAG_OUTPUTFILE)) {
        	this.archiveFileName = replace.get(Template.TAG_OUTPUTFILE);
        } else {
        	this.archiveFileName = UUID.randomUUID().toString() + (this.isZipFile ? ".zip" : ".tar");
        	replace.put(Template.TAG_OUTPUTFILE, this.archiveFileName);
        }
        
        // Initialize Archive object
        if (this.isZipFile) {
        	this.archive = new ZipArchive(factory.getOutputRoot() + File.separator + this.archiveFileName);
        } else {
        	this.archive = new TarArchive(factory.getOutputRoot() + File.separator + this.archiveFileName);
        }
        
        initialized = new Date();
        log.info("Instantiated");
    }

    public void writeFile(String entryName, String content) throws IOException, MergeException {
        if (entryName.equals("/dev/null")) return;
        if (content.isEmpty()) return;
        
		// Providing blank values for User and Group attributes to use
    	String chksum = archive.writeFile(entryName, content, "", "");
    	setArchiveChkSums(getArchiveChkSums() + chksum + "\n");
    }
    
    public Connection getConnection(String dataSource) throws MergeException {
    	Connection theConnection;
    	if (this.connections.containsKey(dataSource)) {
    		theConnection = connections.get(dataSource);
    	} else {
			try {
				// TODO - reactor to use real connection pool and remove JNDI dependency
				Context initContext = new InitialContext();
				DataSource newSource = (DataSource) initContext.lookup(DBROOT + dataSource);
				theConnection = newSource.getConnection();
	    		connections.put(dataSource, theConnection);
			} catch (NamingException e) {
				throw new MergeException("Naming Exception getting new DataSource", DBROOT + dataSource);
			} catch (SQLException e) {
				throw new MergeException("SQL Exception getting new DataSource", DBROOT + dataSource);
			}
    	}
    	return theConnection;
	}
    		
    public void finalize() throws IOException, SQLException {
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

    public String getArchiveChkSums() {
		return archiveChkSums;
	}

	public void setArchiveChkSums(String archiveChkSums) {
		this.archiveChkSums = archiveChkSums;
	}

	/**
     * @param error
     * @return
     */
    public String getHtmlErrorMessage(MergeException error) {
        String message;
        Map<String, String[]> parameters = new HashMap<String,String[]>();
        parameters.put("MESSAGE", 		new String[]{error.getError()});
        parameters.put("CONTEXT", 		new String[]{error.getContext()});
        parameters.put("TRACE", 		new String[]{error.getStackTrace().toString()});
        parameters.put("DragonFlyFullName",	new String[]{"system.errHtml."});
        message = getTemplateFactory().getMergeOutput(parameters);
        if (message.isEmpty()) {
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
        Map<String, String[]> parameters = new HashMap<String,String[]>();
        parameters.put("MESSAGE", 		new String[]{error.getError()});
        parameters.put("CONTEXT", 		new String[]{error.getContext()});
        parameters.put("TRACE", 		new String[]{error.getStackTrace().toString()});
        parameters.put("DragonFlyFullName", new String[]{"system.errJson."});
        message = getTemplateFactory().getMergeOutput(parameters);
        if (message.isEmpty()) {
            message = "INVALID ERROR TEMPLATE! \n" +
                    "Message: " + error.getError() + "\n" +
                    "Context: " + error.getContext() + "\n";
        }
        return message;
    }

}