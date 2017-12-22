package com.ibm.util.merge.data.parser;

import java.util.HashMap;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class Parsers extends HashMap<Integer, ParserProxyInterface> {
	private static final long serialVersionUID = 1L;

	public Parsers() {
		super();
	}
	
	public void registerDefaultProxies(String[] parsers) throws MergeException {
		for (String proxy : parsers) {
			registerProxy(proxy);
		}
	}

	@SuppressWarnings("unchecked")
	public void registerProxy(String className) throws MergeException {
		Class<ParserProxyInterface> clazz;
		ParserProxyInterface theProxy;
		try {
			clazz = (Class<ParserProxyInterface>) Class.forName(className);
			theProxy = (ParserProxyInterface) clazz.newInstance();
			this.put(theProxy.getKey(), theProxy);
		} catch (ClassNotFoundException e) {
			throw new Merge500("Class Not Found exception: " + className + " message: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new Merge500("InstantiationException " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Merge500("IllegalAccessException " + e.getMessage());
		}
	}
	
	public DataElement parseString(int parseAs, String value, String options, Template context) throws MergeException {
		Integer key = new Integer(parseAs);
		if (parseAs == Config.PARSE_NONE) {
			throw new Merge500("Parse Type is None!");
		}
		if (!this.containsKey(key)) {
			throw new Merge500("Parser not found, did you register it?" + Integer.toString(parseAs));
		}
		if (null == value) {
			throw new Merge500("Can't Parse Null!");
		}
		ParserProxyInterface proxy = this.get(parseAs);
		return proxy.fromString(value, options, context);
	}
	
}
