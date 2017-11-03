package com.ibm.util.merge.data.parser;

import java.util.HashMap;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;

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

	private DataProxyCsv cson;
	private DataProxyHtml hson;
	private DataProxyJson gson;
	private DataProxyXmlStrict xmlStrict;
	
	public Parser() throws MergeException {
		cson = new DataProxyCsv();
		hson = new DataProxyHtml();
		gson = new DataProxyJson();
		xmlStrict = new DataProxyXmlStrict();
	}

	public DataElement parse(int i, String data) throws MergeException {
		return parse(i, data, "");
	}

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
