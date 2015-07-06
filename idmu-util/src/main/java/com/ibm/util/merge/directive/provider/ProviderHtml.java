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

import com.ibm.util.merge.ConnectionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.ibm.util.merge.MergeException;

public class ProviderHtml extends ProviderHttp {
	public ProviderHtml() {
		super();
		setType(Provider.TYPE_HTML);
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderHtml clone() throws CloneNotSupportedException {
		ProviderHtml provider = (ProviderHtml) super.clone();
		return provider;
	}

	/**
	 * Retrieve the data (superclass HTTP Provider) and parse the CSV data
	 * @param cf
	 */
	public void getData(ConnectionFactory cf) throws MergeException {
		
		super.getData(cf);
		
		// Parse the Data
		Document doc = Jsoup.parse(getFetchedData());

		// Find <table> elements
		Elements tables = doc.select("table");

		// for each (find) {
		for (Element table : tables) {
			DataTable newTable = getNewTable();
			
			// Find and Process Table Header elements
			Elements headers = table.select("th");
			for (Element header : headers) {
				newTable.addCol(header.ownText());
			}
			
			// Find and Process Table Row elements
			Elements rows = table.select("tr");
			ArrayList<String> newRow = newTable.getNewRow();;
			for (Element row : rows ) {
				if (newRow.size() > 0) {newRow = newTable.getNewRow();}
				Elements cols = row.select("td");
				for (Element col : cols ) {
					newRow.add(col.ownText());
				}
			}
		}
	}

}
