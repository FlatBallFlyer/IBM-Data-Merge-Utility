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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Attribute;
import org.jsoup.select.Elements;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;

/**
 * Provides a jQuery based HTML parser
 * 
 * @author Mike Storey
 *
 */
public class DataProxyHtml {

	/**
	 * Instantiates a proxy 
	 */
	public DataProxyHtml() {
	}

    /**
     * Parse the data with the options
     * 
     * @param htmlString
     * @param options
     * @return
     */
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
