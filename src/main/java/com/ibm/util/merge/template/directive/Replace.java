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
import java.util.TreeMap;
import java.util.UUID;
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
 * Add replace from/to values from the Data Store to the replace stack, and optionally process 
 * that over the content of the template. See class constants for information on replace options.
 * 
 * @author Mike Storey
 * @since: v4.0
 * @see com.ibm.util.merge.template.content.Content
 * @see com.ibm.util.merge.template.content.TagSegment
 * @see com.ibm.util.merge.data.DataManager
 */
public class Replace extends AbstractDataDirective {
	private String toValue;
	private String fromValue = "";
	private boolean processAfter;
	private boolean processRequire;
	private int objectAttrPrimitive;
	private int objectAttrList;
	private int objectAttrObject;
	private String fromAttribute;	// used by List
	private String toAttribute;		// used by List
	private int listAttrMissing;
	private int listAttrNotPrimitive;

	// Transient Variables
	private transient DataProxyJson proxy = new DataProxyJson();

	/**
	 * Instantiate a Replace directive with default values
	 */
	public Replace() {
		this (
			"", "-", "",
			MISSING_IGNORE,
			PRIMITIVE_IGNORE,
			OBJECT_IGNORE,
			OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
			OBJECT_ATTRIBUTE_LIST_THROW,
			OBJECT_ATTRIBUTE_OBJECT_THROW,
			LIST_IGNORE, "", "",
			LIST_ATTR_MISSING_THROW,
			LIST_ATTR_NOT_PRIMITIVE_THROW,
			false,
			false
		);
	}

	/**
	 * Instantiate a Replace directive with the provided values
	 * 
	 * @param source The Data Source
	 * @param delimeter The Delimiter used in Source
	 * @param missing The If Missing option
	 * @param primitive The If Primitive option
	 * @param to The replace To value for If Primitive operator
	 * @param object The If Object option
	 * @param objectAttrPrimitive Object Attr Primitive option
	 * @param objectAttrList Object Attr List option
	 * @param objectAttrObject Object Attr Object option
	 * @param list The If List option
	 * @param fromAttr The list from attribute
	 * @param toAttr The list to attribute
	 * @param listAttrMissing Attr missing option
	 * @param listAttrNotPrimitive Attr not primitive option
	 * @param process Process after indicator
	 * @param require The require all tags indicator
	 */
	public Replace(String source, String delimeter, String to, int missing, int primitive, 
			int object, int objectAttrPrimitive, int objectAttrList, int objectAttrObject, 
			int list, String fromAttr, String toAttr, int listAttrMissing, int listAttrNotPrimitive, boolean process, boolean require) {
		super(source, delimeter, missing, primitive, object, list);
		this.setType(AbstractDirective.TYPE_REPLACE);
		this.fromAttribute = fromAttr;
		this.toAttribute = toAttr;
		this.processAfter 	= process;
		this.objectAttrPrimitive = objectAttrPrimitive;
		this.objectAttrList = objectAttrList;
		this.objectAttrObject = objectAttrObject;
		this.listAttrMissing = listAttrMissing;
		this.listAttrNotPrimitive = listAttrNotPrimitive;
		this.processRequire = require;
		this.toValue = to;
	}

