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
import java.util.HashSet;
import java.util.Map.Entry;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.BookmarkSegment;

/**
 * Insert Sub-Templates at Bookmarks based on data in the Data Store
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Insert extends AbstractDataDirective {

	/**
	 * List of tags that are emptied on the first insert 
	 */
	private HashSet<String> notFirst;
	/**
	 * List of tags that are emptied on the last insert 
	 */
	private HashSet<String> notLast;
	/**
	 * List of tags that are emptied on all but the first insert 
	 */
	private HashSet<String> onlyFirst;
	/**
	 * List of tags that are emptied on all but the last insert 
	 */
	private HashSet<String> onlyLast;
	/**
	 * Java Regex pattern that identifies the bookmarks where content will be inserted
	 */
	private String bookmarkPattern;
	/**
	 * If Primitive Compare operator an IfPrimitive sub-operator see Insert.INSERT_IF_*
	 */
	private int ifOperator;
	/**
	 * The value used by the IfOperator comparison
	 */
	private String ifValue;
	
	/**
	 * Instantiate an Insert Directive with default values 
	 */
	public Insert() {
		this("", "-", Insert.MISSING_IGNORE,
			Insert.PRIMITIVE_IGNORE,
			Insert.OBJECT_IGNORE,
			Insert.LIST_IGNORE,
			new HashSet<String>(), 
			new HashSet<String>(), 
			new HashSet<String>(), 
			new HashSet<String>(), 
			".*",
			Insert.INSERT_IF_STRING_EQUALS,
			""
		);
	}
	
	/**
	 * Instantiate a Insert Directive with the provided values
	 * @param source The Data Source
	 * @param delimeter The Source path delimiter
	 * @param missing The If Missing option
	 * @param primitive The if Primitive option
	 * @param object The if Object option
	 * @param list The if List option
	 * @param notFirst The Not First tags
	 * @param notLast The Not Last tags
	 * @param onlyFirst The only first tags
	 * @param onlyLast The only last tags
	 * @param pattern the bookmark pattern to match
	 * @param ifOperator The if primitive comparison operator option
	 * @param ifValue The if primitive comparison value
	 */
	public Insert(String source, String delimeter, int missing, int primitive, int object, int list, 
			HashSet<String> notFirst, HashSet<String> notLast, HashSet<String> onlyFirst, HashSet<String> onlyLast, 
			String pattern, int ifOperator, String ifValue) {
		super(source, delimeter, missing, primitive, object, list);
		this.setType(AbstractDirective.TYPE_INSERT);
		this.notFirst = new HashSet<String>(); this.notFirst.addAll(notFirst);
		this.notLast = 	new HashSet<String>(); this.notLast.addAll(notLast);
		this.onlyFirst = new HashSet<String>();this.onlyFirst.addAll(onlyFirst);
		this.onlyLast = new HashSet<String>(); this.onlyLast.addAll(onlyLast);
		this.bookmarkPattern = pattern;
		this.ifOperator = ifOperator;
		this.ifValue = ifValue;
	}
	
	@Override
	public void cachePrepare(Template template) throws MergeException {
		super.cachePrepare(template);
		
		// Validate enums
		if (!Insert.MISSING_OPTIONS().containsKey(this.getIfSourceMissing())) {
			throw new Merge500("Invalide Source Missing Option:" + Integer.toString(this.getIfSourceMissing()));
		}
		
		if (!Insert.PRIMITIVE_OPTIONS().containsKey(this.getIfPrimitive())) {
			throw new Merge500("Invalide If Primitive Option:" + Integer.toString(this.getIfPrimitive()));
		}
		
		if (!Insert.LIST_OPTIONS().containsKey(this.getIfList())) {
			throw new Merge500("Invalide If List Option:" + Integer.toString(this.getIfList()));
		}

		if (!Insert.OBJECT_OPTIONS().containsKey(this.getIfObject())) {
			throw new Merge500("Invalide If Object Option:" + Integer.toString(this.getIfObject()));
		}

		if (!Insert.INSERT_IF_OPERATORS().containsKey(this.getIfOperator())) {
			throw new Merge500("Invalide Insert If Operator:" + Integer.toString(this.getIfOperator()));
		}

	}

	@Override
	public Insert getMergable() throws MergeException {
		Insert mergable = new Insert();
		this.makeMergable(mergable);
		mergable.setType(TYPE_INSERT);
		mergable.setBookmarkPattern(bookmarkPattern);
		mergable.setDataSource(this.getDataSource());
		mergable.setSourceContent(this.getSourceContent().getMergable());
		mergable.setNotFirst(notFirst);
		mergable.setNotLast(notLast);
		mergable.setOnlyFirst(onlyFirst);
		mergable.setOnlyLast(onlyLast);
		mergable.setIfSourceMissing(this.getIfSourceMissing());
		mergable.setIfList(this.getIfList());
		mergable.setIfObject(this.getIfObject());
		mergable.setIfPrimitive(this.getIfPrimitive());
		mergable.setIfOperator(this.ifOperator);
		mergable.setIfValue(this.ifValue);
		return mergable;
	}
	
	@Override
	public void execute(Merger context) throws MergeException {
		this.getSourceContent().replace(this.getTemplate().getReplaceStack(), true, Config.nestLimit());
		String source = this.getSourceContent().getValue();
		if (!context.getMergeData().contians(source, this.getDataDelimeter())) {
			switch (this.getIfSourceMissing()) {
			case MISSING_THROW :
				throw new Merge500("Source Data Missing for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
			case MISSING_IGNORE :
				return;
			case MISSING_INSERT:
				this.insertAtBookmarks(context, null, true, true);
				return;
			}
		}
		
		DataElement data = context.getMergeData().get(source, this.getDataDelimeter());
		DataList 	list;
		if (data.isPrimitive()) {
			switch (this.getIfPrimitive()) {
			case PRIMITIVE_THROW :
				throw new Merge500("Primitive Data found for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
			case PRIMITIVE_IGNORE :
				return;
			case PRIMITIVE_INSERT :
				this.insertFromPrimitive(context, (DataPrimitive) data);
				return;
			case PRIMITIVE_INSERT_IF :
				Double dbl = new Double(0); 
				Double cmp = new Double(0);
				String value = data.getAsPrimitive();
				try {
					cmp = Double.valueOf(this.ifValue);
					dbl = Double.valueOf(value);
				} catch (Throwable e) {
					// ok
				}
				switch (this.getIfOperator()) {
				case INSERT_IF_STRING_EQUALS :
					if (value.equals(this.ifValue)) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					} 
					return;
				case INSERT_IF_STRING_EMPTY:
					if (value.isEmpty()) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					} 
					return;
				case INSERT_IF_STRING_NOT_EMPTY :
					if (!value.isEmpty()) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					} 
					return;
				case INSERT_IF_STRING_GT : 
					if (this.ifValue.compareTo(value) > 0) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					}
					return;
				case INSERT_IF_STRING_LT : 
					if (this.ifValue.compareTo(value) < 0) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					}
					return;
				case INSERT_IF_VALUE_EQUALS : 
					if (dbl.equals(cmp)) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					}
					return;
				case INSERT_IF_VALUE_GT : 
					if (dbl.compareTo(cmp) < 0) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					}
					return;
				case INSERT_IF_VALUE_LT : 
					if (dbl.compareTo(cmp) > 0) {
						this.insertFromPrimitive(context, (DataPrimitive) data);
					}
					return;
				}
				this.insertFromPrimitive(context, (DataPrimitive) data);
				return;
			}

		} else if (data.isObject()) {
			switch (this.getIfObject()) {
			case OBJECT_THROW :
				throw new Merge500("Object Data found for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
			case OBJECT_IGNORE :
				return;
			case OBJECT_INSERT_OBJECT :
				this.insertFromObject(context, data.getAsObject());
				break;
			case OBJECT_INSERT_LIST :
				this.insertAtBookmarks(context, data, true, true);
				break;
			}
		
		} else if (data.isList()) {
			switch (this.getIfList()) {
			case LIST_THROW :
				throw new Merge500("List Data found for " + source + " in " + this.getTemplate().getDescription() + " at " + this.getName());
			case LIST_IGNORE :
				return;
			case LIST_INSERT_FIRST :
				list = new DataList();
				if (data.getAsList().size() > 0) {
					list.add(data.getAsList().get(0));
				}
				data = list;
			case LIST_INSERT_LAST :
				list = new DataList();
				if (data.getAsList().size() > 0) {
					list.add(data.getAsList().get(data.getAsList().size()-1));
				}
				data = list;
			case LIST_INSERT :
				this.insertFromList(context, data.getAsList());
				break;
			}
		}
		
	}
		
	/**
	 * Insert a sub-template for each attribute in a DataObject
	 * @param context
	 * @throws MergeException
	 */
	private void insertFromObject(Merger context, DataObject dataObject) throws MergeException {
		int loopcount; int size;
		loopcount = 1; size = dataObject.entrySet().size();
		for (Entry<String, DataElement> member: dataObject.entrySet()) {
			DataObject row = new DataObject();
			row.put("attribute",new DataPrimitive(member.getKey()));
			row.put("value", 	member.getValue());
			this.insertAtBookmarks(context, row, (loopcount==1), (loopcount==size));
			loopcount++;
		}
	}
			
	/**
	 * Insert a sub-template for each member of a DataList
	 * @param context
	 * @throws MergeException
	 */
	private void insertFromList(Merger context, DataList dataList) throws MergeException {
		int loopcount; int size;
		loopcount = 1; size = dataList.size();
		for (DataElement value : dataList) {
			this.insertAtBookmarks(context, value, (loopcount==1), (loopcount==size));
			loopcount++;
		}
	}
			
	/**
	 * Insert a sub-template based on a primitive value
	 * @param context
	 * @throws MergeException
	 */
	private void insertFromPrimitive(Merger context, DataPrimitive from) throws MergeException {
		String dataString = from.getAsPrimitive();
		//  conditional insert?
		this.insertAtBookmarks(context, new DataPrimitive(dataString), true, true);
	}

	/**
	 * Perform sub-template insert at the specified bookmarks.
	 * @param context The Merge Context
	 * @param value The new data context for the merge
	 * @param isFirst First member indicator
	 * @param isLast Last member indicator
	 * @throws MergeException on processing errors
	 */
	public void insertAtBookmarks(Merger context, DataElement value, boolean isFirst, boolean isLast) throws MergeException {
		if (context.getStackSize() > Config.insertLimit()) {
			throw new Merge500("template insert recursion safety, merge stack size exceded");
		}

		for (BookmarkSegment bookmark : this.getTemplate().getMergeContent().getBookmarks()) {
			if (bookmark.getBookmarkName().matches(this.bookmarkPattern)) {
				Template subTemplate = context.getMergable(
						bookmark.getTemplateShorthand(value), 
						bookmark.getDefaultShorthand(), 
						this.getTemplate().getReplaceStack());
				context.pushTemplate(subTemplate.getId().shorthand(), value);
				subTemplate.blankReplace((isFirst ? this.notFirst : this.onlyFirst));
				subTemplate.blankReplace((isLast ? this.notLast : this.onlyLast));
				bookmark.insert(subTemplate.getMergedOutput());
				int size = context.getStackSize();
				context.popTemplate();
				if (!(context.getStackSize() < size)) throw new Merge500("pop didn't work");
			}
		}
	}
	
	/*
	 * Simple Getters / Setters / Enumerator Constants below here
	 */
	/**
	 * @return bookmark name match pattern
	 */
	public String getBookmarkPattern() {
		return bookmarkPattern;
	}

	/**
	 * @param bookmarkPattern The bookmark pattern
	 */
	public void setBookmarkPattern(String bookmarkPattern) {
		this.bookmarkPattern = bookmarkPattern;
	}

	/**
	 * @return Tags to be blank on the first insert
	 */
	public HashSet<String> getNotFirst() {
		return notFirst;
	}

	/**
	 * @param notFirst Not First tag list
	 */
	public void setNotFirst(HashSet<String> notFirst) {
		this.notFirst.clear();
		this.notFirst.addAll(notFirst);
	}

	/**
	 * @return list of tags to be blank on the last insert
	 */
	public HashSet<String> getNotLast() {
		return notLast;
	}

	/**
	 * @param notLast Not Last tag list
	 */
	public void setNotLast(HashSet<String> notLast) {
		this.notLast.clear();
		this.notLast.addAll(notLast);
	}

	/**
	 * @return list of tags to be blank on all but first insert
	 */
	public HashSet<String> getOnlyFirst() {
		return onlyFirst;
	}

	/**
	 * @param onlyFirst Only First tag list
	 */
	public void setOnlyFirst(HashSet<String> onlyFirst) {
		this.onlyFirst.clear();
		this.onlyFirst.addAll(onlyFirst);
	}

	/**
	 * @return list of tags to be blank on all but last insert
	 */
	public HashSet<String> getOnlyLast() {
		return onlyLast;
	}

	/**
	 * @param onlyLast Tag List
	 */
	public void setOnlyLast(HashSet<String> onlyLast) {
		this.onlyLast.clear();
		this.onlyLast.addAll(onlyLast);
	}

	@Override
	public void setIfSourceMissing(int value) {
		if (Insert.MISSING_OPTIONS().keySet().contains(new Integer(value))) {
			super.setIfSourceMissing(value);
		}
	}

	@Override
	public void setIfPrimitive(int value) {
		if (Insert.PRIMITIVE_OPTIONS().keySet().contains(new Integer(value))) {
			super.setIfPrimitive(value);
		}
	}

	@Override
	public void setIfObject(int value) {
		if (Insert.OBJECT_OPTIONS().keySet().contains(new Integer(value))) {
			super.setIfObject(value);
		}
	}

	@Override
	public void setIfList(int value) {
		if (Insert.LIST_OPTIONS().keySet().contains(new Integer(value))) {
			super.setIfList(value);
		}
	}

	public int getIfOperator() {
		return ifOperator;
	}

	public String getIfValue() {
		return ifValue;
	}

	public void setIfOperator(int ifOperator) {
		this.ifOperator = ifOperator;
	}

	public void setIfValue(String ifValue) {
		this.ifValue = ifValue;
	}

	/*
	 * Static Constants and Options below here
	 */
	/**
	 * Value for get/setIfMissing - If the data manager doesn't have the Source provided throw an exception and fail the merge
	 */
	public static final int MISSING_THROW 	= 1;
	/**
	 * Value for get/setIfMissing - If the data manager doesn't have the Source provided ignore this directive and continue with the Merge
	 */
	public static final int MISSING_IGNORE 	= 2;
	/**
	 * Value for get/setIfMissing - If the data manager doesn't have the Source provided perform a single insert with a null context
	 */
	public static final int MISSING_INSERT 	= 3;
	/**
	 * @return Hashmap of supported values for get/setIfMissing
	 */
	public static final HashMap<Integer, String> MISSING_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(MISSING_THROW, 	"throw");
		options.put(MISSING_IGNORE, "ignore");
		options.put(MISSING_INSERT, "insert");
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
	 * Value used in get/setIfPrimitive value: If the data element identified by Source is a Primitive perform a single insert with the Primitive context
	 */
	public static final int PRIMITIVE_INSERT 	= 3;
	/**
	 * Value used in get/setIfPrimitive value: If the data element identified by Source is a Primitive conditionally insert one sub-template based on IfOperator and Value
	 */
	public static final int PRIMITIVE_INSERT_IF	= 4;
	/**
	 * @return The options for get/setIfPrimitive
	 */
	public static final HashMap<Integer, String> PRIMITIVE_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(PRIMITIVE_THROW, 	"throw");
		options.put(PRIMITIVE_IGNORE, 	"ignore");
		options.put(PRIMITIVE_INSERT, 	"insert");
		options.put(PRIMITIVE_INSERT_IF, "insert if");
		return options;
	}

	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the value provided is String.equals() the primitive value perform 1 insert 
	 */
	public static final int INSERT_IF_STRING_EQUALS 	= 1;
	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the primitive is an empty string perform 1 insert
	 */
	public static final int INSERT_IF_STRING_EMPTY 		= 2;
	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the primitive is not an empty string perform 1 insert
	 */
	public static final int INSERT_IF_STRING_NOT_EMPTY 	= 3;
	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the primitive is greater than the value provided perform 1 insert
	 */
	public static final int INSERT_IF_STRING_GT			= 4;
	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the primitive is less than the value provided perform 1 insert
	 */
	public static final int INSERT_IF_STRING_LT			= 5;
	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the value of the primitive equals the value of the string provided perform 1 insert
	 */
	public static final int INSERT_IF_VALUE_EQUALS 		= 6;
	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the value of the primitive is greater than the value of the string provided perform 1 insert
	 */
	public static final int INSERT_IF_VALUE_GT			= 7;
	/**
	 * Value used in ifPrimitive-insertIfOperator value: If the value of the primitive is less than the value of the string provided perform 1 insert
	 */
	public static final int INSERT_IF_VALUE_LT			= 8;
	/**
	 * @return The options for get/setIfOperator
	 */
	public static final HashMap<Integer, String> INSERT_IF_OPERATORS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(INSERT_IF_STRING_EQUALS,	"string equals");
		options.put(INSERT_IF_STRING_EMPTY, 	"string is empty");
		options.put(INSERT_IF_STRING_NOT_EMPTY, "string not empty");
		options.put(INSERT_IF_STRING_GT, 		"string >");
		options.put(INSERT_IF_STRING_LT, 		"string <");
		options.put(INSERT_IF_VALUE_EQUALS, 	"value =");
		options.put(INSERT_IF_VALUE_GT, 		"value >");
		options.put(INSERT_IF_VALUE_LT, 		"value <");
		return options;
	}

	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object throw an exception and stop the merge
	 */
	public static final int OBJECT_THROW 			= 1;
	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object ignore this directive and continue the merge
	 */
	public static final int OBJECT_IGNORE 			= 2;
	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object perform an insert for each attribute of the object
	 */
	public static final int OBJECT_INSERT_OBJECT 	= 3;
	/**
	 * Value used in get/setIfObject value: If the data element identified by Source is an Object perform an insert as List for a single object
	 */
	public static final int OBJECT_INSERT_LIST 	= 4;
	/**
	 * @return The options for get/setIfOjbect
	 */
	public static final HashMap<Integer, String> OBJECT_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_THROW, 			"throw");
		options.put(OBJECT_IGNORE, 			"ignore");
		options.put(OBJECT_INSERT_OBJECT, 	"insertObject");
		options.put(OBJECT_INSERT_LIST, 	"insertList");
		return options;
	}

	/**
	 * Value used in get/setIfList value: If the data element identified by Source is an List throw an exception and stop the merge
	 */
	public static final int LIST_THROW 			= 1;
	/**
	 * Value used in get/setIfList value: If the data element identified by Source is an List ignore this directive and continue the merge
	 */
	public static final int LIST_IGNORE 		= 2;
	/**
	 * Value used in get/setIfList value: If the data element identified by Source is an List insert subtemplates for each member of the list
	 */
	public static final int LIST_INSERT 		= 3;
	/**
	 * Value used in get/setIfList value: If the data element identified by Source is an List insert one subtemplate for the first member of the list
	 */
	public static final int LIST_INSERT_FIRST 	= 4;
	/**
	 * Value used in get/setIfList value: If the data element identified by Source is an List insert one subtemplate for the last member of the list
	 */
	public static final int LIST_INSERT_LAST 	= 5;
	/**
	 * @return The options for get/setIfList
	 */
	public static final HashMap<Integer, String> LIST_OPTIONS() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_THROW, 		"throw");
		options.put(LIST_IGNORE, 		"ignore");
		options.put(LIST_INSERT, 		"insert");
		options.put(LIST_INSERT_FIRST, 	"insert first member");
		options.put(LIST_INSERT_LAST, 	"insert last member");
		return options;
	}

	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		options.put("Source Missing", 	MISSING_OPTIONS());
		options.put("If Primitive", 	PRIMITIVE_OPTIONS());
		options.put("If Object", 		OBJECT_OPTIONS());
		options.put("If List", 			LIST_OPTIONS());
		options.put("Insert If operators", INSERT_IF_OPERATORS());
		return options;
	}
}
