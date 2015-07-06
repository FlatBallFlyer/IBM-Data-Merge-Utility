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

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.RestServlet;
import com.ibm.util.merge.web.rest.servlet.handler.*;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Initialize extends HttpServlet {

    private Logger log = Logger.getLogger(Initialize.class);

    private String warTemplatesPath = "/WEB-INF/templates";
    private String outputDirPath = "/tmp/merge";
    private List<RequestHandler> handlerChain = new ArrayList<>(Arrays.asList(new AllTemplatesResourceHandler(), new AllDirectivesResourceHandler(), new CollectionTemplatesResourceHandler(), new CollectionTemplatesForTypeResourceHandler(), new CollectionForCollectionTypeColumnValueResourceHandler(), new TemplateForFullnameResourceHandler()));

    /**
     * Initialize Logging, Template and Zip Factory objects
     */
    public void init(ServletConfig cfg) {
        applyInitParameters(cfg);
        String fullPath = cfg.getServletContext().getRealPath(warTemplatesPath);
        PrettyJsonProxy jsonProxy = new PrettyJsonProxy();
        FilesystemPersistence fs = new FilesystemPersistence(fullPath, jsonProxy);
        TemplateFactory tf = new TemplateFactory(fs);
        RuntimeContext rtc = new RuntimeContext(tf);
        rtc.initialize(outputDirPath);
        cfg.getServletContext().setAttribute("rtc", rtc);
        for (RequestHandler handler : handlerChain) {
            log.info("Initializing handler " + handler.getClass().getName());
            handler.initialize(cfg, rtc);
        }
        cfg.getServletContext().setAttribute("handlerChain", handlerChain);

    }

    private void applyInitParameters(ServletConfig cfg) {
        String mergeTemplatesFolder = cfg.getInitParameter("merge-templates-folder");
        if(mergeTemplatesFolder != null){
            warTemplatesPath = mergeTemplatesFolder;
        }
        String outputRootDir = cfg.getInitParameter("merge-output-root");
        if(outputRootDir != null){
            outputDirPath = outputRootDir;
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String textResult = "Last initialized successfully at " + RestServlet.findRuntimeContext(req.getServletContext()).getInitialized() + "\n" +
                "Make a POST request to " + req.getRequestURL() + " to reinitialize and reload templates";
        writeTextResult(res, textResult);
        // TODO: Get System Status (Factory Sizes, ?Processing History?)
    }

    private void writeTextResult(HttpServletResponse res, String textResult) throws IOException {
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        res.getOutputStream().println(textResult);
    }

    /**
     * Reinitializes IDMU upon POST to this servlet
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        RuntimeContext rtc = RestServlet.findRuntimeContext(req.getServletContext());
        rtc.initialize(outputDirPath);
        writeTextResult(res, "Successfully reinitialized IDMU.");
        // TODO Execute System Actions :  Populate TemplateType table?, Reset and Reload Template Cache?
    }
}
