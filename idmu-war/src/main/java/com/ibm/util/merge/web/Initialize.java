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
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.FilesystemPersistence;
import org.apache.log4j.Logger;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.ZipFactory;

@WebServlet("/Initialize")
public class Initialize extends HttpServlet {
	private static final Logger log = Logger.getLogger( Initialize.class.getName() );
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;

	public static String getConfig(String paramName, ServletConfig servletConfig){
		return servletConfig.getInitParameter(paramName);
	}

	/**
     * Initialize Logging, Template and Zip Factory objects 
     */
	public void init(ServletConfig cfg) {

		tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates"));

		zf = new ZipFactory();
		cf = new ConnectionFactory();
		// Initialize Log4j
		performInit(cfg, tf, zf);
	}

	public static void performInit(ServletConfig cfg, TemplateFactory tf, ZipFactory zf) {

		Enumeration<String> initParameterNames = cfg.getInitParameterNames();
		while (initParameterNames.hasMoreElements()){
			String paramName = initParameterNames.nextElement();
			log.info("init param " + paramName + "=" + cfg.getInitParameter(paramName));
		}
//		String file = getConfig("log4j-init-file", cfg);
//	    String prefix =  getServletContext().getRealPath("/");
//	    if(file != null) {
//			BasicConfigurator.configure();
//	      PropertyConfigurator.configure(prefix+file);
//	    }
		// Initialize Zip-Factory (set output root folder)
//		ZipFactory.setOutputroot(getConfig("merge-output-root", cfg));
		zf.setOutputroot("/tmp/merge");
		// Initialize cache (Load JSON Persisted Templates)
//		try {
		String tempalteFolder = getConfig("merge-templates-folder", cfg);
//			TemplateFactory.setTemplateFolder(prefix + tempalteFolder);
		String paramTemplatesPersist = getConfig("templates-persist", cfg);
//			boolean databasePersistenceEnabled = paramTemplatesPersist.equals("Database");
		boolean databasePersistenceEnabled = false;
		tf.setDbPersistance(databasePersistenceEnabled);
		tf.loadTemplatesFromFilesystem();
		//		} catch (MergeException e) {
//			throw new RuntimeException("Factory Load All I/O Error, check web.xml for merge-templates-folder and templates-persist values", e);
//		}
		// Initialize Template-Factory Hibernate objects
		tf.initilizeHibernate();
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
