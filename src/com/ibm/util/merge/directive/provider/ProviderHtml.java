package com.ibm.util.merge.directive.provider;

import java.sql.ResultSet;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.Directive;

public class ProviderHtml extends ProviderHttp {
	public ProviderHtml(Directive newOwner, ResultSet dbRow) throws MergeException {
		super(newOwner, dbRow);
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderHtml clone(Directive newOwner) throws CloneNotSupportedException {
		return (ProviderHtml) super.clone(newOwner);
	}

	/**
	 * Retrieve the data (superclass HTTP Provider) and parse the CSV data
	 */
	public void getData() throws MergeException {
		super.getData();
		
		// Parse the Data
		// HtmlParser parser = new HtmlParser(parms);
		// parser.parse(this.textData);
		
		// Find <table> elements
		// for each (find) {
		// 	
		// for (int r = 1; r<rows(); r++) {
		//	ArrayList<String> row = new ArrayList<String>();
		//	theData.add(row);
		// 	for (int c = 1; c<cols(); c++) {
		//		row.add(data(r,c));
		//	}

		// Parse HTML
		// Find <table> element(s)
		// Add data to this.theData and this.columnNames
	}

}
