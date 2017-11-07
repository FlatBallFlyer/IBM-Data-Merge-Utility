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
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * The Class ParseDirective is used to parse some data into a new data item.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class ParseData extends AbstractDataDirective {
	public static final int SOURCE_MISSING_THROW 	= 1;
	public static final int SOURCE_MISSING_IGNORE 	= 2;
	public HashMap<Integer, String> missingOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(SOURCE_MISSING_THROW, 	"throw");
		options.put(SOURCE_MISSING_IGNORE, 	"ignore");
		return options;
	}

	public static final int PRIMITIVE_THROW 	= 1;
	public static final int PRIMITIVE_IGNORE 	= 2;
	public static final int PRIMITIVE_PARSE 	= 3;
	public HashMap<Integer, String> primitiveOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(PRIMITIVE_THROW, 	"throw");
		options.put(PRIMITIVE_IGNORE, 	"ignore");
		options.put(PRIMITIVE_PARSE, 	"parse");
		return options;
	}

	public static final int OBJECT_THROW 	= 1;
	public static final int OBJECT_IGNORE 	= 2;
	public HashMap<Integer, String> objectOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_THROW, 	"throw");
		options.put(OBJECT_IGNORE, 	"ignore");
		return options;
	}

	public static final int LIST_THROW 			= 1;
	public static final int LIST_IGNORE 		= 2;
	public static final int LIST_PARSE_FIRST 	= 3;
	public static final int LIST_PARSE_LAST		= 4;
	public HashMap<Integer, String> listOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_THROW, 	"throw");
		options.put(LIST_IGNORE, 	"ignore");
		options.put(LIST_PARSE_FIRST, 	"parse first primitive");
		options.put(LIST_PARSE_LAST, 	"parse last primitive");
		return options;
	}

	private String dataTarget;
	private String dataTargetDelimiter;
	private String staticData;
	private int parseFormat;
	private transient Parser parser;

	/**
	 * Instantiate a Parse directive with default values
	 * @throws MergeException
	 */
	public ParseData() throws MergeException {
		super();
		this.setType(AbstractDirective.TYPE_PARSE);
		this.dataTarget = "";
		this.dataTargetDelimiter = "\"";
		this.staticData = "";
		this.parseFormat = 	Parser.PARSE_CSV;
		this.ifList = 		LIST_THROW;
		this.ifMissing = 	SOURCE_MISSING_THROW;
		this.ifObject = 	OBJECT_THROW;
		this.ifPrimitive = 	PRIMITIVE_THROW;
		this.parser = new Parser();
	}
	
	@Override
	public ParseData getMergable() throws MergeException {
		ParseData mergable = new ParseData();
		this.makeMergable(mergable);
		mergable.setDataTarget(dataTarget);
		mergable.setDataTargetDelimiter(dataTargetDelimiter);
		mergable.setDataTargetDelimiter(dataTargetDelimiter);
		mergable.setStaticData(staticData);
		mergable.setParseFormat(parseFormat);
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
				context.getMergeData().put(this.dataTarget, this.dataDelimeter, parser.parse(parseFormat, source));
				return;
			}

		} else if (data.isObject()) {
			switch (this.getIfObject()) {
			case OBJECT_THROW :
				throw new Merge500("Object Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case OBJECT_IGNORE :
				return;
			}
		
		} else if (data.isList()) {
			switch (this.getIfList()) {
			case LIST_THROW :
				throw new Merge500("List Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case LIST_IGNORE :
				return;
			case LIST_PARSE_FIRST :
				for (DataElement member : data.getAsList()) {
					if (member.isPrimitive()) {
						String source = member.getAsPrimitive();
						context.getMergeData().put(this.dataTarget, this.dataDelimeter, parser.parse(parseFormat, source));
						return;
					}
				}
				throw new Merge500("ListParseFirst found no primitive");
			case LIST_PARSE_LAST :
				for (int i = data.getAsList().size()-1 ; i >= 0; i--) {
					if (data.getAsList().get(i).isPrimitive()) {
						String source = data.getAsList().get(i).getAsPrimitive();
						context.getMergeData().put(this.dataTarget, this.dataDelimeter, parser.parse(parseFormat, source));
						return;
					}
				}
				throw new Merge500("ListParseLast found no primitive");
			}
		}
	}

	/**
	 * @return parse format
	 */
	public int getParseFormat() {
		return this.parseFormat;
	}
	
	/**
	 * @param format
	 */
	public void setParseFormat(int format) {
		if (Parser.PARSE_OPTIONS().containsKey(format)) {
			this.parseFormat = format;
		}
	}

	/**
	 * @return data target path
	 */
	public String getDataTarget() {
		return dataTarget;
	}

	/**
	 * @param dataTarget
	 */
	public void setDataTarget(String dataTarget) {
		this.dataTarget = dataTarget;
	}

	/**
	 * @return data target path delimiter
	 */
	public String getDataTargetDelimiter() {
		return dataTargetDelimiter;
	}

	/**
	 * @param dataTargetDelimiter
	 */
	public void setDataTargetDelimiter(String dataTargetDelimiter) {
		this.dataTargetDelimiter = dataTargetDelimiter;
	}

	/**
	 * @return Static data for Parsing
	 */
	public String getStaticData() {
		return staticData;
	}

	/**
	 * @param staticData
	 */
	public void setStaticData(String staticData) {
		this.staticData = staticData;
	}

	@Override
	public void setIfSourceMissing(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIfPrimitive(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIfObject(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIfList(int value) {
		// TODO Auto-generated method stub
		
	}

}
