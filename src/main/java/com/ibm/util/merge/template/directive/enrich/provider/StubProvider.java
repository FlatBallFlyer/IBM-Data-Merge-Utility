package com.ibm.util.merge.template.directive.enrich.provider;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.*;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;

public class StubProvider extends AbstractProvider {

	public StubProvider(AbstractSource source) throws MergeException {
		super(source);
		this.setType(AbstractProvider.PROVIDER_STUB);
	}

	@Override
	public DataElement get(Template template) throws MergeException {
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

	@Override
	public void put(Template template) {
		// NO-OP
	}

	@Override
	public void post(Template template) {
		// NO-OP
	}

	@Override
	public void delete(Template template) {
		// NO-OP
	}

}
