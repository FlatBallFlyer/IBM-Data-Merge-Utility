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

import com.ibm.idmu.api.JsonProxy;
import com.ibm.idmu.api.PoolManagerConfiguration;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.*;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.handler.*;
import com.ibm.util.merge.web.rest.servlet.writer.TextResponseWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InitializeServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6542461667547308985L;
	private Logger log = Logger.getLogger(InitializeServlet.class);
    private String warTemplatesPath = "/WEB-INF/templates";
    private String templatesPersistencePoolName = "idmuTemplates";
    private Boolean dbPersist = false;
    private Boolean prettyJson = false;
    private Boolean secure = false;
    private String jdbcPoolsPropertiesPath = "/WEB-INF/properties/databasePools.properties";
    private String log4jPropertiesPath = "/WEB-INF/properties/log4j.properties";
    private File outputDirPath = new File("/tmp/merge");
    private final List<RequestHandler> handlerChain = new ArrayList<>();
    private Map<String, String> servletInitParameters;

    public InitializeServlet() {
        if(!outputDirPath.exists()){
            outputDirPath.mkdirs();
        }
    }

    /**
     * Initialize Logging, Template and Zip Factory objects
     */
    @Override
    public void init(ServletConfig cfg) {
        servletInitParameters = RestServlet.initParametersToMap(cfg);
        applyParameters();
        initializeApp(cfg.getServletContext());
    }

    private void initializeApp(ServletContext servletContext) {
        handlerChain.clear();
        handlerChain.addAll(createHandlerInstances());
        String log4jTruePath = jdbcPoolsPropertiesPath.indexOf("/WEB-INF") == 0 ? servletContext.getRealPath(log4jPropertiesPath) : log4jPropertiesPath;
	    PropertyConfigurator.configure(log4jTruePath);
        JsonProxy jsonProxy = (this.prettyJson ? new PrettyJsonProxy() : new DefaultJsonProxy());
        File templatesDirPath = warTemplatesPath.indexOf("/WEB-INF") == 0 ? new File(servletContext.getRealPath(warTemplatesPath)) : new File(warTemplatesPath);
        File poolsPropertiesPath = jdbcPoolsPropertiesPath.indexOf("/WEB-INF") == 0 ? new File(servletContext.getRealPath(jdbcPoolsPropertiesPath)) : new File(jdbcPoolsPropertiesPath);
        ConnectionPoolManager poolManager = new ConnectionPoolManager();
        if(poolsPropertiesPath.exists()){
            PoolManagerConfiguration config = PoolManagerConfiguration.fromPropertiesFile(poolsPropertiesPath);
            poolManager.applyConfig(config);
        }else{
            log.error("Could not load databasePools properties file from non-existant path: " + poolsPropertiesPath);
            log.error("No database config will be applied");
        }

        final TemplatePersistence persist;
        if(dbPersist){
            JdbcTemplatePersistence jdbcPersistence = new JdbcTemplatePersistence(poolManager);
            jdbcPersistence.setPoolName(templatesPersistencePoolName);
            persist = jdbcPersistence;
        }else{
            FilesystemPersistence filesystemPersistence = new FilesystemPersistence(templatesDirPath, jsonProxy);
            persist = filesystemPersistence;
        }
        TemplateFactory tf = new TemplateFactory(persist, jsonProxy, outputDirPath, poolManager);
        servletContext.setAttribute("TemplateFactory", tf);
        for (RequestHandler handler : handlerChain) {
            log.info("Initializing handler " + handler.getClass().getName());
            handler.initialize(servletInitParameters, tf);
        }
        servletContext.setAttribute("handlerChain", handlerChain);
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
		if (!this.secure) {
			thelist.add(new PutTemplateResourceHandler());
			thelist.add(new PutTemplatePackageResourceHandler());
	    	thelist.add(new DelTemplateResourceHandler());
			thelist.add(new DelTemplatePackageResourceHandler());
		}
        return thelist;
    }

    private void applyParameters() {
        applyInitParameters();
        applySystemParameters();
    }

    private void applySystemParameters() {
        String systemMergeTemplatesFolder = System.getProperty("merge-templates-folder");
        if(systemMergeTemplatesFolder != null){
            log.info("Found so using passed system property value for merge-templates-folder: " + systemMergeTemplatesFolder);
            this.warTemplatesPath = systemMergeTemplatesFolder;
        }
        String systemOutputRootDir = System.getProperty("merge-output-root");
        if(systemOutputRootDir != null){
            log.info("Found so using passed system property value for merge-output-root: " + systemOutputRootDir);
            this.outputDirPath = new File(systemOutputRootDir);
        }
        String systemPoolsPropertiesPath = System.getProperty("jdbc-pools-properties-path");
        if(systemPoolsPropertiesPath != null){
            log.info("Found so using passed system property value for jdbc-pools-properties-path: " + systemPoolsPropertiesPath);
            this.jdbcPoolsPropertiesPath = systemPoolsPropertiesPath;
        }
        String systemLog4jPropertiesPath = System.getProperty("log4j-init-file");
        if(systemLog4jPropertiesPath != null){
            log.info("Found so using passed system property value for log4j-init-file: " + systemLog4jPropertiesPath);
            this.log4jPropertiesPath = systemLog4jPropertiesPath;
        }
        String systemTemplatesPersistencePoolName = System.getProperty("jdbc-persistence-templates-poolname");
        if(systemTemplatesPersistencePoolName != null){
            log.info("Found so using passed system property value for jdbc-persistence-templates-poolname: " + systemTemplatesPersistencePoolName);
            this.templatesPersistencePoolName = systemTemplatesPersistencePoolName;
        }
        String systemPrettyJson = System.getProperty("pretty-json");
        if(systemPrettyJson != null){
            log.info("Found so using passed system property value for pretty-json: " + systemPrettyJson);
            this.prettyJson = systemPrettyJson.equals("yes"); 
        }
        String systemDbPersist = System.getProperty("db-persist");
        if(systemDbPersist != null){
            log.info("Found so using passed system property value for db-persist: " + systemDbPersist);
            this.dbPersist = systemDbPersist.equals("yes"); 
        }
        String systemSecure = System.getProperty("secure-server");
        if(systemSecure != null){
            log.info("Found so using passed system property value for secure-server: " + systemSecure);
            this.secure = systemSecure.equals("yes"); 
        }
    }

    private void applyInitParameters() {
        String mergeTemplatesFolder = servletInitParameters.get("merge-templates-folder");
        if (mergeTemplatesFolder != null) {
            log.info("Setting from ServletConfig: warTemplatesPath=" + mergeTemplatesFolder);
            warTemplatesPath = mergeTemplatesFolder;
        }
        String outputRootDir = servletInitParameters.get("merge-output-root");
        if (outputRootDir != null) {
            log.info("Setting from ServletConfig: outputDirPath=" + outputRootDir);
            outputDirPath = new File(outputRootDir);
        }
        String databasePoolsPropertiesPath = servletInitParameters.get("jdbc-pools-properties-path");
        if(databasePoolsPropertiesPath != null){
            log.info("Setting from ServletConfig: databasePoolsPropertiesPath=" + databasePoolsPropertiesPath);
            this.jdbcPoolsPropertiesPath = databasePoolsPropertiesPath;
        }
        String log4jPropertiesPath = servletInitParameters.get("log4j-init-file");
        if(log4jPropertiesPath != null){
            log.info("Setting from ServletConfig: log4jPropertiesPath=" + log4jPropertiesPath);
            this.log4jPropertiesPath = log4jPropertiesPath;
        }
        String templatesPersistencePoolName = servletInitParameters.get("jdbc-persistence-templates-poolname");
        if(templatesPersistencePoolName != null){
            log.info("Setting from ServletConfig templatesPersistencePoolName=" + templatesPersistencePoolName);
            this.templatesPersistencePoolName = templatesPersistencePoolName;
        }
        String prettyJsonParameter = servletInitParameters.get("pretty-json");
        if (prettyJsonParameter != null) {
            log.info("Setting from ServletConfig: prettyJson=" + prettyJsonParameter);
            this.prettyJson = prettyJsonParameter.equals("yes");
        }
        String dbPersistParameter = servletInitParameters.get("db-persist");
        if (dbPersistParameter != null) {
            log.info("Setting from ServletConfig: dbPersist=" + dbPersistParameter);
            this.dbPersist = dbPersistParameter.equals("yes");
        }
        String secureServer = servletInitParameters.get("secure-server");
        if (secureServer != null) {
            log.info("Setting from ServletConfig: secure-server=" + secureServer);
            this.secure = secureServer.equals("yes");
        }
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
