package com.ibm.util.merge.template.directive.enrich.provider;

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.directive.*;

public class StubProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient final DataProxyJson proxy = new DataProxyJson();

	public StubProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException {
		Template aTemplate = new Template("system","sample","");
		aTemplate.addDirective(new Enrich());
		aTemplate.addDirective(new Insert());
		aTemplate.addDirective(new ParseData());
		aTemplate.addDirective(new Replace());
		aTemplate.addDirective(new SaveFile());
		String templateJson = proxy.toJson(aTemplate);
		return new DataPrimitive(templateJson);
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public String getDbName() {
		return this.dbName;
	}

	@Override
	public Merger getContext() {
		return this.context;
	}

}
