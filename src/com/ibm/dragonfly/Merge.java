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

package com.ibm.dragonfly;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation - instantiates a template, merges the output and finalizes the output archive.
 * @see TemplateFactory
 * @see Template
 */
@WebServlet("/Merge")
public class Merge extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(HttpServlet.class.getName());

	/**
     * Default constructor. 
     */
    public Merge() {
    }

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
	 * This is the Servlet - Template interface:
	 * <ul>
	 * 		<li>A template is retrieved from the TemplateFactory</li>
	 * 		<li>The Template is Merged and written to the response</li>
	 * 		<li>The ZIP file containing generated documents is Finalized</li>
	 * </ul>
	 * 
	 * @throws IOException getWriter failed
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request the Http Request object
	 * @param response the Http Response Object
	 */
	public void merge(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Create the response object
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();			

		// Do the Merge and Handle all Exceptions
		try {
			// Get a template using the httpServletRequest constructor
			Template root = TemplateFactory.getTemplate(request);
			
			// Perform the merge and write output
			out.write(root.merge());
			
			// Finalize ZIP file for any additional generated output files
			root.packageOutput(); 
			
		} catch (DragonFlyException e) {
			log.fatal("DragonFlyException: " + e.getErrorCode());
			out.write("<html><head></head><body><h1>DragonFly Exception - MERGE FAILED!</h1>");
			out.write("<p>DragonFLy Exception: " + e.getErrorCode() + "</p>");
			out.write("<p>Message: " + e.getMessage() + "</p>");
			out.write("<p>StackTrace: <textarea width=80 height=24>");
			e.printStackTrace(out);
			out.write("</textarea></p></body></html>");
		} catch (DragonFlySqlException e) {
			log.fatal("DragonFlySqlException: " + e.getErrorCode() + ":" + e.getQueryString());
			out.write("<html><head></head><body><h1>SQL Exception - MERGE FAILED!</h1>");
			out.write("<p>SQL Exception: " + e.getErrorCode() + "</p>");
			out.write("<p>Message: " + e.getMessage() + "</p>");
			out.write("<p>QueryString: " + e.getQueryString() + "</p>");
			out.write("<p>SQL Error:" + e.getSqlError() + "</p>");
			out.write("<p>StackTrace: <textarea width=80 height=24>");
			e.printStackTrace(out);
			out.write("</textarea></p></body></html>");
		} catch (IOException e) {
			log.fatal("DragonFlySqlException: " + e.getMessage() );
			out.write("<html><head></head><body><h1>IO Exception - MERGE FAILED!</h1>");
			out.write("<p>Message: " + e.getMessage() + "</p>");
			out.write("<p>StackTrace: <textarea width=80 height=24>");
			e.printStackTrace(out);
			out.write("</textarea></p></body></html>");
		}
	}
}