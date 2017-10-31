package com.ibm.util.merge.template.directive.enrich.provider;

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.directive.*;

public class StubProvider extends AbstractProvider {

	public StubProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);
	}

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException {
		DataList theTemplates = new DataList();
		Template aTemplate = new Template("system","sample","");
		aTemplate.addDirective(new Enrich());
		aTemplate.addDirective(new Insert());
		aTemplate.addDirective(new ParseData());
		aTemplate.addDirective(new Replace());
		aTemplate.addDirective(new SaveFile());
		String templateJson = proxy.toJson(aTemplate);
		theTemplates.add(new DataPrimitive(templateJson));
		return theTemplates;
	}

}
