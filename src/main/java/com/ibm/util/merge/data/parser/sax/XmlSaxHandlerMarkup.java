package com.ibm.util.merge.data.parser.sax;

import java.util.ArrayList;

import org.xml.sax.*;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;

public class XmlSaxHandlerMarkup extends XmlSaxHandler {
	private DataList data;
	private ArrayList<DataObject> contextStack;
	public XmlSaxHandlerMarkup() {
		contextStack = new ArrayList<DataObject>();
		data = new DataList();
		DataObject root = new DataObject();
		data.add(root);
		contextStack.add(root);
	}

	public DataElement getDataElement() throws Merge500 {
		return data.get(0).getAsObject().get(IDMU_CONTENT).getAsList().get(0);
	}
	
	private void addMember(DataElement member) throws Merge500 {
		DataObject context = contextStack.get(contextStack.size()-1);
		if (!context.containsKey(IDMU_CONTENT)) {
			context.put(IDMU_CONTENT, new DataList());
		}
		context.get(IDMU_CONTENT).getAsList().add(member);
	}
	
	public void	characters(char[] ch, int start, int length) {
		// notification of character data.
		String value = new String(ch, start, length);
		value = value.trim().replaceAll(" +", " "); // remove ignorable whitespace
		if (value.isEmpty()) return;
		try {
			this.addMember(new DataPrimitive(value));
		} catch (Merge500 e) {
			// TODO Auto-generated catch block
		}
	}
	
	public void	startElement(String uri, String localName, String qName, Attributes atts) {
		DataObject element = new DataObject();
		DataObject memberObject = new DataObject();
		try {
			this.addMember(element);
		} catch (Merge500 e) {
			// TODO Auto-generated catch block
		}
		element.put(localName, memberObject);
		contextStack.add(memberObject);
		for (int i=0; i < atts.getLength(); i++) {
			memberObject.put(atts.getLocalName(i), new DataPrimitive(atts.getValue(i)));
		}
	}
	
	public void endElement(String uri, String localName, String qName) {
		contextStack.remove(contextStack.size()-1); 
	}
	
}
