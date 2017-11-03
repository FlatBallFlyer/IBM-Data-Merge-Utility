package com.ibm.util.merge.data.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Attribute;
import org.jsoup.select.Elements;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;


public class DataProxyHtml {

	public DataProxyHtml() {
		
	}

    public DataElement fromHTML(String htmlString, String options) {
    	DataList theList = new DataList();
    	Document doc = Jsoup.parse(htmlString);
    	Elements elements = doc.select(options);

    	for (Element element : elements) {
    		DataObject theElement = new DataObject();
    		theElement.put("name", new DataPrimitive(element.nodeName()));
    		theElement.put("text", new DataPrimitive(element.text()));
    		theElement.put("html", new DataPrimitive(element.html()));
    		DataObject theAttrs = new DataObject();
    		for (Attribute attr : element.attributes()) {
    			theAttrs.put(attr.getKey(), new DataPrimitive(attr.getValue()));
    		}
    		theElement.put("attrs", theAttrs);
    		theList.add(theElement);
    	}
    	return theList;
    }

}
