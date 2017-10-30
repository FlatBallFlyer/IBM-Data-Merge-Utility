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

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.*;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;

/**
 * The Class EnrichDirective. Sub-classes of this directive are used 
 * to retrieve data from a data source and place it into the Data storage
 * area of the merge context.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Enrich extends AbstractDirective implements DataProvider {

	private static final int TARGET_MISSING_THROW 	= 1;
	private static final int TARGET_MISSING_IGNORE = 2;
	public HashMap<Integer, String> targetMissingOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(TARGET_MISSING_THROW, 		"throw");
		options.put(TARGET_MISSING_IGNORE, 	"ignore");
		return options;
	}

	private String targetDataName; 
	private String targetDataDelimeter;
	private String enrichSource;
	private int parseAs;
	private int ifTargetMissing;
	private transient AbstractSource source;
	private transient AbstractProvider provider;

	public Enrich() {
		super();
		this.setType(AbstractDirective.TYPE_ENRICH);
		this.targetDataName = "";
		this.targetDataDelimeter = "\"";
		this.enrichSource = "";
		this.parseAs = ParseData.PARSE_NONE;
		this.ifTargetMissing = TARGET_MISSING_IGNORE;
	}

	@Override
	public AbstractDirective getMergable() {
		Enrich mergable = new Enrich();
		this.makeMergable(mergable);
		mergable.setType(AbstractDirective.TYPE_ENRICH);
		mergable.setTargetDataName(this.targetDataName);
		mergable.setTargetDataDelimeter(this.targetDataDelimeter);
		mergable.setEnrichSource(this.enrichSource);
		mergable.setParseAs(this.parseAs);
		mergable.setIfTargetMissing(this.ifTargetMissing);
		return mergable;
	}
	
	@Override
	public void execute(Merger context) throws MergeException {
		DataElement data = this.getProvider().get(getTemplate());
		context.getMergeData().put(this.targetDataName, this.targetDataDelimeter, data);
	}

	public AbstractProvider getProvider() throws MergeException {
		if (null == this.provider) {
			this.provider = this.source.getProvider();
		}
		return provider;
	}

	public AbstractSource getSource() {
		if (null == this.source) {
			if (null != this.template) {
// TODO				this.source = template.getContext().getConfig().getSource(enrichSource);
			}
		}
		return source;
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
	
	public void setSource(AbstractSource source) {
		this.source = source;
	}

	public void setIfTargetMissing(int value) {
		if (this.targetMissingOptions().containsKey(value)) {
			this.ifTargetMissing = value;
		}
	}

	public int getIfTargetMissing() {
		return this.ifTargetMissing;
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

}
