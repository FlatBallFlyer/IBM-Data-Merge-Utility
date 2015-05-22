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
	
	public String getName(int col) {
		return columnNames.get(col);
	}

	public String getValue(int row, String name) {
		return getValue(row,this.getCol(name)); 
	}

	public String getValue(int row, int col) {
		if (row < 0 | row > theData.size()) {return "";}
		if (col < 0 | col > theData.get(row).size()) {return "";}
		return theData.get(row).get(col);
	}
	
	public ArrayList<String> getNewRow() {
		ArrayList<String> newRow = new ArrayList<String>();
		this.theData.add(newRow);
		return newRow;
	}

}
