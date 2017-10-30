package com.ibm.util.merge.data;

import java.util.ArrayList;

public class Path {
	private ArrayList<PathPart> parts;
	private String separator;
	private String current;
	
	public Path(String path, String separator) {
		this.current = "";
		this.separator = separator;
		this.parts = new ArrayList<PathPart>();
		String[] stringParts = path.split(separator);
		for ( String part : stringParts ) {
        	parts.add(new PathPart(part));
		}
	}
	
	public Path(Path from) {
		this.current = from.current;
		this.separator = from.separator;
		this.parts = new ArrayList<PathPart>();
    	this.parts.addAll(from.parts);
	}
	
	public PathPart pop() {
		PathPart aPart = parts.remove(0);
		if (!current.isEmpty()) current += separator;
		current += aPart.part; 
		return aPart;
	}
	
	public void push(PathPart part) {
		this.parts.add(0, part);
	}
	
	public int size() {
		return parts.size();
	}

	public PathPart get(int index) {
		return parts.get(index);
	}

	public String getPath() {
		String getPath = "";
		for (PathPart part : parts) {
			getPath += part.asString() + this.separator;
		}
		if (getPath.length() >= this.separator.length() ) {
			getPath = getPath.substring(0, getPath.length() - this.separator.length());
		}
		return getPath;
	}
 
	public void add(String part) {
		parts.add(new PathPart(part));
	}

	public PathPart remove() {
		if (parts.size() > 0 ) {
			return parts.remove(parts.size()-1);
		} else return null;
	}
	
	public String getSeparator() {
		return separator;
	}
	
	public String getCurrent() {
		return current;
	}
	
}
