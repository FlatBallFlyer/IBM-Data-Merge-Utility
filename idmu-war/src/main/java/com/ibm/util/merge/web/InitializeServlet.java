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

import com.ibm.idmu.api.PoolManagerConfiguration;
import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.handler.*;
import com.ibm.util.merge.web.rest.servlet.writer.TextResponseWriter;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class InitializeServlet extends HttpServlet {
    private Logger log = Logger.getLogger(InitializeServlet.class);
    private String warTemplatesPath = "/WEB-INF/templates";
    private String jdbcPoolsPropertiesPath = "/WEB-INF/properties/databasePools.properties";
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
        applyInitParameters();
        initializeApp(cfg.getServletContext());
    }

    private void initializeApp(ServletContext servletContext) {
        handlerChain.clear();
        handlerChain.addAll(createHandlerInstances());
        File templatesDirPath = warTemplatesPath.indexOf("/WEB-INF") == 0 ? new File(servletContext.getRealPath(warTemplatesPath)) : new File(warTemplatesPath);
        File poolsPropertiesPath = jdbcPoolsPropertiesPath.indexOf("/WEB-INF") == 0 ? new File(servletContext.getRealPath(jdbcPoolsPropertiesPath)) : new File(jdbcPoolsPropertiesPath);
        PrettyJsonProxy jsonProxy = new PrettyJsonProxy();
        FilesystemPersistence fs = new FilesystemPersistence(templatesDirPath, jsonProxy, outputDirPath);
        TemplateFactory tf = new TemplateFactory(fs);
        ConnectionPoolManager poolManager = new ConnectionPoolManager();
        PoolManagerConfiguration config = PoolManagerConfiguration.fromPropertiesFile(poolsPropertiesPath);
        poolManager.applyConfig(config);
        RuntimeContext rtc = new RuntimeContext(tf, poolManager);
        rtc.initialize();
        servletContext.setAttribute("rtc", rtc);
        for (RequestHandler handler : handlerChain) {
            log.info("Initializing handler " + handler.getClass().getName());
            handler.initialize(servletInitParameters, rtc);
        }
        servletContext.setAttribute("handlerChain", handlerChain);
    }

    private ArrayList<RequestHandler> createHandlerInstances() {
        return new ArrayList<>(Arrays.asList(
                new AllTemplatesResourceHandler(),
                new AllDirectivesResourceHandler(),
                new CollectionTemplatesResourceHandler(),
                new CollectionTemplatesForTypeResourceHandler(),
                new CollectionForCollectionTypeColumnValueResourceHandler(),
                new TemplateForFullnameResourceHandler(),
//                new CreateTemplateResourceHandler(),
                new UpdateTemplateResourceHandler(),
                new PerformMergeResourceHandler(),
                new DeleteTemplateResourceHandler()
        ));
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
            this.jdbcPoolsPropertiesPath = databasePoolsPropertiesPath;
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String textResult = "Last initialized successfully at " + RestServlet.findRuntimeContext(req.getServletContext()).getInitialized() + "\n" +
                "Make a POST request to " + req.getRequestURL() + " to reinitialize and reload templates";
        new TextResponseWriter(res, textResult).write();
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
