package com.ibm.util.merge.template;

public class TemplateName implements Comparable<TemplateName> {
	private final String collection;
	private final String name;
	private final String columnValue;
	public TemplateName(String collection, String name, String columnValue) {
		this.collection = collection;
		this.name = name;
		this.columnValue = columnValue;
	}
	public TemplateName(Template template) {
		this.collection = template.getCollection();
		this.name = template.getName();
		this.columnValue = template.getColumnValue();
	}
	public String getFullName() {
		return this.collection + "." + this.name + '.' + this.columnValue;
	}
	public String getCollection() {
		return collection;
	}
	public String getName() {
		return name;
	}
	public String getColumnValue() {
		return columnValue;
	}
	@Override
	public int compareTo(TemplateName that) {
		return this.getFullName().compareTo(that.getFullName());
	}
}
