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
package com.ibm.util.merge.directive.provider;

import java.util.ArrayList;

public class DataTable {
	private ArrayList<String> columnNames = new ArrayList<String>();
	private ArrayList<ArrayList<String>> theData = new ArrayList<ArrayList<String>>();
	
	public DataTable() {
	}
	
	public int size() {
		return theData.size();
	}
	
	public int cols() {
		return columnNames.size();
	}

	public void addCol(String name) {
		this.columnNames.add(name);
	}
	
	public int getCol(String name) {
		return columnNames.indexOf(name);
	}
	
	public String getCol(int index) {
		if (index < 0 || index >= columnNames.size()) {return "";}
		return columnNames.get(index);
	}

	public String getValue(int row, String name) {
		return getValue(row,this.getCol(name)); 
	}

	public String getValue(int row, int col) {
		if (row < 0 || row >= theData.size()) {return "";}
		if (col < 0 || col >= theData.get(row).size()) {return "";}
		return theData.get(row).get(col);
	}
	
	public ArrayList<String> getNewRow() {
		ArrayList<String> newRow = new ArrayList<String>();
		this.theData.add(newRow);
		return newRow;
	}

	public void addRow(ArrayList<String> newRow) {
		this.theData.add(newRow);
	}

	public void setCols(ArrayList<String> strings) {
		this.columnNames = strings;
	}

}
