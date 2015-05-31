package com.ibm.util.merge.directive.provider;

import static org.junit.Assert.*;

import org.junit.*;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.Directive;

public class ProviderCsvTest extends ProviderHttpTest {
	
	@Before
	public void setUp() {
		template = new Template();
		directive = new DirectiveStub();
		provider = new ProviderCsv();

		ProviderCsv myProvider = (ProviderCsv) provider;
		directive.setProvider(myProvider);
		template.addDirective(directive);
		template.addReplace("TEST_TAG", "One,Two,Three,Four\nr1c1,r1c2,r1c3,r1c4\nr2c1,r2c2,r2c3,r2c4\nr3c1,r3c2,r3c3,r3c4\n");
		myProvider.reset();
		myProvider.setTag("TEST_TAG");
		myProvider.setStaticData("A,B,C\n1,2,3\n4,5,6\n");
		myProvider.setUrl("http://sometestdata.csv");
	}
	
	@After
	public void tearDown() {
		template = null;
		directive = null;
		provider = null;
	}
	
	@Test
	public void testGetDataFromUrl() {
		// TODO - How to Unit Test HTTP code?
		// provider.getData();
	}

	@Test
	public void testGetDataFromTag() throws MergeException {
		super.testGetDataFromTag();
		assertEquals(1,provider.size());
		assertEquals(3,provider.getTable(0).size());
		assertEquals("r2c2", provider.getTable(0).getValue(1, "Two"));
	}

	@Test
	public void testGetDataFromStatic() throws MergeException {
		super.testGetDataFromStatic();
		assertEquals(1,provider.size());
		assertEquals(2,provider.getTable(0).size());
		assertEquals("3", provider.getTable(0).getValue(0,  "C"));
	}

	@Test
	public void testProviderCsvCloneDirective() throws CloneNotSupportedException {
		ProviderCsv newProvider = (ProviderCsv) provider.clone();
		assertNotEquals(provider, newProvider);
		assertNull(newProvider.getDirective());
	}

	@Test
	public void testGetQueryString() {
		assertEquals("http://sometestdata.csv", provider.getQueryString());
	}

	private class DirectiveStub extends Directive {
		public DirectiveStub() {}
		public void executeDirective() throws MergeException {}
	}
}
