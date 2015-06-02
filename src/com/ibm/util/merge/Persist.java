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
package com.ibm.util.merge;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JSON Get/Put Template Servlet 
 * @see TemplateFactory
 * @see Template
 * @author  Mike Storey
 */
@WebServlet("/Template")
public class Persist extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
     * Default constructor. 
     */
    public Persist() {
    }

	/**
	 * Get a JSON formatted template. The template full name is specified as a request parameter.
	 * 
	 * @throws IOException getWriter failed  
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param req the Http Request object
	 * @param res the Http Response Object
	 */ 
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = null;
    	Template template;

		try {
			// Create the response writer
			response.setContentType("text/json");
			out = response.getWriter();
		} catch (IOException e) {
			@SuppressWarnings("unused")
			MergeException me = new MergeException(e, "IO Error Getting Servlet Printwriter", "Persist Servlet");
		}
    	
    	try {
			template = TemplateFactory.getTemplate(request);
    		out.write(template.asJson());
    		out.close();    		
		} catch (MergeException e) {
			out.write(e.getJsonErrorMessage());
		}
    }

	/**
	 * Save a new Template, provided as a JSON object. The JSON template is returned on success.
	 * 
	 * @throws IOException getWriter failed
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param req the Http Request object
	 * @param res the Http Response Object
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
    	Template template;

		try {
			// Create the response writer
			response.setContentType("text/html");
			out = response.getWriter();
		} catch (IOException e) {
			@SuppressWarnings("unused")
			MergeException me = new MergeException(e, "IO Error Getting Servlet Printwriter", "Persist Servlet");
		}
    	
    	try {
			template = TemplateFactory.saveTemplate(request.toString());
    		out.write(template.asJson());
    		out.close();    		
		} catch (MergeException e) {
			out.write(e.getJsonErrorMessage());
		}
	}

}