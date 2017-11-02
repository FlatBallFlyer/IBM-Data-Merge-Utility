/*
 * Copyright 2015, 2015 IBM
 * 
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
package com.ibm.util.merge.template.directive;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.*;

/**
 * The Class EnrichDirective. Sub-classes of this directive are used 
 * to retrieve data from a data source and place it into the Data storage
 * area of the merge context.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Enrich extends AbstractDirective {

	private String targetDataName; 
	private String targetDataDelimeter;
	private String enrichClass;
	private String enrichSource;
	private String enrichParameter;
	private String enrichCommand;
	private int parseAs;
	private transient Parser parser;

	public Enrich() throws MergeException {
		super();
		this.parser = new Parser();
		this.setType(AbstractDirective.TYPE_ENRICH);
		this.targetDataName = "";
		this.targetDataDelimeter = "\"";
		this.enrichClass = "com.ibm.util.merge.template.directive.enrich.provider.StubProvider";
		this.enrichSource = "";
		this.enrichParameter = "";
		this.enrichCommand = "";
		this.parseAs = ParseData.PARSE_NONE;
	}

	@Override
	public AbstractDirective getMergable() throws MergeException {
		Enrich mergable = new Enrich();
		this.makeMergable(mergable);
		mergable.setType(AbstractDirective.TYPE_ENRICH);
		mergable.setTargetDataName(this.targetDataName);
		mergable.setTargetDataDelimeter(this.targetDataDelimeter);
		mergable.setEnrichSource(this.enrichSource);
		mergable.setEnrichClass(this.enrichClass);
		mergable.setEnrichCommand(this.enrichCommand);
		mergable.setEnrichParameter(this.enrichParameter);
		mergable.setParseAs(this.parseAs);
		return mergable;
	}
	
	@Override
	public void execute(Merger context) throws MergeException {
		ProviderInterface provider = context.getProvider(this.enrichClass, this.enrichSource, this.enrichParameter);
		DataElement value = provider.provide(this.enrichCommand, this.getTemplate().getWrapper(), context, this.template.getReplaceStack());
		if (this.parseAs != ParseData.PARSE_NONE) {
			value = parser.parse(this.parseAs, value.getAsPrimitive());
		}
		this.getTemplate().getContext().getMergeData().put(this.targetDataName, this.targetDataDelimeter, value);
	}

	public String getTargetDataName() {
		return targetDataName;
	}
	
	public void setTargetDataName(String dataName) {
		this.targetDataName = dataName;
	}
	
	public String getEnrichSource() {
		return enrichSource;
	}

	public void setEnrichSource(String enrichSource) {
		this.enrichSource = enrichSource;
	}
	
	public String getTargetDataDelimeter() {
		return this.targetDataDelimeter;
	}

	public void setTargetDataDelimeter(String dataDelimeter) {
		this.targetDataDelimeter = dataDelimeter;
	}

	public int getParseAs() {
		return parseAs;
	}

	public void setParseAs(int parseAs) {
		this.parseAs = parseAs;
	}

	public String getEnrichClass() {
		return enrichClass;
	}

	public void setEnrichClass(String enrichClass) {
		this.enrichClass = enrichClass;
	}

	public String getEnrichCommand() {
		return enrichCommand;
	}

	public void setEnrichCommand(String enrichCommand) {
		this.enrichCommand = enrichCommand;
	}

	public String getEnrichParameter() {
		return enrichParameter;
	}

	public void setEnrichParameter(String enrichParameter) {
		this.enrichParameter = enrichParameter;
	}

}
