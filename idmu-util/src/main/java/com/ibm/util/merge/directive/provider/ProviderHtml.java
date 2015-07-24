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

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ProviderHtml extends ProviderHttp {
	public ProviderHtml() {
		super();
		setType(Providers.TYPE_HTML);
	}
	
	public ProviderHtml asNew() {
		ProviderHtml to = new ProviderHtml();
		to.copyFieldsFrom((ProviderHttp)this);
		return to;
	}

	/**
	 * Retrieve the data (superclass HTTP Provider) and parse the CSV data
	 * @param cf
	 */
	@Override
	public void getData(MergeContext rtc) throws MergeException {
		// Get the data
		super.getData(rtc);
		
		// Parse the Data
		Document doc = Jsoup.parse(getFetchedData());

		// Find <table> elements
		Elements tables = doc.select("table");

		// for each (find) {
		for (Element table : tables) {
			DataTable newTable = addNewTable();
			
			// Find and Process Table Header elements
			Elements headers = table.select("th");
			for (Element header : headers) {
				newTable.addCol(header.ownText());
			}
			
			// Find and Process Table Row elements
			Elements rows = table.select("tr");
			ArrayList<String> newRow = newTable.addNewRow();;
			for (Element row : rows ) {
				if (newRow.size() > 0) {newRow = newTable.addNewRow();}
				Elements cols = row.select("td");
				for (Element col : cols ) {
					newRow.add(col.ownText());
				}
			}
		}
	}

}
