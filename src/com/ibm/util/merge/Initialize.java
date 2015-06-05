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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@WebServlet("/Initialize")
public class Initialize extends HttpServlet {
	private static final Logger log = Logger.getLogger( Initialize.class.getName() );
	private static final long serialVersionUID = 1L;

	/**
     * Initialize Logging, Template and Zip Factory objects 
     */
	public void init() {
		// Initialize Log4j
		String file = getInitParameter("log4j-init-file");
	    String prefix =  getServletContext().getRealPath("/");
	    if(file != null) {
	      PropertyConfigurator.configure(prefix+file);
	    }

	    // Initialize Zip-Factory (set output root folder)
		ZipFactory.setOutputroot(getInitParameter("merge-output-root"));

		// Initialize cache (Load JSON Persisted Templates)
		TemplateFactory.reset();
		TemplateFactory.setTemplateFolder(getInitParameter("merge-templates-folder"));
		TemplateFactory.setDbPersistance(getInitParameter("templates-persist").equals("Database"));
		try {
			TemplateFactory.loadAll();
		} catch (MergeException e) {
			log.warn("Factory Load All I/O Error, check web.xml for merge-templates-folder and templates-persist values");
		}

		// Initialize Template-Factory Hibernate objects
		TemplateFactory.initilizeHibernate();
	}

	/**
	 * @throws IOException getWriter failed  
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param req the Http Request object
	 * @param res the Http Response Object
	 */ 
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	// Get Status (Factory Sizes, ?Processing History?) 
    	// - Create a Static SystemStatus object and return JSON serialization?
    }

	/**
	 * @throws IOException getWriter failed
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param req the Http Request object
	 * @param res the Http Response Object
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    // Execute System Actions??? 
		// - Populate TemplateType table?
		// - Reset and Reload Template Cache?
	}
}
