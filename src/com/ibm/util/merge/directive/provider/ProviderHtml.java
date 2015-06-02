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
package com.ibm.util.merge.directive.provider;

import com.ibm.util.merge.MergeException;

public class ProviderHtml extends ProviderHttp {
	public ProviderHtml() {
		super();
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderHtml clone() throws CloneNotSupportedException {
		return (ProviderHtml) super.clone();
	}

	/**
	 * Retrieve the data (superclass HTTP Provider) and parse the CSV data
	 */
	public void getData() throws MergeException {
		super.getData();
		
		// TODO Parse the Data
		// HtmlParser parser = new HtmlParser(parms);
		// parser.parse(this.textData);
		
		// Find <table> elements
		// for each (find) {
		// 	
		// for (int r = 1; r<rows(); r++) {
		//	ArrayList<String> row = new ArrayList<String>();
		//	theData.add(row);
		// 	for (int c = 1; c<cols(); c++) {
		//		row.add(data(r,c));
		//	}

		// Parse HTML
		// Find <table> element(s)
		// Add data to this.theData and this.columnNames
	}

}
