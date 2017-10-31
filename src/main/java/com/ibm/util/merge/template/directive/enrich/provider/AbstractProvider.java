package com.ibm.util.merge.template.directive.enrich.provider;

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;

public abstract class AbstractProvider {
	private final String source;
	private final String dbName;
	private final Merger context;
	protected DataProxyJson proxy = new DataProxyJson();
	protected Parser parser = new Parser();

	public AbstractProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}
	
	public abstract DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException;

	public String getSource() {
		return source;
	}

	public String getDbName() {
		return dbName;
	}


	public Merger getContext() {
		return context;
	}
	
}