	@Override
	public void cachePrepare(Template template, Config config) throws MergeException {
		super.cachePrepare(template, config);

		// Validate enums
		if (!Replace.MISSING_OPTIONS().containsKey(this.getIfSourceMissing())) {
			throw new Merge500("Invalide Source Missing Option:" + Integer.toString(this.getIfSourceMissing()));
		}
		
		if (!Replace.PRIMITIVE_OPTIONS().containsKey(this.getIfPrimitive())) {
			throw new Merge500("Invalide If Primitive Option:" + Integer.toString(this.getIfPrimitive()));
		}
		
		if (!Replace.LIST_OPTIONS().containsKey(this.getIfList())) {
			throw new Merge500("Invalide If List Option:" + Integer.toString(this.getIfList()));
		}

		if (!Replace.OBJECT_OPTIONS().containsKey(this.getIfObject())) {
			throw new Merge500("Invalide If Object Option:" + Integer.toString(this.getIfObject()));
		}
		
		if (!Replace.LIST_ATTR_MISSING_OPTIONS().containsKey(this.getListAttrMissing())) {
			throw new Merge500("Invalide List Attribute Missing Option:" + Integer.toString(this.getListAttrMissing()));
		}

		if (!Replace.LIST_ATTR_NOT_PRIMITIVE_OPTIONS().containsKey(this.getListAttrNotPrimitive())) {
			throw new Merge500("Invalide List Attribute Not Primitive Option:" + Integer.toString(this.getListAttrNotPrimitive()));
		}

		if (!Replace.OBJECT_ATTRIBUTE_LIST_OPTIONS().containsKey(this.getObjectAttrList())) {
			throw new Merge500("Invalide Object Attribute List Option:" + Integer.toString(this.getObjectAttrList()));
		}

		if (!Replace.OBJECT_ATTRIBUTE_PRIMITIVE_OPTIONS().containsKey(this.getObjectAttrPrimitive())) {
			throw new Merge500("Invalide Object Attribute Primitive Option:" + Integer.toString(this.getObjectAttrPrimitive()));
		}

		if (!Replace.OBJECT_ATTRIBUTE_OBJECT_OPTIONS().containsKey(this.getObjectAttrObject())) {
			throw new Merge500("Invalide Object Attribute Primitive Option:" + Integer.toString(this.getObjectAttrObject()));
		}
	}

	@Override
	public AbstractDirective getMergable(Merger context) throws MergeException {
		Replace mergable = new Replace();
		this.makeMergable(mergable, context);
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
		mergable.setToValue(this.getToValue());
		mergable.setFromValue(this.getFromValue());
		return mergable;
	}

