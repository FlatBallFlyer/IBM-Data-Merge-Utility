package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.StubSource;

public class AbstractProviderTest {
	private class AbstractTest extends AbstractProvider {
		public AbstractTest(AbstractSource source) throws MergeException {
			super(source);
			this.setType(AbstractProvider.PROVIDER_STUB);
		}

		@Override
		public DataElement get(Template template) throws MergeException {return null;}

		@Override
		public void put(Template template) throws MergeException {}

		@Override
		public void post(Template template) throws MergeException {}

		@Override
		public void delete(Template template) throws MergeException {}
	}
	
	AbstractTest test;
	StubSource source;
	DataProxyJson proxy = new DataProxyJson();
	
	@Before
	public void setUp() throws Exception {
		source = new StubSource();
		test = new AbstractTest(source);
	}

	@Test
	public void testAbstractProvider() {
		assertEquals(AbstractProvider.PROVIDER_STUB, test.getType());
		assertSame(source, test.getSource());
	}

	@Test
	public void testSetGetType() {
		for (int type : AbstractProvider.PROVIDER_TYPES().keySet()) {
			test.setType(type);
			assertEquals(type, test.getType());
		}
		test.setType(AbstractProvider.PROVIDER_STUB);
		assertEquals(AbstractProvider.PROVIDER_STUB, test.getType());
		test.setType(0);
		assertEquals(AbstractProvider.PROVIDER_STUB, test.getType());
	}

	@Test
	public void testGetSource() {
		assertTrue(test.getSource() instanceof StubSource);

	}

	@Test
	public void testGetEnvironmentString() throws MergeException {
		// ENVIRONMENT VARIABLE test-env WITH CONTENT {"foo":"bar"} required 
		source.setEnv("test-env");
		String env = test.getEnvironmentString();
		assertEquals("{\"foo\":\"bar\"}", env);
	}

	@Test
	public void testGetVcapEntry() throws MergeException {
		// ENVIRONMENT VARIABLE VCAP_SERVICES WITH CONTENT {"test-env": [{"foo":"bar"}]} required 
		source.setEnv("VCAP:test-env");
		String vcap = test.getEnvironmentString();
		assertEquals("{\"foo\":\"bar\"}", vcap);
	}

	@Test
	public void testGetJsonMember() {
		JsonArray array = new JsonArray();
		try {
			test.getJsonMemeber(array, "foo");
		} catch (MergeException e) {
			return; // expected
		}
		fail("GetJsonMember failed to throw exceptoin on Array");
	}
	
	@Test
	public void testGetJsonMember2() {
		JsonPrimitive primitive = new JsonPrimitive("bar");
		try {
			test.getJsonMemeber(primitive, "foo");
		} catch (MergeException e) {
			return; // expected
		}
		fail("GetJsonMember failed to throw exceptoin on Primitive");
	}

	@Test
	public void testGetJsonMember3() {
		JsonPrimitive primitive = new JsonPrimitive("bar");
		JsonObject object = new JsonObject();
		object.add("foo", primitive);
		try {
			test.getJsonMemeber(primitive, "bad");
		} catch (MergeException e) {
			return; // expected
		}
		fail("GetJsonMember failed to throw exceptoin on Memeber Missing");
	}

	@Test
	public void testGetJsonMember4() {
		JsonObject object = new JsonObject();
		object.add("foo", new JsonObject());
		try {
			test.getJsonMemeber(object, "foo");
		} catch (MergeException e) {
			return; // expected
		}
		fail("GetJsonMember failed to throw exceptoin on Memeber Not Primitive");
	}

	@Test
	public void testGetJsonMemberSuccess() throws MergeException {
		JsonPrimitive primitive = new JsonPrimitive("bar");
		JsonObject object = new JsonObject();
		object.add("foo", primitive);
		String result = test.getJsonMemeber(object, "foo");
		assertEquals("bar", result);
	}
}
