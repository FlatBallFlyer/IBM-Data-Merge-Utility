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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class ProviderCsv extends ProviderHttp {
	public ProviderCsv() {
		super();
		setType(Providers.TYPE_CSV);
	}
	
	public ProviderCsv asNew() {
		ProviderCsv to = new ProviderCsv();
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
		
		DataTable newTable = new DataTable();
		CSVParser parser;
		try {
			parser = new CSVParser(new StringReader(getFetchedData()), CSVFormat.EXCEL.withHeader());
			for (String colName : parser.getHeaderMap().keySet() ) {
				newTable.addCol(colName);
			}
		    for (CSVRecord record : parser) {
				ArrayList<String> row = newTable.addNewRow();
		    	for (String field : record) {row.add(field);}
		    }
		    parser.close();
		} catch (IOException e) {
			throw new MergeException(e, "CSV Parser Stringreader IO Exception", getFetchedData());
		}
		if (newTable.size() > 0) {
			getTables().add(newTable);
		}
	}
}
