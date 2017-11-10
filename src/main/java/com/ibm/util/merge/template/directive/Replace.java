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
import java.util.Map.Entry;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.Path;
import com.ibm.util.merge.data.PathPart;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

/**
 * The Class ReplaceDirective drives replace processing 
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Replace extends AbstractDataDirective {
	public static final int MISSING_THROW 	= 1;
	public static final int MISSING_IGNORE = 2;
	public static final HashMap<Integer, String> MISSING_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(MISSING_THROW, 		"throw");
		options.put(MISSING_IGNORE, 	"ignore");
		return options;
	}
	
	public static final int PRIMITIVE_THROW 	= 1;
	public static final int PRIMITIVE_IGNORE 	= 2;
	public static final int PRIMITIVE_REPLACE 	= 3;
	public static final int PRIMITIVE_JSON	 	= 4;
	public static final HashMap<Integer, String> PRIMITIVE_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(PRIMITIVE_THROW, 	"throw");
		options.put(PRIMITIVE_IGNORE, 	"ignore");
		options.put(PRIMITIVE_REPLACE, 	"replace");
		options.put(PRIMITIVE_JSON, 	"replace with JSON");
		return options;
	}

	public static final int OBJECT_THROW 	= 1;
	public static final int OBJECT_IGNORE 	= 2;
	public static final int OBJECT_REPLACE 	= 3;
	public static final int OBJECT_REPLACE_LIST = 4;
	public static final int OBJECT_REPLACE_JSON = 5;
	public static final HashMap<Integer, String> OBJECT_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_THROW, 	"throw");
		options.put(OBJECT_IGNORE, 	"ignore");
		options.put(OBJECT_REPLACE, "replace object");
		options.put(OBJECT_REPLACE_LIST, "replace as list");
		options.put(OBJECT_REPLACE_JSON, 	"replace with JSON");
		return options;
	}

	public static final int OBJECT_ATTRIBUTE_PRIMITIVE_THROW	= 1;
	public static final int OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE 	= 2;
	public static final int OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE 	= 3;
	public static final HashMap<Integer, String> OBJECT_ATTRIBUTE_PRIMITIVE_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_ATTRIBUTE_PRIMITIVE_THROW, 	"throw");
		options.put(OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE, 	"ignore");
		options.put(OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE, "insertList");
		return options;
	}

	public static final int OBJECT_ATTRIBUTE_OBJECT_THROW	= 1;
	public static final int OBJECT_ATTRIBUTE_OBJECT_IGNORE 	= 2;
	public static final HashMap<Integer, String> OBJECT_ATTRIBUTE_OBJECT_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_ATTRIBUTE_OBJECT_THROW, 	"throw");
		options.put(OBJECT_ATTRIBUTE_OBJECT_IGNORE, 	"ignore");
		return options;
	}

	public static final int OBJECT_ATTRIBUTE_LIST_THROW 		= 1;
	public static final int OBJECT_ATTRIBUTE_LIST_IGNORE 		= 2;
	public static final int OBJECT_ATTRIBUTE_LIST_FIRST 		= 3;
	public static final int OBJECT_ATTRIBUTE_LIST_LAST 			= 4;
	public static final HashMap<Integer, String> OBJECT_ATTRIBUTE_LIST_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_ATTRIBUTE_LIST_THROW, 	"throw");
		options.put(OBJECT_ATTRIBUTE_LIST_IGNORE, 	"ignore");
		options.put(OBJECT_ATTRIBUTE_LIST_FIRST, 	"use first primitive");
		options.put(OBJECT_ATTRIBUTE_LIST_LAST, 	"use last primitive");
		return options;
	}
	
	public static final int LIST_THROW 		= 1;
	public static final int LIST_IGNORE 	= 2;
	public static final int LIST_REPLACE 	= 3;
	public static final int LIST_FIRST 		= 4;
	public static final int LIST_LAST 		= 5;
	public static final int LIST_JSON 		= 6;
	public static final HashMap<Integer, String> LIST_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_THROW, 	"throw");
		options.put(LIST_IGNORE, 	"ignore");
		options.put(LIST_REPLACE, 	"replace");
		options.put(LIST_FIRST, 	"use first only");
		options.put(LIST_LAST,	 	"use last only");
		options.put(LIST_JSON,	 	"replace with json");
		return options;
	}

	public static final int LIST_ATTR_MISSING_THROW 	= 1;
	public static final int LIST_ATTR_MISSING_IGNORE 	= 2;
	public static final HashMap<Integer, String> LIST_ATTR_MISSING_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_ATTR_MISSING_THROW, 	"throw");
		options.put(LIST_ATTR_MISSING_IGNORE, 	"ignore");
		return options;
	}

	public static final int LIST_ATTR_NOT_PRIMITIVE_THROW 	= 1;
	public static final int LIST_ATTR_NOT_PRIMITIVE_IGNORE 	= 2;
	public static final HashMap<Integer, String> LIST_ATTR_NOT_PRIMITIVE_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_ATTR_NOT_PRIMITIVE_THROW, 	"throw");
		options.put(LIST_ATTR_NOT_PRIMITIVE_IGNORE,	"ignore");
		return options;
	}

	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		options.put("Source Missing", 			MISSING_OPTIONS());
		options.put("If Primitive", 			PRIMITIVE_OPTIONS());
		options.put("If Object", 				OBJECT_OPTIONS());
		options.put("Object-Attr Primitive", 	OBJECT_ATTRIBUTE_PRIMITIVE_OPTIONS());
		options.put("Object-Attr Object", 	 	OBJECT_ATTRIBUTE_OBJECT_OPTIONS());
		options.put("Object-Attr List", 	 	OBJECT_ATTRIBUTE_LIST_OPTIONS());
		options.put("List-Attr Missing", 		LIST_ATTR_MISSING_OPTIONS());
		options.put("List-Attr Not Primitive", 	LIST_ATTR_NOT_PRIMITIVE_OPTIONS());
		return options;
	}

	private String fromAttribute;	// used by List
	private String toAttribute;		// used by List
	private boolean processAfter;
	private boolean processRequire;
	private int objectAttrPrimitive;
	private int objectAttrList;
	private int objectAttrObject;
	private int listAttrMissing;
	private int listAttrNotPrimitive;
	private transient DataProxyJson proxy = new DataProxyJson();

	/**
	 * Instantiate a Replace directive with default values
	 */
	public Replace() {
		this (
			"", "-", 
			MISSING_THROW,
			PRIMITIVE_THROW,
			OBJECT_THROW,
			OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
			OBJECT_ATTRIBUTE_LIST_THROW,
			OBJECT_ATTRIBUTE_OBJECT_THROW,
			LIST_THROW,
			LIST_ATTR_MISSING_THROW,
			LIST_ATTR_NOT_PRIMITIVE_THROW,
			true,
			true
		);
	}

	/**
	 * Instantiate a Replace directive with the provided values
	 * 
	 * @param source
	 * @param delimeter
	 * @param missing
	 * @param primitive
	 * @param object
	 * @param list
	 * @param process
	 */
	public Replace(String source, String delimeter, int missing, int primitive, 
			int object, int objectAttrPrimitive, int objectAttrList, int objectAttrObject, 
			int list, int listAttrMissing, int listAttrNotPrimitive, boolean process, boolean require) {
		super(source, delimeter, missing, primitive, object, list);
		this.type = AbstractDirective.TYPE_REPLACE;
		this.processAfter 	= process;
		this.objectAttrPrimitive = objectAttrPrimitive;
		this.objectAttrList = objectAttrList;
		this.objectAttrObject = objectAttrObject;
		this.listAttrMissing = listAttrMissing;
		this.listAttrNotPrimitive = listAttrNotPrimitive;
		this.processRequire = require;
	}

	@Override
	public void cleanup(Config config, Template template) throws MergeException {
		this.cleanupAbstract(config, template);
	}

	@Override
	public AbstractDirective getMergable() {
		Replace mergable = new Replace();
		this.makeMergable(mergable);
		mergable.setFromAttribute(this.fromAttribute);
		mergable.setToAttribute(this.toAttribute);
		mergable.setProcessAfter(this.processAfter);
		mergable.setProcessRequire(this.processRequire);
		mergable.setIfList(this.getIfList());
		mergable.setIfObject(this.getIfObject());
		mergable.setIfPrimitive(this.getIfPrimitive());
		mergable.setObjectAttrList(this.getObjectAttrList());
		mergable.setObjectAttrObject(this.getObjectAttrObject());
		mergable.setObjectAttrPrimitive(this.getObjectAttrPrimitive());
		mergable.setListAttrMissing(this.getListAttrMissing());
		mergable.setListAttrNotPrimitive(this.getListAttrNotPrimitive());
		return mergable;
	}

	@Override
	public void execute(Merger context) throws MergeException {
		if (!context.getMergeData().contians(this.dataSource, this.dataDelimeter)) {
			switch (this.getIfSourceMissing()) {
			case MISSING_THROW :
				throw new Merge500("Source Data Missing for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case MISSING_IGNORE :
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
			case PRIMITIVE_REPLACE :
				this.replaceFromString(data.getAsPrimitive());
				break;
			case PRIMITIVE_JSON:
				this.replaceFromString(proxy.toJson(data.getAsPrimitive()));
				break;
			}

		} else if (data.isObject()) {
			switch (this.getIfObject()) {
			case OBJECT_THROW :
				throw new Merge500("Object Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case OBJECT_IGNORE :
				return;
			case OBJECT_REPLACE:
				this.replaceFromObject(data.getAsObject());
				break;
			case OBJECT_REPLACE_LIST:
				DataList list = new DataList();
				list.add(data);
				this.replaceFromList(list);
				break;
			case OBJECT_REPLACE_JSON:
				this.replaceFromString(proxy.toJson(data));
				break;
			}
		
		} else if (data.isList()) {
			switch (this.getIfList()) {
			case LIST_THROW :
				throw new Merge500("List Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case LIST_IGNORE :
				return;
			case LIST_REPLACE :
				this.replaceFromList(data.getAsList());
				break;
			case LIST_FIRST:
				DataElement listFirst = data.getAsList().get(0);
				if (listFirst.isPrimitive()) {
					this.replaceFromString(listFirst.getAsPrimitive());
				} else if (listFirst.isObject()) {
					this.replaceFromObject(listFirst.getAsObject());
				}
				break;
			case LIST_LAST:
				DataElement listLast = data.getAsList().get(data.getAsList().size()-1);
				if (listLast.isPrimitive()) {
					this.replaceFromString(listLast.getAsPrimitive());
				} else if (listLast.isObject()) {
					this.replaceFromObject(listLast.getAsObject());
				}
				break;
			case LIST_JSON:
				this.replaceFromString(proxy.toJson(data));
				break;
			}
		}
		
		if (this.processAfter) {
			template.getMergeContent().replace(template.getReplaceStack(), this.processRequire, this.getTemplate().getContext().getConfig().getNestLimit()); 
		}
	}
	
	/**
	 * Add replace values from a primitive
	 * @param context
	 * @throws MergeException
	 */
	private void replaceFromString(String dataString) throws MergeException {
//		String dataString = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsPrimitive();
		Path dataPath = new Path(this.dataSource, this.dataDelimeter);
		PathPart from = dataPath.remove();
		while (from.isList) from = dataPath.remove(); 
		this.template.addReplace(from.part, dataString); 
	}
	
	/**
	 * Add replace values from a list
	 * @param context
	 * @throws MergeException
	 */
	private void replaceFromList(DataList dataList) throws MergeException {
//		DataList dataList = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsList();
		for (DataElement row : dataList) {
			if (row.isObject()) {
				DataObject orow = row.getAsObject();
				String fromValue = "";
				String toValue = "";
				if (!orow.containsKey(this.fromAttribute) || 
					!orow.containsKey(this.toAttribute)) { 
					switch (this.getListAttrMissing()) {
					case LIST_ATTR_MISSING_THROW :
						throw new Merge500("List from/to Attribute Missing " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
					case LIST_ATTR_MISSING_IGNORE :
						return;
					}
				}
				if (!orow.get(this.fromAttribute).isPrimitive() || 
					!orow.get(this.toAttribute).isPrimitive()) {
					switch (this.getListAttrNotPrimitive()) {
					case LIST_ATTR_NOT_PRIMITIVE_THROW :
						throw new Merge500("List from/to Attribute Missing " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
					case LIST_ATTR_NOT_PRIMITIVE_IGNORE :
						return;
					}
				}
				
				fromValue = row.getAsObject().get(this.fromAttribute).getAsPrimitive();
				toValue = row.getAsObject().get(this.toAttribute).getAsPrimitive();
				this.template.addReplace(fromValue, toValue);
			}
		}
	}
	
	/**
	 * Add replace values from a DataObject
	 * @param context
	 * @throws MergeException
	 */
	private void replaceFromObject(DataObject dataObject) throws MergeException {
//		DataObject dataObject = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsObject();
		String from = "";
		String to = "";
		for (Entry<String, DataElement> member: dataObject.entrySet()) {
			if (member.getValue().isPrimitive()) {
				switch (this.getObjectAttrPrimitive()) {
				case OBJECT_ATTRIBUTE_PRIMITIVE_THROW :
					throw new Merge500("Object Attribute is Primitive " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
				case OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE :
					continue;
				case OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE :
					from = member.getKey();
					to = member.getValue().getAsPrimitive();
					template.addReplace(from, to);
				}
			} else if (member.getValue().isObject()) {
				switch (this.getObjectAttrObject()) {
				case OBJECT_ATTRIBUTE_OBJECT_THROW :
					throw new Merge500("Object Attribute is Object " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
				case OBJECT_ATTRIBUTE_OBJECT_IGNORE :
					continue;
				}
			} else if (member.getValue().isList()) {
				switch (this.getObjectAttrList()) {
				case OBJECT_ATTRIBUTE_LIST_THROW :
					throw new Merge500("Object Attribute is List " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
				case OBJECT_ATTRIBUTE_LIST_IGNORE :
					continue;
				case OBJECT_ATTRIBUTE_LIST_FIRST :
					if (member.getValue().getAsList().size() < 1) {
						continue; // No First Member
					}
					if (!member.getValue().getAsList().get(0).isPrimitive()) {
						throw new Merge500("Object Attribute List First is not primitive " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
					}
					from = member.getKey();
					to = member.getValue().getAsList().get(0).getAsPrimitive();
					template.addReplace(from, to);
					continue;
				case OBJECT_ATTRIBUTE_LIST_LAST :
					int last = member.getValue().getAsList().size();
					if (last < 1) {
						continue; // No Last Member
					}
					if (!member.getValue().getAsList().get(last-1).isPrimitive()) {
						throw new Merge500("Object Attribute List Last is not primitive " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
					}
					from = member.getKey();
					to = member.getValue().getAsList().get(last-1).getAsPrimitive();
					template.addReplace(from, to);
					continue;
				}
			}
		}
	}

	/**
	 * @return name of attribute for List To values
	 */
	public String getToAttribute() {
		return toAttribute;
	}

	/**
	 * @return name of attribute for List From values
	 */
	public String getFromAttribute() {
		return fromAttribute;
	}

	/**
	 * @return process indicator
	 */
	public boolean getProcessAfter() {
		return processAfter;
	}

	public int getObjectAttrPrimitive() {
		return objectAttrPrimitive;
	}
	
	public int getObjectAttrList() {
		return objectAttrList;
	}

	public int getObjectAttrObject() {
		return objectAttrObject;
	}

	public int getListAttrNotPrimitive() {
		return listAttrNotPrimitive;
	}

	public int getListAttrMissing() {
		return listAttrMissing;
	}

	/**
	 * @param toAttribute
	 */
	public void setToAttribute(String toAttribute) {
		this.toAttribute = toAttribute;
	}

	/**
	 * @param fromAttribute
	 */
	public void setFromAttribute(String fromAttribute) {
		this.fromAttribute = fromAttribute;
	}

	/**
	 * @param processAfter
	 */
	public void setProcessAfter(boolean processAfter) {
		this.processAfter = processAfter;
	}

	public void setObjectAttrPrimitive(int objectAttrPrimitive) {
		if (OBJECT_ATTRIBUTE_PRIMITIVE_OPTIONS().keySet().contains(objectAttrPrimitive)) {
			this.objectAttrPrimitive = objectAttrPrimitive;
		}
	}

	public void setObjectAttrList(int objectAttrList) {
		if (OBJECT_ATTRIBUTE_LIST_OPTIONS().keySet().contains(objectAttrList)) {
			this.objectAttrList = objectAttrList;
		}
	}

	public void setObjectAttrObject(int objectAttrObject) {
		if (OBJECT_ATTRIBUTE_OBJECT_OPTIONS().keySet().contains(objectAttrObject)) {
			this.objectAttrObject = objectAttrObject;
		}
	}

	public void setListAttrMissing(int listAttrMissing) {
		if (LIST_ATTR_MISSING_OPTIONS().keySet().contains(listAttrMissing)) {
			this.listAttrMissing = listAttrMissing;
		}
	}

	public void setListAttrNotPrimitive(int listAttrNotPrimitive) {
		if (LIST_ATTR_NOT_PRIMITIVE_OPTIONS().keySet().contains(listAttrNotPrimitive)) {
			this.listAttrNotPrimitive = listAttrNotPrimitive;
		}
	}

	public boolean getProcessRequire() {
		return processRequire;
	}

	public void setProcessRequire(boolean processRequire) {
		this.processRequire = processRequire;
	}

	@Override
	public void setIfSourceMissing(int value) {
		if (MISSING_OPTIONS().keySet().contains(value)) {
			this.ifMissing = value;
		}
	}

	@Override
	public void setIfPrimitive(int value) {
		if (PRIMITIVE_OPTIONS().keySet().contains(value)) {
			this.ifPrimitive= value;
		}
	}

	@Override
	public void setIfObject(int value) {
		if (OBJECT_OPTIONS().keySet().contains(value)) {
			this.ifObject = value;
		}
	}

	@Override
	public void setIfList(int value) {
		if (LIST_OPTIONS().keySet().contains(value)) {
			this.ifList = value;
		}
	}
}
