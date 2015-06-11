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

import org.apache.log4j.Logger;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;

/**
 * Servlet implementation - instantiates a template, merges the output and finalizes the output archive.
 * @see TemplateFactory
 * @see Template
 * @author  Mike Storey
 */
@WebServlet("/Merge")
public class Merge extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(HttpServlet.class.getName());

	/**
	 * @throws IOException getWriter failed  
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param req the Http Request object
	 * @param res the Http Response Object
	 */ 
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	merge(req, res);
    }

	/**
	 * @throws IOException getWriter failed
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param req the Http Request object
	 * @param res the Http Response Object
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    merge(req, res);
	}

	/**
	 * This is the Servlet to Template interface:
	 * <ul>
	 * 		<li>A template is retrieved from the TemplateFactory using the HTTP request constructor</li>
	 * 		<li>The Template is Merged and written to the response</li>
	 * 		<li>The ZIP file containing generated output files is Finalized</li>
	 * </ul>
	 * 
	 * @throws IOException getWriter failed
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request the Http Request object
	 * @param response the Http Response Object
	 * @throws MergeException 
	 */
	public void merge(HttpServletRequest request, HttpServletResponse response) {
		Template root = null;
		PrintWriter out = null;
		long start = System.currentTimeMillis();
		
		try {
			// Create the response writer
			response.setContentType("text/html");
			out = response.getWriter();
		} catch (IOException e) {
			@SuppressWarnings("unused")
			MergeException me = new MergeException(e, "IO Error Getting Servlet Printwriter", "Merge Servlet");
		}
		
		try {
			// Get a template using the httpServletRequest constructor
			root = TemplateFactory.getTemplate(request.getParameterMap());
			// Perform the merge and write output
			out.write(root.merge());

			// Close Connections and Finalize Output
			root.packageOutput();
			long elapsed = System.currentTimeMillis() - start;
			log.warn(String.format("Merge completed in %d milliseconds", elapsed));
		} catch (MergeException e) {
			out.write(e.getHtmlErrorMessage());
		}
	}
}