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
package com.ibm.util.merge.data;

import java.util.ArrayList;

/**
 * Represents a path to an element of the Data Manger. 
 * 
 * @author Mike Storey
 *
 */
public class Path {
	private ArrayList<PathPart> parts;
	private String separator;
	private String current;
	
	/**
	 * Instantiate a new Path from value/delimiter pair
	 * 
	 * @param path
	 * @param separator
	 */
	public Path(String path, String separator) {
		this.current = "";
		this.separator = separator;
		this.parts = new ArrayList<PathPart>();
		String[] stringParts = path.split(separator);
		for ( String part : stringParts ) {
        	parts.add(new PathPart(part));
		}
	}
	
	/**
	 * Clone a path
	 * 
	 * @param from
	 */
	public Path(Path from) {
		this.current = from.current;
		this.separator = from.separator;
		this.parts = new ArrayList<PathPart>();
    	this.parts.addAll(from.parts);
	}
	
	/**
	 * @return the last path part
	 */
	public PathPart pop() {
		PathPart aPart = parts.remove(0);
		if (!current.isEmpty()) current += separator;
		current += aPart.part; 
		return aPart;
	}
	
	/**
	 * Add a path part
	 * 
	 * @param part
	 */
	public void push(PathPart part) {
		this.parts.add(0, part);
	}
	
	/**
	 * @return path size (number of parts)
	 */
	public int size() {
		return parts.size();
	}

	/**
	 * @param index
	 * @return the path part
	 */
	public PathPart get(int index) {
		return parts.get(index);
	}

	/**
	 * @return the full path
	 */
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
 
	/**
	 * Add a path part
	 * @param part
	 */
	public void add(String part) {
		parts.add(new PathPart(part));
	}

	/**
	 * @return and remove the last member of the parts 
	 */
	public PathPart remove() {
		if (parts.size() > 0 ) {
			return parts.remove(parts.size()-1);
		} else return null;
	}
	
	/**
	 * @return and remove the indicated member of the parts 
	 */
	public PathPart remove(int index) {
		if (parts.size() > index ) {
			return parts.remove(index);
		} else return null;
	}
	
	/**
	 * @return path separator
	 */
	public String getSeparator() {
		return separator;
	}
	
	/**
	 * @return current path string
	 */
	public String getCurrent() {
		return current;
	}
	
}