	@Override
	public void execute(Merger context) throws MergeException {
		this.getSourceContent().replace(this.getTemplate().getReplaceStack(), true, this.getConfig().getNestLimit());
		String source = this.getSourceContent().getValue();
		if (!context.getMergeData().contians(source, this.getDataDelimeter())) {
			switch (this.getIfSourceMissing()) {
			case MISSING_THROW :
				throw new Merge500("Source Data Missing for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
			case MISSING_IGNORE :
				return;
			case MISSING_REPLACE : 
				this.replaceFromString(this.toValue);
				if (this.processAfter) {
					this.getTemplate().getMergeContent().replace(
							this.getTemplate().getReplaceStack(), 
							this.processRequire, 
							this.getConfig().getNestLimit()
					); 
				}
				return;
			case MISSING_GUID :
				this.replaceFromString(UUID.randomUUID().toString());
				if (this.processAfter) {
					this.getTemplate().getMergeContent().replace(
							this.getTemplate().getReplaceStack(), 
							this.processRequire, 
							this.getConfig().getNestLimit()
					); 
				}
				return;
			}
		}
		
		DataElement data = context.getMergeData().get(source, this.getDataDelimeter());
		
		if (data.isPrimitive()) {
			switch (this.getIfPrimitive()) {
			case PRIMITIVE_THROW :
				throw new Merge500("Primitive Data found for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
			case PRIMITIVE_IGNORE :
				return;
			case PRIMITIVE_REPLACE :
				this.replaceFromString(data.getAsPrimitive());
				break;
			case PRIMITIVE_JSON:
				this.replaceFromString(proxy.toString(data.getAsPrimitive()));
				break;
			}

		} else if (data.isObject()) {
			switch (this.getIfObject()) {
			case OBJECT_THROW :
				throw new Merge500("Object Data found for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
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
				this.replaceFromString(proxy.toString(data));
				break;
			}
		
		} else if (data.isList()) {
			switch (this.getIfList()) {
			case LIST_THROW :
				throw new Merge500("List Data found for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
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
				} else if (listFirst.isList()) {
					this.replaceFromList(listFirst.getAsList());
				}
				break;
			case LIST_LAST:
				DataElement listLast = data.getAsList().get(data.getAsList().size()-1);
				if (listLast.isPrimitive()) {
					this.replaceFromString(listLast.getAsPrimitive());
				} else if (listLast.isObject()) {
					this.replaceFromObject(listLast.getAsObject());
				} else if (listLast.isList()) {
					this.replaceFromList(listLast.getAsList());
				}
				break;
			case LIST_JSON:
				this.replaceFromString(proxy.toString(data));
				break;
			}
		}
		
		if (this.processAfter) {
			this.getTemplate()
				.getMergeContent()
				.replace(this.getTemplate().getReplaceStack(), 
						this.processRequire, 
						this.getConfig().getNestLimit()); 
		}
	}
	
	/**
	 * Add replace values from a primitive
	 * @param context
	 * @throws MergeException
	 */
	private void replaceFromString(String to) throws MergeException {
		Path dataPath = new Path(this.getDataSource(), this.getDataDelimeter());
		String from;
		if (this.fromValue.isEmpty()) {
			PathPart partFrom = dataPath.remove();
			while (partFrom.isList) partFrom = dataPath.remove(); 
			from  = partFrom.part;
		} else {
			from = this.fromValue;
		}
		this.getTemplate().addReplace(from, to); 
	}
	
	/**
	 * Add replace values from a list
	 * @param context
	 * @throws MergeException
	 */
	private void replaceFromList(DataList dataList) throws MergeException {
		for (DataElement row : dataList) {
			if (row.isObject()) {
				DataObject orow = row.getAsObject();
				String fromValue = "";
				String toValue = "";
				if (!orow.containsKey(this.fromAttribute) || 
					!orow.containsKey(this.toAttribute)) { 
					switch (this.getListAttrMissing()) {
					case LIST_ATTR_MISSING_THROW :
						throw new Merge500("List from/to Attribute Missing " + this.getDataSource() + " in " + this.getTemplate().getDescription() + " at " + this.getName());
					case LIST_ATTR_MISSING_IGNORE :
						return;
					}
				}
				if (!orow.get(this.fromAttribute).isPrimitive() || 
					!orow.get(this.toAttribute).isPrimitive()) {
					switch (this.getListAttrNotPrimitive()) {
					case LIST_ATTR_NOT_PRIMITIVE_THROW :
						throw new Merge500("List from/to Attribute Missing " + this.getDataSource() + " in " + this.getTemplate().getDescription() + " at " + this.getName());
					case LIST_ATTR_NOT_PRIMITIVE_IGNORE :
						return;
					}
				}
				
				fromValue = row.getAsObject().get(this.fromAttribute).getAsPrimitive();
				toValue = row.getAsObject().get(this.toAttribute).getAsPrimitive();
				this.getTemplate().addReplace(fromValue, toValue);
			}
		}
	}
	
	/**
	 * Add replace values from a DataObject
	 * @param context
	 * @throws MergeException
	 */
	private void replaceFromObject(DataObject dataObject) throws MergeException {
		String from = "";
		String to = "";
		TreeMap<String, DataElement> ordered = new TreeMap<String, DataElement>(dataObject);
		for (Entry<String, DataElement> member: ordered.entrySet()) {
			if (member.getValue().isPrimitive()) {
				switch (this.getObjectAttrPrimitive()) {
				case OBJECT_ATTRIBUTE_PRIMITIVE_THROW :
					throw new Merge500("Object Attribute is Primitive " + this.getDataSource() + " in " + this.getTemplate().getDescription() + " at " + this.getName());
				case OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE :
					continue;
				case OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE :
					from = member.getKey();
					to = member.getValue().getAsPrimitive();
					this.getTemplate().addReplace(from, to);
				}
			} else if (member.getValue().isObject()) {
				switch (this.getObjectAttrObject()) {
				case OBJECT_ATTRIBUTE_OBJECT_THROW :
					throw new Merge500("Object Attribute is Object " + this.getDataSource() + " in " + this.getTemplate().getDescription() + " at " + this.getName());
				case OBJECT_ATTRIBUTE_OBJECT_IGNORE :
					continue;
				}
			} else if (member.getValue().isList()) {
				switch (this.getObjectAttrList()) {
				case OBJECT_ATTRIBUTE_LIST_THROW :
					throw new Merge500("Object Attribute is List " + this.getDataSource() + " in " + this.getTemplate().getDescription() + " at " + this.getName());
				case OBJECT_ATTRIBUTE_LIST_IGNORE :
					continue;
				case OBJECT_ATTRIBUTE_LIST_FIRST :
					if (member.getValue().getAsList().size() < 1) {
						continue; // No First Member
					}
					if (!member.getValue().getAsList().get(0).isPrimitive()) {
						throw new Merge500("Object Attribute List First is not primitive " + this.getDataSource() + " in " + this.getTemplate().getDescription() + " at " + this.getName());
					}
					from = member.getKey();
					to = member.getValue().getAsList().get(0).getAsPrimitive();
					this.getTemplate().addReplace(from, to);
					continue;
				case OBJECT_ATTRIBUTE_LIST_LAST :
					int last = member.getValue().getAsList().size();
					if (last < 1) {
						continue; // No Last Member
					}
					if (!member.getValue().getAsList().get(last-1).isPrimitive()) {
						throw new Merge500("Object Attribute List Last is not primitive " + this.getDataSource() + " in " + this.getTemplate().getDescription() + " at " + this.getName());
					}
					from = member.getKey();
					to = member.getValue().getAsList().get(last-1).getAsPrimitive();
					this.getTemplate().addReplace(from, to);
					continue;
				}
			}
		}
	}
	
	/*
	 * Simple getter / setters below here
	 */

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

	public boolean getProcessRequire() {
		return processRequire;
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

	public String getFromValue() {
		return fromValue;
	}

	public String getToValue() {
		return toValue;
	}

	/**
	 * @param toAttribute The attribute that has To values
	 */
	public void setToAttribute(String toAttribute) {
		this.toAttribute = toAttribute;
	}

	/**
	 * @param fromAttribute The attribute that has From values
	 */
	public void setFromAttribute(String fromAttribute) {
		this.fromAttribute = fromAttribute;
	}

	/**
	 * @param processAfter Process Replace Stack after adding values
	 */
	public void setProcessAfter(boolean processAfter) {
		this.processAfter = processAfter;
	}

	public void setProcessRequire(boolean processRequire) {
		this.processRequire = processRequire;
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

	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	@Override
	public void setIfSourceMissing(int value) {
		if (MISSING_OPTIONS().keySet().contains(value)) {
			super.setIfSourceMissing(value);
		}
	}

	@Override
	public void setIfPrimitive(int value) {
		if (PRIMITIVE_OPTIONS().keySet().contains(value)) {
			super.setIfPrimitive(value);
		}
	}

	@Override
	public void setIfObject(int value) {
		if (OBJECT_OPTIONS().keySet().contains(value)) {
			super.setIfObject(value);
		}
	}

	@Override
	public void setIfList(int value) {
		if (LIST_OPTIONS().keySet().contains(value)) {
			super.setIfList(value);
		}
	}

	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	/*
	 * Constants and Options below here
	 */
	/**
	 * Value for get/setIfMissing - If the data manager doesn't have the Source provided throw an exception and fail the merge
	 */
	public static final int MISSING_THROW 	= 1;
	/**
	 * Value for get/setIfMissing - If the data manager doesn't have the Source provided ignore this directive and continue the merge
	 */
	public static final int MISSING_IGNORE = 2;
	/**
	 * Value for get/setIfMissing - If the data manager doesn't have the Source provided use the toValue provided for the replace action
	 */
	public static final int MISSING_REPLACE = 3;
	/**
	 * Value for get/setIfMissing - If the data manager doesn't have the Source provided use a Java Generated GUID
	 */
	public static final int MISSING_GUID = 4;
	/**
	 * @return Hashmap of supported values for get/setIfMissing
	 */
	public static final HashMap<Integer, String> MISSING_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(MISSING_THROW, 	"throw");
		options.put(MISSING_IGNORE, 	"ignore");
		options.put(MISSING_REPLACE,	"replace");
		options.put(MISSING_GUID,  	"guidValue");
		return options;
	}
	
	/**
	 * Value used in get/setIfPrimitive value: If the data element identified by Source is a Primitive throw an exception and stop the merge
	 */
	public static final int PRIMITIVE_THROW 	= 1;
	/**
	 * Value used in get/setIfPrimitive value: If the data element identified by Source is a Primitive ignore the directive and continue the merge
	 */
	public static final int PRIMITIVE_IGNORE 	= 2;
	/**
	 * Value used in get/setIfPrimitive value: If the data element identified by Source is a Primitive replace the Source Name (last segment only) with the Primitive value
	 */
	public static final int PRIMITIVE_REPLACE 	= 3;
	/**
	 * Value used in get/setIfPrimitive value: If the data element identified by Source is a Primitive put the value as a Json String
	 */
	public static final int PRIMITIVE_JSON	 	= 4;
	/**
	 * @return The options for get/setIfPrimitive
	 */
	public static final HashMap<Integer, String> PRIMITIVE_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(PRIMITIVE_THROW, 	"throw");
		options.put(PRIMITIVE_IGNORE, 	"ignore");
		options.put(PRIMITIVE_REPLACE, 	"replace");
		options.put(PRIMITIVE_JSON, 	"replace with JSON");
		return options;
	}

	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object throw an exception and stop the merge
	 */
	public static final int OBJECT_THROW 	= 1;
	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object ignore the directive and continue the merge
	 */
	public static final int OBJECT_IGNORE 	= 2;
	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object replace all object attributes with their values
	 */
	public static final int OBJECT_REPLACE 	= 3;
	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object treat is as a list of 1 object and insert using List features
	 */
	public static final int OBJECT_REPLACE_LIST = 4;
	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object put the value as a Json String
	 */
	public static final int OBJECT_REPLACE_JSON = 5;
	/**
	 * @return The options for get/setIfObject
	 */
	public static final HashMap<Integer, String> OBJECT_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_THROW, 	"throw");
		options.put(OBJECT_IGNORE, 	"ignore");
		options.put(OBJECT_REPLACE, "replace object");
		options.put(OBJECT_REPLACE_LIST, "replace as list");
		options.put(OBJECT_REPLACE_JSON, 	"replace with JSON");
		return options;
	}

	/**
	 * Value used in get/setIfObjectAttrPrimitive value: If the Object replace sees an attribute with a primitive value throw and exception and stop the merge
	 */
	public static final int OBJECT_ATTRIBUTE_PRIMITIVE_THROW	= 1;
	/**
	 * Value used in get/setIfObjectAttrPrimitive value: If the Object replace sees an attribute with a primitive value ignore the directive and continue the merge
	 */
	public static final int OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE 	= 2;
	/**
	 * Value used in get/setIfObjectAttrPrimitive value: If the Object replace sees an attribute with a primitive value replace the attribute name with it's value
	 */
	public static final int OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE 	= 3;
	/**
	 * @return The options for get/setIfObjectAttrPrimitive
	 */
	public static final HashMap<Integer, String> OBJECT_ATTRIBUTE_PRIMITIVE_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_ATTRIBUTE_PRIMITIVE_THROW, 	"throw");
		options.put(OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE, 	"ignore");
		options.put(OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE, "insertList");
		return options;
	}

	/**
	 * Value used in get/setIfObjectAttrObject value: If the Object replace sees an attribute with a Object value throw and exception and stop the merge
	 */
	public static final int OBJECT_ATTRIBUTE_OBJECT_THROW	= 1;
	/**
	 * Value used in get/setIfObjectAttrObject value: If the Object replace sees an attribute with a Object value ignore this directive and continue with the merge
	 */
	public static final int OBJECT_ATTRIBUTE_OBJECT_IGNORE 	= 2;
	/**
	 * @return The options for get/setIfObjectAttrObject
	 */
	public static final HashMap<Integer, String> OBJECT_ATTRIBUTE_OBJECT_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_ATTRIBUTE_OBJECT_THROW, 	"throw");
		options.put(OBJECT_ATTRIBUTE_OBJECT_IGNORE, 	"ignore");
		return options;
	}

	/**
	 * Value used in get/setIfObjectAttrList value: If the Object replace sees an attribute with a List value throw and exception and stop the merge
	 */
	public static final int OBJECT_ATTRIBUTE_LIST_THROW 		= 1;
	/**
	 * Value used in get/setIfObjectAttrList value: If the Object replace sees an attribute with a List value ignore this directive and continue the merge
	 */
	public static final int OBJECT_ATTRIBUTE_LIST_IGNORE 		= 2;
	/**
	 * Value used in get/setIfObjectAttrList value: If the Object replace sees an attribute with a List use the first primitive member of that list
	 */
	public static final int OBJECT_ATTRIBUTE_LIST_FIRST 		= 3;
	/**
	 * Value used in get/setIfObjectAttrList value: If the Object replace sees an attribute with a List use the last primitive member of that list
	 */
	public static final int OBJECT_ATTRIBUTE_LIST_LAST 			= 4;
	/**
	 * @return The options for get/setIfObjectAttrList
	 */
	public static final HashMap<Integer, String> OBJECT_ATTRIBUTE_LIST_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_ATTRIBUTE_LIST_THROW, 	"throw");
		options.put(OBJECT_ATTRIBUTE_LIST_IGNORE, 	"ignore");
		options.put(OBJECT_ATTRIBUTE_LIST_FIRST, 	"use first primitive");
		options.put(OBJECT_ATTRIBUTE_LIST_LAST, 	"use last primitive");
		return options;
	}
	
	/**
	 * Value used in get/setIfList value: If the Source is a List throw an exception and end the merge
	 */
	public static final int LIST_THROW 		= 1;
	/**
	 * Value used in get/setIfList value: If the Source is a List ignore this directive and continue the merge
	 */
	public static final int LIST_IGNORE 	= 2;
	/**
	 * Value used in get/setIfList value: Add replace values based for each member of the list using the fromAttribute and toAttribute names for each object in the list
	 */
	public static final int LIST_REPLACE 	= 3;
	/**
	 * Value used in get/setIfList value: Add replace values based for the first member of the list
	 */
	public static final int LIST_FIRST 		= 4;
	/**
	 * Value used in get/setIfList value: Add replace values based for the last member of the list
	 */
	public static final int LIST_LAST 		= 5;
	/**
	 * Value used in get/setIfList value: Add a replace value with the list JSON string
	 */
	public static final int LIST_JSON 		= 6;
	/**
	 * @return The options for get/setIfList
	 */
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

	/**
	 * Value used in get/setIfListAttrMissing value: If the List member object does not have a From or To attribute throw an exception and stop the merge
	 */
	public static final int LIST_ATTR_MISSING_THROW 	= 1;
	/**
	 * Value used in get/setIfListAttrMissing value: If the List member object does not have a From or To attribute ignore this directive and continue the merge
	 */
	public static final int LIST_ATTR_MISSING_IGNORE 	= 2;
	/**
	 * @return The options for get/setIfListAttrMissing
	 */
	public static final HashMap<Integer, String> LIST_ATTR_MISSING_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_ATTR_MISSING_THROW, 	"throw");
		options.put(LIST_ATTR_MISSING_IGNORE, 	"ignore");
		return options;
	}

	/**
	 * Value used in get/setIfListAttrNotPrimitive value: If the List member From or To attribute is not a primitive throw an exception and stop the merge
	 */
	public static final int LIST_ATTR_NOT_PRIMITIVE_THROW 	= 1;
	/**
	 * Value used in get/setIfListAttrNotPrimitive value: If the List member From or To attribute is not a primitive ignore this directive and continue the merge
	 */
	public static final int LIST_ATTR_NOT_PRIMITIVE_IGNORE 	= 2;
	/**
	 * @return The options for get/setIfListAttrNotPrimitive
	 */
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
		options.put("If List", 					LIST_OPTIONS());
		options.put("List-Attr Missing", 		LIST_ATTR_MISSING_OPTIONS());
		options.put("List-Attr Not Primitive", 	LIST_ATTR_NOT_PRIMITIVE_OPTIONS());
		return options;
	}
}
