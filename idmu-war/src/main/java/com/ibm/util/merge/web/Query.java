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
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;

/**
 * Ajax Get List service
 *
 * @author Mike Storey
 * @see TemplateFactory
 * @see Template
 */
@WebServlet("/Query")
public class Query extends HttpServlet {

    private RuntimeContext rtc;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        TemplateFactory tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates", new PrettyJsonProxy()));

        rtc = new RuntimeContext(tf);
        rtc.initialize("/tmp/merge");
    }

    /**
     * Servlet called as HTTP Get
     * - Parameter: list=Collections|Tempaltes
     *
     * @param req the Http Request object
     * @param res the Http Response Object
     * @throws IOException getWriter failed
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)  {

        // Create the response writer
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException("Could not getForBrowser response writer", e);
        }
        response.setContentType("text/json");
        String parm = request.getParameter("list");
        if (parm != null) {
            if (parm.equals("Collections")) {
                out.write(rtc.getTemplateFactory().getCollectionNamesJSON());
                out.close();
            }
            if (parm.equals("Templates")) {
                out.write(rtc.getTemplateFactory().getTemplateNamesJSON(request.getParameter("collection")));
                out.close();
            }
        }
    }

    /**
     * Save a new Template, provided as a JSON object. The JSON template is returned on success.
     *
     * @param req the Http Request object
     * @param res the Http Response Object
     * @throws IOException getWriter failed
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
    }
}