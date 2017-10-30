package com.ibm.util.merge.data.parser.sax;

import org.xml.sax.helpers.DefaultHandler;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;

public abstract class XmlSaxHandler extends DefaultHandler {
	public static final String IDMU_CONTENT = "idmu-content";

	public XmlSaxHandler() {
	}
	
	public abstract DataElement getDataElement() throws MergeException;

}
