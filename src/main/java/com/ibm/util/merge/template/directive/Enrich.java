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
package com.ibm.util.merge.template.directive;

import java.util.HashMap;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.enrich.provider.*;

/**
 * <p>The enrich directive is responsible for fetching data with a Provider and placing the output in the Data Store.</p>
 * <p>See Also: {@link com.ibm.util.merge.template.directive.enrich.provider.ProviderInterface}
 * @author Mike Storey
 * @since: v4.0
 */
public class Enrich extends AbstractDirective {

	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		return options;
	}

	/**
	 * The target where the data will be placed
	 */
	private String targetDataName; 
	/**
	 * The delimiter used in the target data name
	 */
	private String targetDataDelimeter;
	/**
	 * The class name of the enrich provider
	 */
	private String enrichClass;
	/**
	 * The Source name for the provider
	 */
	private String enrichSource;
	/**
	 * The Source initialization parameter for the provider
	 */
	private String enrichParameter;
	/**
	 * The Execute Command used to retrieve the data
	 */
	private String enrichCommand;
	/**
	 * The parsAs format for parsing options (See Config.PARSE*)
	 */
	private int parseAs;
	
	/*
	 * Transient Attributes
	 */
	private transient Content targetContent;

	/**
	 * Instantiate an Enrich Directive
	 * @throws MergeException on processing errors
	 */
	public Enrich() throws MergeException {
		super();
		this.setType(AbstractDirective.TYPE_ENRICH);
		this.targetDataName = "stub";
		this.targetDataDelimeter = "-";
		this.enrichClass = "com.ibm.util.merge.template.directive.enrich.provider.StubProvider";
		this.enrichSource = "";
		this.enrichParameter = "";
		this.enrichCommand = "";
		this.parseAs = Config.PARSE_NONE;
	}

	@Override
	public void cachePrepare(Template template) throws MergeException {
		super.cachePrepare(template);
		
		// Initialize Transients
		this.targetContent = new Content(template.getWrapper(), this.targetDataName, TagSegment.ENCODE_NONE);

		// Validate Enums
		if ((this.parseAs != Config.PARSE_NONE) && (!Config.hasParser(this.parseAs))) {
			throw new Merge500("Invalide Parse As Value: " + this.parseAs) ;
		}
	}

	@Override
	public AbstractDirective getMergable() throws MergeException {
		Enrich mergable = new Enrich();
		this.makeMergable(mergable);
		mergable.setTargetContent(this.targetContent.getMergable());
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
		DataElement value = provider.provide(this.enrichCommand, this.getTemplate().getWrapper(), context, this.getTemplate().getReplaceStack(), this.parseAs);
		this.getTargetContent().replace(this.getTemplate().getReplaceStack(), true, Config.nestLimit());
		String targetName = this.getTargetContent().getValue();
		this.getTemplate().getContext().getMergeData().put(targetName, this.targetDataDelimeter, value);
	}

	/**
	 * @return target data object
	 */
	public String getTargetDataName() {
		return targetDataName;
	}
	
	/**
	 * @param dataName The target data name
	 */
	public void setTargetDataName(String dataName) {
		this.targetDataName = dataName;
	}
	
	/**
	 * @return target data name delimiter
	 */
	public String getTargetDataDelimeter() {
		return this.targetDataDelimeter;
	}

	/**
	 * @param dataDelimeter The path delimiter
	 */
	public void setTargetDataDelimeter(String dataDelimeter) {
		this.targetDataDelimeter = dataDelimeter;
	}

	/**
	 * @return enrich source name
	 */
	public String getEnrichSource() {
		return enrichSource;
	}

	/**
	 * @param enrichSource The Source Name
	 */
	public void setEnrichSource(String enrichSource) {
		this.enrichSource = enrichSource;
	}
	
	/**
	 * @return Parsing format
	 */
	public int getParseAs() {
		return parseAs;
	}

	/**
	 * @param parseAs The ParseAs option
	 */
	public void setParseAs(int parseAs) {
		this.parseAs = parseAs;
	}

	/**
	 * @return Enrichment Provider Class
	 */
	public String getEnrichClass() {
		return enrichClass;
	}

	/**
	 * @param enrichClass The enrichment provider class name
	 */
	public void setEnrichClass(String enrichClass) {
		this.enrichClass = enrichClass;
	}

	/**
	 * @return Command to execute
	 */
	public String getEnrichCommand() {
		return enrichCommand;
	}

	/**
	 * @param enrichCommand The command to execute
	 * @throws MergeException on processing errors
	 */
	public void setEnrichCommand(String enrichCommand) throws MergeException {
		this.enrichCommand = enrichCommand;
	}

	/**
	 * @return Parameters for Enrich Command
	 */
	public String getEnrichParameter() {
		return enrichParameter;
	}

	/**
	 * @param enrichParameter The Provider parameter
	 */
	public void setEnrichParameter(String enrichParameter) {
		this.enrichParameter = enrichParameter;
	}

	public Content getTargetContent() {
		return targetContent;
	}

	public void setTargetContent(Content targetContent) {
		this.targetContent = targetContent;
	}

}
