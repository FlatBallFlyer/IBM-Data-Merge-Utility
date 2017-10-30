package com.ibm.util.merge.data.parser.sax;

import java.util.ArrayList;

import org.xml.sax.*;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.Path;
import com.ibm.util.merge.exception.MergeException;

public class XmlSaxHandlerData extends XmlSaxHandler {
	private DataManager data;
	private ArrayList<Path> context;
	
	public XmlSaxHandlerData() throws MergeException {
		this.setData(new DataManager());
		data.put("root", "&", new DataObject());
		Path start = new Path("root", "&"); 
		context = new ArrayList<Path>();
		context.add(start);
	}

	@Override
	public DataElement getDataElement() throws MergeException {
		return this.data.get("root", "&");
	}

	public void	characters(char[] ch, int start, int length) {
		// notification of character data.
		String value = new String(ch, start, length);
		value = value.trim().replaceAll(" +", " "); // remove ignorable whitespace
		if (value.isEmpty()) return;
		try {
			data.put(context.get(context.size()-1), new DataPrimitive(value));
		} catch (MergeException e) {
			// impossible
		}
	}
	
	public void	startElement(String uri, String localName, String qName, Attributes atts) {
		try {
			Path here = new Path(context.get(context.size()-1));
			here.add(localName);
			data.put(here, new DataObject());
			context.add(here);
			for (int i=0; i < atts.getLength(); i++) {
				here.add(atts.getLocalName(i));
				data.put(here, new DataPrimitive(atts.getValue(i)));
				here.remove();
			}
		} catch (MergeException e) {
			// impossible
		}
	}
	
	public void endElement(String uri, String localName, String qName) {
		context.remove(context.size()-1); 
	}
	
	public DataManager getData() {
		return data;
	}

	public void setData(DataManager data) {
		this.data = data;
	}
	
}
