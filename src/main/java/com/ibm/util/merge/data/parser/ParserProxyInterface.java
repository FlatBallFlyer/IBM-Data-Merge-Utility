package com.ibm.util.merge.data.parser;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public interface ParserProxyInterface {

	public Integer getKey();
	public DataElement fromString(String data, String options, Template context) throws MergeException;

}
