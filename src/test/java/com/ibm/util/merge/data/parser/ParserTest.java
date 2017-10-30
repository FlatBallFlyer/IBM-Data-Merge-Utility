package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.ParseData;

public class ParserTest {
	Parser parser;
	
	@Before
	public void setUp() throws Exception {
		 parser = new Parser();
	}

	@Test
	public void testParserCsv() throws MergeException {
		DataElement parsed = parser.parse(ParseData.PARSE_CSV, 
				"col1,col2,col3\n" +
				"r1c1,r1c2,r1c3\n" +
				"r2c1,r2c2,r2c3\n" 
				);
		assertTrue(parsed.isList());
	}

	@Test
	public void testParserHtml() throws MergeException {
		DataElement parsed = parser.parse(ParseData.PARSE_HTML, 
				"<html><head></head><body></body></html>");
		assertTrue(parsed.isList());
	}

	@Test
	public void testParserJson() throws MergeException {
		DataElement parsed = parser.parse(ParseData.PARSE_JSON, 
				"{\"attribute\":\"value\"}"
				);
		assertTrue(parsed.isObject());
	}

	@Test
	public void testParserXmlMarkup() throws MergeException {
		DataElement parsed = parser.parse(ParseData.PARSE_XML_MARKUP, 
				"<attribute>value</attribute>");
		assertTrue(parsed.isObject());
	}

	@Test
	public void testParserXmlData() throws MergeException {
		DataElement parsed = parser.parse(ParseData.PARSE_XML_DATA, 
				"<attribute>value</attribute>");
		assertTrue(parsed.isObject());
	}
}
