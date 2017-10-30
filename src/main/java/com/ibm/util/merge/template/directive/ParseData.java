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
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * The Class ParseDirective. Sub-classes of this directive are used 
 * to parse some data from the Merge Context into a new data item
 * in the merge context data store. 
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class ParseData extends AbstractDataDirective {
	public static final int PARSE_CSV	 		= 1;
	public static final int PARSE_HTML	 		= 2;
	public static final int PARSE_JSON			= 3;
	public static final int PARSE_NONE	 		= 4;
	public static final int PARSE_XML_MARKUP	= 5;
	public static final int PARSE_XML_DATA		= 6;
	public static final HashMap<Integer, String> PARSE_OPTIONS() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(PARSE_CSV, 		"csv");
		values.put(PARSE_HTML, 		"html");
		values.put(PARSE_JSON, 		"json");
		values.put(PARSE_NONE, 		"none");
		values.put(PARSE_XML_DATA, 	"xml-data");
		values.put(PARSE_XML_MARKUP, "xml-mark");
		return values;
	}

	private static final int TARGET_MISSING_THROW 	= 1;
	private static final int TARGET_MISSING_IGNORE 	= 2;
	private static final int TARGET_MISSING_PARSE 	= 3;
	public HashMap<Integer, String> targetMissingOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(TARGET_MISSING_THROW, 	"throw");
		options.put(TARGET_MISSING_IGNORE, 	"ignore");
		options.put(TARGET_MISSING_PARSE, 	"parse");
		return options;
	}
	
	private static final int SOURCE_MISSING_THROW 	= 1;
	private static final int SOURCE_MISSING_IGNORE 	= 2;
	public HashMap<Integer, String> missingOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(SOURCE_MISSING_THROW, 	"throw");
		options.put(SOURCE_MISSING_IGNORE, 	"ignore");
		return options;
	}

	private static final int PRIMITIVE_THROW 	= 1;
	private static final int PRIMITIVE_IGNORE 	= 2;
	private static final int PRIMITIVE_PARSE 	= 3;
	public HashMap<Integer, String> primitiveOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(PRIMITIVE_THROW, 	"throw");
		options.put(PRIMITIVE_IGNORE, 	"ignore");
		options.put(PRIMITIVE_PARSE, 	"parse");
		return options;
	}

	private static final int OBJECT_THROW 	= 1;
	private static final int OBJECT_IGNORE 	= 2;
	private static final int OBJECT_PARSE 	= 3;
	public HashMap<Integer, String> objectOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_THROW, 	"throw");
		options.put(OBJECT_IGNORE, 	"ignore");
		options.put(OBJECT_PARSE, 	"parse");
		return options;
	}

	private static final int LIST_THROW 	= 1;
	private static final int LIST_IGNORE 	= 2;
	private static final int LIST_PARSE 	= 3;
	public HashMap<Integer, String> listOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_THROW, 	"throw");
		options.put(LIST_IGNORE, 	"ignore");
		options.put(LIST_PARSE, 	"parse");
		return options;
	}

	private String targetData;
	private int ifTargetMissing;
	private int parseFormat;
	private transient Parser parser;

	public ParseData() throws MergeException {
		super();
		this.setType(AbstractDirective.TYPE_PARSE);
		this.parser = new Parser();
		this.parseFormat = 		PARSE_CSV;
		this.ifMissing = 		SOURCE_MISSING_THROW;
		this.ifPrimitive = 		PRIMITIVE_THROW;
		this.ifList = 			LIST_THROW;
		this.ifObject = 		OBJECT_THROW;
		this.ifTargetMissing = 	TARGET_MISSING_THROW;
	}
	
	@Override
	public ParseData getMergable() throws MergeException {
		ParseData mergable = new ParseData();
		mergable.setType(AbstractDirective.TYPE_PARSE);
		mergable.setTargetData(targetData);
		mergable.setParseFormat(this.getParseFormat());
		mergable.setIfTargetMissing(this.getIfTargetMissing());
		return mergable;
	}

	@Override
	public void execute(Merger context) throws MergeException {
		if (!context.getMergeData().contians(this.dataSource, this.dataDelimeter)) {
			switch (this.getIfSourceMissing()) {
			case SOURCE_MISSING_THROW :
				throw new Merge500("Source Data Missing for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case SOURCE_MISSING_IGNORE :
				return;
			}
		}
		
		DataElement data = context.getMergeData().get(this.dataSource, this.dataDelimeter);
		
		if (data.isPrimitive()) {
			switch (this.getIfPrimitive()) {
			case PRIMITIVE_THROW :
				throw new Merge500("Primitive Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case PRIMITIVE_IGNORE :
				return;
			case PRIMITIVE_PARSE :
				String source = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsPrimitive();
				context.getMergeData().put(this.targetData, this.dataDelimeter, parser.parse(parseFormat, source));
				return;
			}

		} else if (data.isObject()) {
			switch (this.getIfObject()) {
			case OBJECT_THROW :
				throw new Merge500("Object Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case OBJECT_IGNORE :
				return;
			case OBJECT_PARSE :
				String source = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsPrimitive();
				context.getMergeData().put(this.targetData, this.dataDelimeter, parser.parse(parseFormat, source));
				return;
			}
		
		} else if (data.isList()) {
			switch (this.getIfList()) {
			case LIST_THROW :
				throw new Merge500("List Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case LIST_IGNORE :
				return;
			case LIST_PARSE :
				String source = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsPrimitive();
				context.getMergeData().put(this.targetData, this.dataDelimeter, parser.parse(parseFormat, source));
				break;
			}
		}
	}

	public String getTargetData() {
		return targetData;
	}
	
	public void setTargetData(String targetData) {
		this.targetData = targetData;
	}
	
	public int getParseFormat() {
		return this.parseFormat;
	}
	
	public void setParseFormat(int format) {
		if (PARSE_OPTIONS().containsKey(format)) {
			this.parseFormat = format;
		}
	}

	public int getIfTargetMissing() {
		return ifTargetMissing;
	}

	public void setIfTargetMissing(int ifTargetMissing) {
		if (this.targetMissingOptions().containsKey(ifTargetMissing)) {
			this.ifTargetMissing = ifTargetMissing;
		}
	}

}
