/*
 * Copyright 2015 IBM
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

package com.ibm.tk;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Merge
 */
@WebServlet("/Merge")
public class Merge extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Merge() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	merge(req, res);
    }

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    merge(req, res);
	}

	/**
	 * @throws IOException - getWriter failed
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void merge(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();			
		
		// Initialize initial replace hash map
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("{collection}", "root");
		replace.put("{column}","");
		replace.put("{name}", "default");
		

		// Testing Values for test template
		//replace.put("{collection}", "test");
		//replace.put("{name}", "testRoot");
		
		// Iterate parameters, setting replace values or loging levels
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String paramValue = request.getParameterValues(paramName)[0];
			if ("CacheReset".equals(paramName) && "Yes".equals(paramValue) ) {
				TemplateFactory.reset();
			} else {
				replace.put("{" + paramName + "}", paramValue);
			}
		}
				
		// Do the Merge and Handle all Exceptions
		try {
			// Get a template from the factory
			Template root = TemplateFactory.getTemplate(replace.get("{collection}"), replace.get("{column}"), replace.get("{name}"));
			
			// Add replace values from the parameter list
			root.getReplaceValues().putAll(replace);
			
			// Perform the merge and write output
			out.write(root.merge());
			
		} catch (tkException e) {
			out.write("MERGE FAILED! tkException " + e.getErrorCode() + "\n");
			out.write(e.getMessage() + "\n");
			e.printStackTrace(out);
		} catch (tkSqlException e) {
			out.write("MERGE FAILED! SQLException" + e.getErrorCode() + "\n");
			out.write(" - QueryString:" + e.getQueryString() + "\n");
			out.write(" - SQL Error:" + e.getSqlError() + "\n");
			out.write(e.getMessage() + "\n");
			e.printStackTrace(out);
		} catch (IOException e) {
			out.write("MERGE FAILED! IOException"  + "\n");
			out.write(e.getMessage());
			e.printStackTrace(out);
		}
	}
}