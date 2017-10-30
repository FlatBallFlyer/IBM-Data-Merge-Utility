package com.ibm.util.merge.data.parser;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.sax.XmlSaxHandlerData;
import com.ibm.util.merge.data.parser.sax.XmlSaxHandlerMarkup;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.ParseData;

public class Parser {
	private DataProxyCsv cson;
	private DataProxyHtml hson;
	private DataProxyJson gson;
	private DataProxyXml xmlData;
	private DataProxyXml xmlMakrup;
	
	public Parser() throws MergeException {
		cson = new DataProxyCsv();
		hson = new DataProxyHtml();
		gson = new DataProxyJson();
		xmlData = new DataProxyXml(new XmlSaxHandlerData());
		xmlMakrup = new DataProxyXml(new XmlSaxHandlerMarkup());
	}

	public DataElement parse(int i, String data) throws MergeException {
		DataElement element = null;
		switch (i) {
		case ParseData.PARSE_CSV :
			element = cson.fromCsv(data);
			break;
		case ParseData.PARSE_HTML :
			element = hson.fromHTML(data);
			break;
		case ParseData.PARSE_JSON :
			element = gson.fromJSON(data, DataElement.class);
			break;
		case ParseData.PARSE_XML_DATA :
			element = xmlData.fromXML(data);
			break;
		case ParseData.PARSE_XML_MARKUP :
			element = xmlMakrup.fromXML(data);
			break;
		}
		return element;
	}
}
