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
import java.util.HashSet;
import java.util.Map.Entry;

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
 * The Class InsertDirective. Sub-classes of this directive are used 
 * to insert templates at specific bookmarks within a tempalte. 
 * retrieve data from a data source and place it into the Data storage
 * area of the merge context.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Insert extends AbstractDataDirective {
	public static final int MISSING_THROW 	= 1;
	public static final int MISSING_IGNORE 	= 2;
	public static final int MISSING_INSERT 	= 3;
	public HashMap<Integer, String> missingOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(MISSING_THROW, 	"throw");
		options.put(MISSING_IGNORE, "ignore");
		options.put(MISSING_INSERT, "insert");
		return options;
	}
	
	public static final int PRIMITIVE_THROW 	= 1;
	public static final int PRIMITIVE_IGNORE 	= 2;
	public static final int PRIMITIVE_INSERT 	= 3;
	public HashMap<Integer, String> primitiveOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(PRIMITIVE_THROW, 	"throw");
		options.put(PRIMITIVE_IGNORE, 	"ignore");
		options.put(PRIMITIVE_INSERT, 	"insert");
		return options;
	}

	public static final int OBJECT_THROW 			= 1;
	public static final int OBJECT_IGNORE 			= 2;
	public static final int OBJECT_INSERT_OBJECT 	= 3;
	public static final int OBJECT_INSERT_LIST 	= 4;
	public HashMap<Integer, String> objectOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_THROW, 			"throw");
		options.put(OBJECT_IGNORE, 			"ignore");
		options.put(OBJECT_INSERT_OBJECT, 	"insertObject");
		options.put(OBJECT_INSERT_LIST, 	"insertList");
		return options;
	}


	public static final int LIST_THROW 	= 1;
	public static final int LIST_IGNORE 	= 2;
	public static final int LIST_INSERT 	= 3;
	public HashMap<Integer, String> listOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_THROW, 	"throw");
		options.put(LIST_IGNORE, 	"ignore");
		options.put(LIST_INSERT, 	"insert");
		return options;
	}

	private HashSet<String> notFirst;
	private HashSet<String> notLast;
	private HashSet<String> onlyFirst;
	private HashSet<String> onlyLast;
	private String bookmarkPattern;
	
	public Insert() {
		this("", "-", Insert.MISSING_THROW,
			Insert.PRIMITIVE_THROW,
			Insert.OBJECT_THROW,
			Insert.LIST_THROW,
			new HashSet<String>(), 
			new HashSet<String>(), 
			new HashSet<String>(), 
			new HashSet<String>(), 
			".*",false
		);
	}
	
	public Insert(String source, String delimeter, int missing, int primitive, int object, int list, 
			HashSet<String> notFirst, HashSet<String> notLast, HashSet<String> onlyFirst, HashSet<String> onlyLast, 
			String pattern, Boolean remove) {
		super(source, delimeter, missing, primitive, object, list);
		this.setType(AbstractDirective.TYPE_INSERT);
		this.notFirst = new HashSet<String>(); this.notFirst.addAll(notFirst);
		this.notLast = 	new HashSet<String>(); this.notLast.addAll(notLast);
		this.onlyFirst = new HashSet<String>();this.onlyFirst.addAll(onlyFirst);
		this.onlyLast = new HashSet<String>(); this.onlyLast.addAll(onlyLast);
		this.bookmarkPattern = pattern;
	}
	
	@Override
	public Insert getMergable() {
		Insert mergable = new Insert();
		this.makeMergable(mergable);
		mergable.setType(TYPE_INSERT);
		mergable.setBookmarkPattern(bookmarkPattern);
		mergable.setDataSource(dataSource);
		mergable.setNotFirst(notFirst);
		mergable.setNotLast(notLast);
		mergable.setOnlyFirst(onlyFirst);
		mergable.setOnlyLast(onlyLast);
		mergable.setIfSourceMissing(this.getIfSourceMissing());
		mergable.setIfList(this.getIfList());
		mergable.setIfObject(this.getIfObject());
		mergable.setIfPrimitive(this.getIfPrimitive());
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
			case MISSING_INSERT:
				this.insertAtBookmarks(context, null, true, true);
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
			case PRIMITIVE_INSERT :
				this.insertFromString(context);
				return;
			}

		} else if (data.isObject()) {
			switch (this.getIfObject()) {
			case OBJECT_THROW :
				throw new Merge500("Object Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case OBJECT_IGNORE :
				return;
			case OBJECT_INSERT_OBJECT :
				this.insertFromObject(context);
				break;
			case OBJECT_INSERT_LIST :
				this.insertAtBookmarks(context, data, true, true);
				break;
			}
		
		} else if (data.isList()) {
			switch (this.getIfList()) {
			case LIST_THROW :
				throw new Merge500("List Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case LIST_IGNORE :
				return;
			case LIST_INSERT :
				this.insertFromList(context);
				break;
			}
		}
		
	}
		
	private void insertFromObject(Merger context) throws MergeException {
		int loopcount; int size;
		DataObject dataObject = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsObject();
		loopcount = 1; size = dataObject.entrySet().size();
		for (Entry<String, DataElement> member: dataObject.entrySet()) {
			if (member.getValue().isPrimitive()) {
				DataObject row = new DataObject();
				row.put("attribute",new DataPrimitive(member.getKey()));
				row.put("value", 	new DataPrimitive(member.getValue().getAsPrimitive()));
				this.insertAtBookmarks(context, row, (loopcount==1), (loopcount==size));
				loopcount++;
			}
		}
	}
			
	private void insertFromList(Merger context) throws MergeException {
		int loopcount; int size;
		DataList dataList = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsList();
		loopcount = 1; size = dataList.size();
		for (DataElement value : dataList) {
			this.insertAtBookmarks(context, value, (loopcount==1), (loopcount==size));
			loopcount++;
		}
	}
			
	private void insertFromString(Merger context) throws MergeException {
		String dataString = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsPrimitive();
		//  conditional insert?
		this.insertAtBookmarks(context, new DataPrimitive(dataString), true, true);
	}

	public void insertAtBookmarks(Merger context, DataElement value, boolean isFirst, boolean isLast) throws MergeException {
		if (context.getStackSize() > context.getConfig().getNestLimit()) {
			throw new Merge500("template insert recursion safety, merge stack size exceded");
		}

		context.getMergeData().pushContext(value);
		for (BookmarkSegment bookmark : this.template.getMergeContent().getBookmarks()) {
			if (bookmark.getBookmarkName().matches(this.bookmarkPattern)) {
				Template subTemplate = context.getMergable(
						bookmark.getTemplateShorthand(value), 
						bookmark.getDefaultShorthand(), 
						this.template.getReplaceStack());
				subTemplate.blankReplace((isFirst ? this.notFirst : this.onlyFirst));
				subTemplate.blankReplace((isLast ? this.notLast : this.onlyLast));
				bookmark.insert(subTemplate.getMergeContent());
				context.getMergeData().popContext();
			}
		}
	}
	
	/*
	 * Simple Getters / Setters / Enumerator Constants below here
	 */
	public String getBookmarkPattern() {
		return bookmarkPattern;
	}

	public void setBookmarkPattern(String bookmarkPattern) {
		this.bookmarkPattern = bookmarkPattern;
	}

	public HashSet<String> getNotFirst() {
		return notFirst;
	}

	public void setNotFirst(HashSet<String> notFirst) {
		this.notFirst.clear();
		this.notFirst.addAll(notFirst);
	}

	public HashSet<String> getNotLast() {
		return notLast;
	}

	public void setNotLast(HashSet<String> notLast) {
		this.notLast.clear();
		this.notLast.addAll(notLast);
	}

	public HashSet<String> getOnlyFirst() {
		return onlyFirst;
	}

	public void setOnlyFirst(HashSet<String> onlyFirst) {
		this.onlyFirst.clear();
		this.onlyFirst.addAll(onlyFirst);
	}

	public HashSet<String> getOnlyLast() {
		return onlyLast;
	}

	public void setOnlyLast(HashSet<String> onlyLast) {
		this.onlyLast.clear();
		this.onlyLast.addAll(onlyLast);
	}

}
