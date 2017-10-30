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
package com.ibm.util.merge.template.directive.enrich.source;

import java.util.HashMap;

import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;

/**
 * The Class AbstractSource. Sub-classes of this class read and provide configuration
 * information for a specific source. 
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public abstract class AbstractSource {

	public static final int SOURCE_CACHE	= 1;
	public static final int SOURCE_CLOUDANT = 2;
	public static final int SOURCE_FILE 	= 3;
	public static final int SOURCE_JDBC 	= 4;
	public static final int SOURCE_MONGO 	= 5;
	public static final int SOURCE_REST 	= 6;
	public static final int SOURCE_STUB		= 7;
	public static final HashMap<Integer, String> SOURCE_TYPES() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(SOURCE_CACHE, 	"cache");
		values.put(SOURCE_CLOUDANT, "cloudant");
		values.put(SOURCE_FILE, 	"file");
		values.put(SOURCE_JDBC, 	"jdbc");
		values.put(SOURCE_MONGO, 	"mongo");
		values.put(SOURCE_REST, 	"rest");
		values.put(SOURCE_STUB, 	"stub");
		return values;
	}
	
	private int type;
	private int parseAs;
	private String name = "";
	private String env = "";
	private String getCommand;
	private String putCommand;
	private String postCommand;
	private String deleteCommand;

	public AbstractSource() {
		type = SOURCE_STUB;
		parseAs = ParseData.PARSE_NONE;
		this.getCommand = "";
		this.putCommand = "";
		this.postCommand = "";
		this.deleteCommand = "";
	}

	public abstract AbstractProvider getProvider() throws MergeException;
	public abstract void setOptions(DataObject sourceObject);
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		if (SOURCE_TYPES().containsKey(type)) {
			this.type = type;
		}
	}
	
	public int getParseAs() {
		return parseAs;
	}
	public void setParseAs(int parseAs) {
		if (ParseData.PARSE_OPTIONS().containsKey(parseAs)) {
			this.parseAs = parseAs;
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	
	public String getGetCommand() {
		return getCommand;
	}
	public void setGetCommand(String getCommand) {
		this.getCommand = getCommand;
	}

	public String getPutCommand() {
		return putCommand;
	}
	public void setPutCommand(String putCommand) {
		this.putCommand = putCommand;
	}

	public String getPostCommand() {
		return postCommand;
	}
	public void setPostCommand(String postCommand) {
		this.postCommand = postCommand;
	}

	public String getDeleteCommand() {
		return deleteCommand;
	}
	public void setDeleteCommand(String deleteCommand) {
		this.deleteCommand = deleteCommand;
	}
	
}
