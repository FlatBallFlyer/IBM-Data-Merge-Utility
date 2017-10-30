package com.ibm.util.merge.data;

import java.util.ArrayList;
import java.util.Map;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class DataManager {
	private DataObject data = new DataObject();;
	private ArrayList<DataElement> contextStack;
	
	public DataManager() {
		contextStack = new ArrayList<DataElement>();
	}
	
	public int size() {
		return data.entrySet().size();
	}
	
	public void clear() {
		this.data = new DataObject();
	}
	
	public void pushContext(DataElement context) {
		contextStack.add(context);
	}
	
	public void popContext() {
		contextStack.remove(contextStack.size()-1);
	}
	
	public int contextStackSize() {
		return contextStack.size();
	}
	
	public boolean contians(String address, String delimiter) {
		Path path = new Path(address, delimiter);
		if (address.equals(Merger.IDMU_CONTEXT)) {
			return (this.contextStack.size() != 0);
		}

		try {
			getElement(path,0);
		} catch (MergeException e) {
			return false;
		}
		return true;
	}
	
	public DataElement get(String address, String delimiter) throws MergeException {
		if (address.equals(Merger.IDMU_CONTEXT)) {
			if (contextStack.isEmpty()) {
				throw new Merge500("Context EMPTY!");
			}
			return this.contextStack.get(contextStack.size()-1);
		}
		Path path = new Path(address, delimiter);
		DataElement element = getElement(path,0);
		return element;
	}
	
	public void put(String address, String delimiter, DataElement value) throws MergeException {
		Path path = new Path(address, delimiter);
		DataElement entry;
		if (this.contians(address, delimiter)) {
			entry = getElement(path, 0);
			entry = entry.makeArray();
			entry.getAsList().add(value);
		} else {
			entry = getElement(path, 1);
			PathPart part = path.pop();
			if (entry.isObject()) {
				entry.getAsObject().put(part.part, value);
			} else if (entry.isList()) {
				entry.getAsList().add(part.index, value);
			} else {
				entry = entry.makeArray();
				entry.getAsList().add(value);
			}
		}
	}
	
	public void put(String address, String delimiter, Map<String, String[]> parameterMap) throws MergeException {
		DataObject parameters = new DataObject();
		for (String parameter : parameterMap.keySet()) {
			DataList values = new DataList(); 
			for (String value : parameterMap.get(parameter)) {
				values.add(new DataPrimitive(value));
			}
			parameters.put(parameter, values);
		}
		this.put(address, delimiter, parameters);
	}

	public void put(String address, String delimiter, String value) throws MergeException {
		DataPrimitive primitiveValue = new DataPrimitive(value);
		this.put(address, delimiter, primitiveValue);
	}

	public void put(Path path, DataElement value) throws MergeException {
		put(path.getPath(), path.getSeparator(), value);
	}
	
	private DataElement getElement(Path path, int to) throws MergeException {
		DataElement entry = data;
		while (path.size() > to) {
			PathPart part = path.pop();
			if (part.isList) {
				if (!entry.isList()) throw new Merge500("Array not found at " + path.getCurrent() + " in " + path.getPath());
				if (entry.getAsList().size()-1 < part.index) throw new Merge500("Array to small at:" + path.getCurrent() + " in " + path.getPath());
				entry = entry.getAsList().get(part.index);
			} else {
				if (!entry.isObject()) throw new Merge500("Object not found at " + path.getCurrent() + " in " + path.getPath());
				if (!entry.getAsObject().containsKey(part.part)) throw new Merge500("Object does not have attribute:" + part.part + " at:" + path.getCurrent() + " in " + path.getPath());
				entry = entry.getAsObject().get(part.part);
			}
		}
		return entry;
	}

}

