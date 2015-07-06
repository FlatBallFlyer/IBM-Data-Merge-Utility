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
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.util.merge.*;

@WebServlet("/Initialize")
public class Initialize extends HttpServlet {
	private RuntimeContext rtc;

	public static String getConfig(String paramName, ServletConfig servletConfig){
		return servletConfig.getInitParameter(paramName);
	}

	/**
     * Initialize Logging, Template and Zip Factory objects 
     */
	public void init(ServletConfig cfg) {

		TemplateFactory tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates"));

		// Initialize Log4j
		rtc = new RuntimeContext(tf, new ZipFactory());
		rtc.initialize("/tmp/merge");
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
