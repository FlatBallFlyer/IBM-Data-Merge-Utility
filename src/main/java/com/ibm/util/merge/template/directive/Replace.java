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
import java.util.Map.Entry;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.Path;
import com.ibm.util.merge.data.PathPart;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * The Class ReplaceDirective. Sub-Classes of this directive place a series of 
 * values onto the Merge Replace Stack used to replace key values in the template
 * with data from the Merge Context.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Replace extends AbstractDataDirective {
	private String fromAttribute;	// used by List
	private String toAttribute;		// used by List
	private boolean processAfter;
	private int repalceRepeat = 1;

	public static final int MISSING_THROW 	= 1;
	public static final int MISSING_IGNORE = 2;
	public HashMap<Integer, String> missingOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(MISSING_THROW, 		"throw");
		options.put(MISSING_IGNORE, 	"ignore");
		return options;
	}
	
	public static final int PRIMITIVE_THROW 	= 1;
	public static final int PRIMITIVE_IGNORE 	= 2;
	public static final int PRIMITIVE_REPLACE 	= 3;
	public HashMap<Integer, String> primitiveOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(PRIMITIVE_THROW, 	"throw");
		options.put(PRIMITIVE_IGNORE, 	"ignore");
		options.put(PRIMITIVE_REPLACE, 	"replace");
		return options;
	}

	public static final int OBJECT_THROW 	= 1;
	public static final int OBJECT_IGNORE 	= 2;
	public static final int OBJECT_REPLACE = 3;
	public HashMap<Integer, String> objectOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(OBJECT_THROW, 	"throw");
		options.put(OBJECT_IGNORE, 	"ignore");
		options.put(OBJECT_REPLACE, "insertList");
		return options;
	}

	public static final int LIST_THROW 	= 1;
	public static final int LIST_IGNORE 	= 2;
	public static final int LIST_REPLACE 	= 3;
	public HashMap<Integer, String> listOptions() {
		HashMap<Integer, String> options = new HashMap<Integer, String>();
		options.put(LIST_THROW, 	"throw");
		options.put(LIST_IGNORE, 	"ignore");
		options.put(LIST_REPLACE, 	"replace");
		return options;
	}

	public Replace() {
		this (
			"", "-", 
			MISSING_THROW,
			PRIMITIVE_THROW,
			OBJECT_THROW,
			LIST_THROW,
			true,
			1
		);
	}

	public Replace(String source, String delimeter, int missing, int primitive, int object, int list, boolean process, int repeat) {
		super(source, delimeter, missing, primitive, object, list);
		this.type = AbstractDirective.TYPE_REPLACE;
		this.processAfter 	= process;
		this.repalceRepeat 	= repeat;
	}

	@Override
	public AbstractDirective getMergable() {
		Replace mergable = new Replace();
		this.makeMergable(mergable);
		mergable.setFromAttribute(this.fromAttribute);
		mergable.setToAttribute(this.toAttribute);
		mergable.setProcessAfter(this.processAfter);
		mergable.setRepalceRepeat(this.repalceRepeat);
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
				this.replaceFromString(context);
				break;
			}

		} else if (data.isObject()) {
			switch (this.getIfObject()) {
			case OBJECT_THROW :
				throw new Merge500("Object Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case OBJECT_IGNORE :
				return;
			case OBJECT_REPLACE:
				this.replaceFromObject(context);
				break;
			}
		
		} else if (data.isList()) {
			switch (this.getIfList()) {
			case LIST_THROW :
				throw new Merge500("List Data found for " + this.dataSource + " in " + this.template.getDescription() + " at " + this.getName());
			case LIST_IGNORE :
				return;
			case LIST_REPLACE :
				this.replaceFromList(context);
				break;
			}
		}
		
		if (this.processAfter) {
			template.getMergeContent().replace(template.getReplaceStack(), true, Config.MAX_NEST); // TODO: Soft Fail
		}
	}
	
	private void replaceFromString(Merger context) throws MergeException {
		String dataString = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsPrimitive();
		Path dataPath = new Path(this.dataSource, this.dataDelimeter);
		PathPart from = dataPath.remove();
		while (from.isList) from = dataPath.remove(); 
		this.template.addReplace(from.part, dataString); 
	}
	
	private void replaceFromList(Merger context) throws MergeException {
		DataList dataList = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsList();
		for (DataElement row : dataList) {
			if (row.isObject()) {
				DataObject orow = row.getAsObject();
				String fromValue = "";
				String toValue = "";
				if (orow.containsKey(this.fromAttribute) && 
						orow.get(this.fromAttribute).isPrimitive() && 
						orow.containsKey(this.toAttribute) && 
						orow.get(this.toAttribute).isPrimitive()) {
					fromValue = row.getAsObject().get(this.fromAttribute).getAsPrimitive();
					toValue = row.getAsObject().get(this.toAttribute).getAsPrimitive();
					this.template.addReplace(fromValue, toValue); 
				}
			}
		}
	}
	
	private void replaceFromObject(Merger context) throws MergeException {
		DataObject dataObject = context.getMergeData().get(this.dataSource, this.dataDelimeter).getAsObject();
		for (Entry<String, DataElement> member: dataObject.entrySet()) {
			if (member.getValue().isPrimitive()) {
				String from = member.getKey();
				String to = member.getValue().getAsPrimitive();
				template.addReplace(from, to);
			} else if (member.getValue().isList() && member.getValue().getAsList().get(0).isPrimitive()) {
				String from = member.getKey();
				String to = member.getValue().getAsList().get(0).getAsPrimitive();
				template.addReplace(from, to);
			}
		}
	}

	public String getToAttribute() {
		return toAttribute;
	}

	public void setToAttribute(String toAttribute) {
		this.toAttribute = toAttribute;
	}

	public String getFromAttribute() {
		return fromAttribute;
	}

	public void setFromAttribute(String fromAttribute) {
		this.fromAttribute = fromAttribute;
	}

	public boolean getProcessAfter() {
		return processAfter;
	}

	public void setProcessAfter(boolean processAfter) {
		this.processAfter = processAfter;
	}

	public int getRepalceRepeat() {
		return repalceRepeat;
	}

	public void setRepalceRepeat(int repalceRepeat) {
		this.repalceRepeat = repalceRepeat;
	}

}
