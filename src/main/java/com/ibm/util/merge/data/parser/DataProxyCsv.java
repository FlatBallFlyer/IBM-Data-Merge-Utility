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


public class DataProxyCsv {

	public DataProxyCsv() {
	}

    public DataElement fromCsv(String data) throws MergeException {
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
