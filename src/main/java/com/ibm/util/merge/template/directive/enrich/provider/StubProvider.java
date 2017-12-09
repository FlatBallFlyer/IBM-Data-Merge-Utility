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
package com.ibm.util.merge.template.directive.enrich.provider;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.*;

/**
 * A simple stub provider - provides a Sample Template json object
 * 
 * @author Mike Storey
 *
 */
public class StubProvider implements ProviderInterface {
	private final DataProxyJson proxy = new DataProxyJson();
	private static final ProviderMeta meta = new ProviderMeta(
			"N/A",
			"N/A", 
			"N/A",
			"Will parse the Template JSON",
			"Primitive with TemplateJson if not parsed, Template object if parsed");
	
	/**
	 * Instantiate the provider
	 * 
	 */
	public StubProvider() {
	}

	@Override
	public DataElement provide(Enrich context) throws MergeException {
		Template aTemplate = new Template("system","sample","");
		aTemplate.addDirective(new Enrich());
		aTemplate.addDirective(new Insert());
		aTemplate.addDirective(new ParseData());
		aTemplate.addDirective(new Replace());
		aTemplate.addDirective(new SaveFile());
		
		String templateJson = proxy.toString(aTemplate);
		if (context.getParseAs() == Config.PARSE_JSON) {
			return proxy.fromString(templateJson, DataElement.class);
		} else {
			return new DataPrimitive(templateJson);
		}
	}

	@Override
	public void close() {
		// nothing to close
		return;
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return StubProvider.meta;
	}
}
