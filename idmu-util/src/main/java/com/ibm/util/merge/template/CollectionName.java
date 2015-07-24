package com.ibm.util.merge.template;

public class CollectionName implements Comparable<CollectionName>{
	private final String collection;
	public CollectionName(String col) { 
		collection = col; 
	}
	
	public String getCollection() { 
		return collection; 
	}
	
	@Override
	public int compareTo(CollectionName o) {
		return this.getCollection().compareTo(o.getCollection());
	}
}
