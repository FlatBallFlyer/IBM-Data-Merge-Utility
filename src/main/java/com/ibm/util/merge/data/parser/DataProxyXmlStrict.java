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


public class DataProxyXmlStrict {
	// TODO - Refactor to always Object with name, attrs, members
	
	public DataProxyXmlStrict() {
	}

    public DataElement fromXML(String xmlString) throws MergeException {
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
    
    private void addNodes(NodeList from, DataList to) {
		for (int index = 0; index < from.getLength(); index++) {
			Node node = from.item(index);
			DataObject object = new DataObject();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				object.put("name", new DataPrimitive(node.getNodeName()));
				if (node.hasAttributes()) {
					DataObject attrs = new DataObject();
					for (int attr = 0; attr < node.getAttributes().getLength(); attr++) {
						Node attribute = node.getAttributes().item(attr);
						attrs.put(attribute.getNodeName(),	new DataPrimitive(attribute.getNodeValue()));
					}
					object.put("attrs", attrs);
				}
				if (node.hasChildNodes()) {
					DataList list = new DataList();
					object.put("members", list);
					addNodes(node.getChildNodes(), list);
				}
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
