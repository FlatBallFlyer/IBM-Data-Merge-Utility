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
package com.ibm.util.merge.data.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;


/**
 * Provides CSV Parsing features
 * 
 * @author Mike Storey
 *
 */
public class DataProxyCsv implements ParserProxyInterface {

	/**
	 * Instantiate a proxy 
	 */
	public DataProxyCsv() {
	}

	@Override
	public Integer getKey() {
		return 1;
	}

    /**
     * Parse the CSV data into a DataList object with DataObject members
     * 
     * @param data Data to Parse
     * @return the parsed List
     * @throws MergeException on processing errors
     */
	@Override
    public DataElement fromString(String data) throws MergeException {
		DataList list = new DataList();
		String sourceCsv = data;
		CSVParser parser;
		try {
			parser = new CSVParser(new StringReader(sourceCsv), CSVFormat.EXCEL.withHeader());
			Map<String, Integer> headers = parser.getHeaderMap();
		    for (CSVRecord record : parser) {
		    	DataObject row = new DataObject();
		    	for (String key : headers.keySet()) {
		    		String value = record.get(headers.get(key));
		    		row.put(key, new DataPrimitive(value));
		    	}
		    	list.add(row);
		    }
		    parser.close();
		    return list;
		} catch (IOException e) {
			throw new Merge500("CSV Parser Error:" + e.getMessage());
		}
    }

}
