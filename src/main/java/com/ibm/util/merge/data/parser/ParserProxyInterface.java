package com.ibm.util.merge.data.parser;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;

public interface ParserProxyInterface {

	public Integer getKey();
	public DataElement fromString(String data) throws MergeException;

}
