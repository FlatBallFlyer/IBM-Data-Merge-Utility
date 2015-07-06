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

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/Initialize")
public class Initialize extends HttpServlet {
//	private RuntimeContext rtc;

	/**
     * Initialize Logging, Template and Zip Factory objects 
     */
	public void init(ServletConfig cfg) {
		String fullPath = cfg.getServletContext().getRealPath("/WEB-INF/templates");
//		TemplateFactory tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates", new PrettyJsonProxy()));
		PrettyJsonProxy jsonProxy = new PrettyJsonProxy();
		FilesystemPersistence fs = new FilesystemPersistence(fullPath, jsonProxy);
		TemplateFactory tf = new TemplateFactory(fs);


		RuntimeContext rtc = new RuntimeContext(tf);
		rtc.initialize("/tmp/merge");
		cfg.getServletContext().setAttribute("rtc", rtc);
	}

	/**
	 * @throws IOException getWriter failed  
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param req the Http Request object
	 * @param res the Http Response Object
	 */ 
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.getOutputStream().println("Initialized successfully.");
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
