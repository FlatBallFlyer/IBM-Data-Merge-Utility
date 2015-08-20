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
package com.ibm.util.merge.web;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.handler.*;
import com.ibm.util.merge.web.rest.servlet.writer.TextResponseWriter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InitializeServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6542461667547308985L;
	private Logger log = Logger.getLogger(InitializeServlet.class);
	private static final String PARAMETER_LOGGING_PROPS 	= "log4j-init-fil";
	private static final String PARAMETER_SECURE_SERVER 	= "secure-server";
	private File idmuPropertiesFile;
	private Properties runtimeProperties = new Properties();
    private final List<RequestHandler> handlerChain = new ArrayList<>();

    public InitializeServlet() {
    	// provide default runtime parameters
    	runtimeProperties.setProperty(PARAMETER_LOGGING_PROPS, "/opt/ibm/idmu/properties/log4j.properties");
    	runtimeProperties.setProperty(PARAMETER_SECURE_SERVER, "no");
    	runtimeProperties.setProperty(TemplateFactory.PARAMETER_TEMPLATE_DIR, 		"/opt/ibm/idmu/templates");
    	runtimeProperties.setProperty(TemplateFactory.PARAMETER_OUTPUT_DIR, 		"/opt/ibm/idmu/output");
    	runtimeProperties.setProperty(TemplateFactory.PARAMETER_POOLS_PROPERTIES, 	"/opt/ibm/idmu/properties");
    	runtimeProperties.setProperty(TemplateFactory.PARAMETER_TEMPLATE_POOL, 		"idmuTemplates");
    	runtimeProperties.setProperty(TemplateFactory.PARAMETER_DB_PERSIST, 		"no");
    	runtimeProperties.setProperty(TemplateFactory.PARAMETER_PRETTY_JSON, 		"no");
    }

    /**
     * Initialize Servlet
     */
    @Override
    public void init(ServletConfig cfg) {
        initializeApp(cfg.getServletContext());
    }

    private void initializeApp(ServletContext servletContext) {
    	getRuntimeProperties();
	    PropertyConfigurator.configure(this.runtimeProperties.getProperty(PARAMETER_LOGGING_PROPS));
    	handlerChain.clear();
        handlerChain.addAll(createHandlerInstances());
        TemplateFactory tf = new TemplateFactory(this.runtimeProperties);
        servletContext.setAttribute("TemplateFactory", tf);
        for (RequestHandler handler : handlerChain) {
            log.info("Initializing handler " + handler.getClass().getName());
            handler.initialize(this.runtimeProperties, tf);
        }
        servletContext.setAttribute("handlerChain", handlerChain);
    }

    private void getRuntimeProperties() {
        String folder = getServletContext().getInitParameter("IDMU_ROOT");
        if (folder.isEmpty()) {folder = System.getProperty("IDMU_ROOT");}
    	if (folder.isEmpty()) {folder = "/opt/ibm/idmu";}
    	this.idmuPropertiesFile = new File(folder + File.pathSeparator + "properties" + File.pathSeparator + "idmu.properties");
    	if (! this.idmuPropertiesFile.exists() ) {
    		setup(folder);
    	}
    	try {
			this.runtimeProperties.load(new FileInputStream(this.idmuPropertiesFile));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load properties file " + this.idmuPropertiesFile);
		}
    }
    
    private void setup(String folder) {
    	ArrayList<String> folders = new ArrayList<String>();
    	folders.add("templates");
    	folders.add("packages");
    	folders.add("output");
    	folders.add("properties");
    	folders.add("logs");
    	folders.add("database");
    	for (String aFolder : folders) {
    		File theSource = new File("WEB-INF" + File.pathSeparator + aFolder);
    		File theTarget = new File(folder + File.pathSeparator + aFolder);
    		if (!theTarget.exists()) {theTarget.mkdirs();}
    		try {
				FileUtils.copyDirectory(theSource, theTarget);
			} catch (IOException e) {
				throw new RuntimeException("SETUP ERROR! - Unable to copy files from " + theSource + " to " + theTarget);
			}
    	}
    }
    
    private ArrayList<RequestHandler> createHandlerInstances() {
    	ArrayList<RequestHandler> thelist = new ArrayList<RequestHandler>();
		thelist.add(new GetCollectionsResourceHandler());
		thelist.add(new GetDirectivesResourceHandler());
		thelist.add(new GetTemplateNamesForCollectionResourceHandler());
		thelist.add(new GetTemplateNamesForNameResourceHandler());
		thelist.add(new GetTemplatePackageResourceHandler());
		thelist.add(new GetTemplateResourceHandler());
		thelist.add(new PerformMergeResourceHandler());
		thelist.add(new RemoveArchiveResourceHandler());
		thelist.add(new GetStatusResourceHandler());
		if (this.runtimeProperties.get(PARAMETER_SECURE_SERVER).equals("yes")) {
			thelist.add(new PutTemplateResourceHandler());
			thelist.add(new PutTemplatePackageResourceHandler());
	    	thelist.add(new DelTemplateResourceHandler());
			thelist.add(new DelTemplatePackageResourceHandler());
		}
        return thelist;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.getWriter().write("Initialized");
    }

    /**
     * Reinitializes IDMU upon POST to this servlet
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        initializeApp(req.getServletContext());
        new TextResponseWriter(res, "Successfully reinitialized IDMU.").write();
    }
}
