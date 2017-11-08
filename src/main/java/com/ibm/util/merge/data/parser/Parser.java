/*
 * 
 * Copyright 2015-2017 IBM
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
package com.ibm.util.merge.data.parser;

import java.util.HashMap;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;

/**
 * Provides an abstract Parser object for all parsing
 * 
 * @author Mike Storey
 *
 */
public class Parser {
	public static final int PARSE_CSV	= 1;
	public static final int PARSE_HTML	= 2;
	public static final int PARSE_JSON	= 3;
	public static final int PARSE_NONE	= 4;
	public static final int PARSE_XML	= 5;
	public static final HashMap<Integer, String> PARSE_OPTIONS() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(PARSE_CSV, 	"csv");
		values.put(PARSE_HTML, 	"html");
		values.put(PARSE_JSON, 	"json");
		values.put(PARSE_NONE, 	"none");
		values.put(PARSE_XML,	"xml");
		return values;
	}

	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		options.put("Parse Formats", PARSE_OPTIONS());
		return options;
	}
	
	private DataProxyCsv cson;
	private DataProxyHtml hson;
	private DataProxyJson gson;
	private DataProxyXmlStrict xmlStrict;
	
	/**
	 * Instantiates a Parser
	 * 
	 * @throws MergeException
	 */
	public Parser() throws MergeException {
		cson = new DataProxyCsv();
		hson = new DataProxyHtml();
		gson = new DataProxyJson();
		xmlStrict = new DataProxyXmlStrict();
	}

	/**
	 * Convience parse method that uses all default options
	 * 
	 * @param i
	 * @param data
	 * @return
	 * @throws MergeException
	 */
	public DataElement parse(int i, String data) throws MergeException {
		return parse(i, data, "");
	}

	/**
	 * Parse a string into a DataElement object
	 * 
	 * @param i
	 * @param data
	 * @param options
	 * @return
	 * @throws MergeException
	 */
	public DataElement parse(int i, String data, String options) throws MergeException {
		DataElement element = null;
		switch (i) {
		case PARSE_CSV :
			element = cson.fromCsv(data);
			break;
		case PARSE_HTML :
			element = hson.fromHTML(data, options);
			break;
		case PARSE_JSON :
			element = gson.fromJSON(data, DataElement.class);
			break;
		case PARSE_XML :
			element = xmlStrict.fromXML(data);
			break;
		}
		return element;
	}
}
