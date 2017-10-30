package com.ibm.util.merge.data.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.sax.XmlSaxHandler;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;


public class DataProxyXml {
	XmlSaxHandler saxHandler;
	
	public DataProxyXml(XmlSaxHandler saxHandler) {
		this.saxHandler = saxHandler;
	}

    public DataElement fromXML(String xmlString) throws MergeException {
    	try {
	    	SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setNamespaceAware(true);
	        SAXParser saxParser;
			saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(saxHandler);
			xmlReader.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes("UTF-8"))));
    	} catch (ParserConfigurationException e) {
    		throw new Merge500("XML Parser Configuration Error:" + e.getMessage());
    	} catch (SAXException e) {
    		throw new Merge500("SAX Exception:" + e.getMessage());
    	} catch (IOException e) {
    		throw new Merge500("SAX I/O Exception:" + e.getMessage());
		}        
        return saxHandler.getDataElement();
    }

}
