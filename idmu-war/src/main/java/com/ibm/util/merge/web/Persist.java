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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.TemplateFactory;

/**
 * JSON Get/Put Template Servlet
 *
 * @author Mike Storey
 * @see TemplateFactory
 * @see Template
 */
@WebServlet("/Template")
public class Persist extends HttpServlet {
    /**
     * Servlet called as HTTP Get
     * - Request contains JSON Template Object with optional Collection, Name and Column values
     * - An Empty request (No Collection, Name or Column) returns a list of all Collection Names
     * - A non-empty request returns a JSON List of Template Objects that match the provided values
     *
     * @param req the Http Request object
     * @param res the Http Response Object
     * @throws IOException getWriter failed
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = null;
        // Create the response writer
        response.setContentType("text/json");
        out = response.getWriter();
        out.write(TemplateFactory.getTemplateAsJson(request.getParameter("DragonFlyFullName")));
        out.close();
    }

    /**
     * Save a new Template, provided as a JSON object. The JSON template is returned on success.
     *
     * @param req the Http Request object
     * @param res the Http Response Object
     * @throws IOException getWriter failed
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = null;
        // Create the response writer
        response.setContentType("text/html");
        out = response.getWriter();
        String template = request.getParameter("template");
        out.write(TemplateFactory.saveTemplateFromJson(template));
        out.close();
    }
}