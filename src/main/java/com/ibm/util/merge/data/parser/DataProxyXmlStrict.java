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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;


/**
 * Implements a Strict DOM based parser of XML - Experimental
 * 
 * @author Mike Storey
 *
 */
public class DataProxyXmlStrict implements ParserProxyInterface {
	
	/**
	 * Instantiate a proxy
	 */
	public DataProxyXmlStrict() {
	}

	@Override
	public Integer getKey() {
		return 5;
	}

    /**
     * Parse the XML content into a Data Element
     * 
     * @param xmlString the String to parse
     * @return the parsed object
     * @throws MergeException on processing errors
     */
    public DataElement fromString(String xmlString, String options, Template context) throws MergeException {
        Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(xmlString)));
			doc.getDocumentElement().normalize();
		} catch (SAXException | IOException e1) {
			throw new Merge500("SAX Exception Prasing XML:" + e1.getMessage());
		} catch (ParserConfigurationException e2) {
			throw new Merge500("Parser Config Exception Prasing XML:" + e2.getMessage());
		}
		DataObject theDoc = new DataObject();
		theDoc.put("name", new DataPrimitive(doc.getDocumentElement().getNodeName()));
		
		DataList list = new DataList();
		theDoc.put("members", list);
		addNodes(doc.getDocumentElement().getChildNodes(), list);

		return theDoc;
    }
    
    /**
     * Private helper function - Recurivy called
     * @param from
     * @param to
     */
    private void addNodes(NodeList from, DataList to) {
		for (int index = 0; index < from.getLength(); index++) {
			Node node = from.item(index);
			DataObject object = new DataObject();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				object.put("name", new DataPrimitive(node.getNodeName()));
				DataObject attrs = new DataObject();
				if (node.hasAttributes()) {
					for (int attr = 0; attr < node.getAttributes().getLength(); attr++) {
						Node attribute = node.getAttributes().item(attr);
						attrs.put(attribute.getNodeName(),	new DataPrimitive(attribute.getNodeValue()));
					}
				}
				object.put("attrs", attrs);
				DataList list = new DataList();
				if (node.hasChildNodes()) {
					addNodes(node.getChildNodes(), list);
				}
				object.put("members", list);
				to.add(object);
			}
			
			if (node.getNodeType() == Node.TEXT_NODE) {
				if (!node.getNodeValue().trim().isEmpty()) {
					to.add(new DataPrimitive(node.getNodeValue()));
				}
			}
		}
    }

}
