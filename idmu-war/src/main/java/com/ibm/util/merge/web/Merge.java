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

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.util.merge.*;
import org.apache.log4j.Logger;

/**
 * Servlet implementation - instantiates a template, merges the output and finalizes the output archive.
 *
 * @author Mike Storey
 * @see TemplateFactory
 * @see Template
 */
@WebServlet("/Merge")
public class Merge extends HttpServlet {
    private static final Logger log = Logger.getLogger(HttpServlet.class.getName());
    private TemplateFactory tf;
    private ZipFactory zf;
    private ConnectionFactory cf;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates"));
        zf = new ZipFactory();
        cf = new ConnectionFactory();
        Initialize.performInit(servletConfig, tf, zf);
    }

    /**
     * @param req the Http Request object
     * @param res the Http Response Object
     * @throws IOException getWriter failed
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            merge(req, res);
        } catch (MergeException e) {
            throw new RuntimeException("Merge error", e);
        }
    }

    /**
     * @param req the Http Request object
     * @param res the Http Response Object
     * @throws IOException getWriter failed
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            merge(req, res);
        } catch (MergeException e) {
            throw new RuntimeException("Merge error", e);
        }
    }

    /**
     * This is the Servlet to Template interface:
     * <ul>
     * <li>A template is retrieved from the TemplateFactory using the HTTP request constructor</li>
     * <li>The Template is Merged and written to the response</li>
     * <li>The ZIP file containing generated output files is Finalized</li>
     * </ul>
     *
     * @param request  the Http Request object
     * @param response the Http Response Object
     * @throws IOException    getWriter failed
     * @throws MergeException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void merge(HttpServletRequest request, HttpServletResponse response) throws IOException, MergeException {

        Template root = tf.getTemplate(request.getParameterMap());
        long start = System.currentTimeMillis();
        // Create the response writer
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        // Get a template using the httpServletRequest constructor
        // Perform the merge and write output
        String merged = root.merge(zf, tf, cf);
        out.write(merged);
        // Close Connections and Finalize Output
        root.packageOutput(zf, cf);
        long elapsed = System.currentTimeMillis() - start;
        log.warn(String.format("Merge completed in %d milliseconds", elapsed));
    }
}