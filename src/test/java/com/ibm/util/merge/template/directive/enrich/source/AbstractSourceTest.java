package com.ibm.util.merge.template.directive.enrich.source;
/*
 * SPECIAL TESTING REQUIREMENTS
 * Environment Variables abstract-source
{"credentials":"foo"} 
 * and VCAP_SERVICES  
{"abstract-source":[{"credentials":"foo"}]}
 *  
 */

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.StubProvider;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;

public class AbstractSourceTest {
	private class AbstractTest extends AbstractSource {
		public AbstractTest() {
			super();
		}

		@Override
		public AbstractProvider getProvider() throws MergeException {
			return new StubProvider(this);
		}

		@Override
		public void setOptions(DataObject directive) {
			// TODO Auto-generated method stub
			
		}
	}
	
	DataProxyJson proxy = new DataProxyJson();
	AbstractTest source;
	
	@Before
	public void setup() {
		source = new AbstractTest();
	}
	
	@Test
	public void testGetSetType() {
		for (Integer type : AbstractSource.SOURCE_TYPES().keySet()) {
			source.setType(type);
			assertEquals(type.intValue(), source.getType());
		}
	}
	
	@Test
	public void testSetTypeFail() {
		source.setType(AbstractSource.SOURCE_REST);
		assertEquals(AbstractSource.SOURCE_REST, source.getType());
		source.setType(0);
		assertEquals(AbstractSource.SOURCE_REST, source.getType());
	}
	
	@Test
	public void testGetSetProvider() throws MergeException {
		AbstractProvider provider = source.getProvider();
		assertTrue(provider instanceof StubProvider);
		AbstractProvider again = source.getProvider();
		assertNotSame(provider, again);
	}
	
	@Test
	public void testGetSetParseAs() {
		for (Integer parse : ParseData.PARSE_OPTIONS().keySet()) {
			source.setParseAs(parse);
			assertEquals(parse.intValue(), source.getParseAs());
		}
		source.setParseAs(ParseData.PARSE_NONE);
		assertEquals(ParseData.PARSE_NONE, source.getParseAs());
		source.setParseAs(0);
		assertEquals(ParseData.PARSE_NONE, source.getParseAs());
	}

	@Test
	public void testGetSetName() {
		source.setName("Bar");
		assertEquals("Bar", source.getName());
	}

	@Test
	public void testGetSetEnv() {
		source.setEnv("Foo");
		assertEquals("Foo", source.getEnv());
	}

	@Test
	public void testGetSetGetCommand() {
		source.setGetCommand("Foo");
		assertEquals("Foo", source.getGetCommand());
	}

	@Test
	public void testGetSetPutCommand() {
		source.setPutCommand("Foo");
		assertEquals("Foo", source.getPutCommand());
	}

	@Test
	public void testGetSetPostCommand() {
		source.setPostCommand("Foo");
		assertEquals("Foo", source.getPostCommand());
	}

	@Test
	public void testGetSetDeleteCommand() {
		source.setDeleteCommand("Foo");
		assertEquals("Foo", source.getDeleteCommand());
	}

}
