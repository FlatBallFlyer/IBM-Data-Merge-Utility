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

import java.util.HashMap;

import com.ibm.util.merge.Configuration;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.directive.*;

/**
 * Implements a stub provider that returns a String with TemplateJson 
 * 
 * @author flatballflyer
 *
 */
public class StubProvider implements ProviderInterface {
	private final DataProxyJson proxy = new DataProxyJson();
	private static final ProviderMeta meta = new ProviderMeta(
			"Option Name",
			"Credentials", 
			"Command Help",
			"Parse Help",
			"Return Help");
	
	private final String source;
	private final String dbName;
	private transient final Merger context;

	/**
	 * Instantiate the provider
	 * 
	 * @param source
	 * @param dbName
	 * @param context
	 * @throws MergeException
	 */
	public StubProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		Template aTemplate = new Template("system","sample","");
		aTemplate.addDirective(new Enrich());
		aTemplate.addDirective(new Insert());
		aTemplate.addDirective(new ParseData());
		aTemplate.addDirective(new Replace());
		aTemplate.addDirective(new SaveFile());
		
		String templateJson = proxy.toString(aTemplate);
		if (parseAs == Configuration.PARSE_JSON) {
			return proxy.fromString(templateJson, DataElement.class);
		} else {
			return new DataPrimitive(templateJson);
		}
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

	@Override
	public ProviderMeta getMetaInfo() {
		return StubProvider.meta;
	}
	
}
