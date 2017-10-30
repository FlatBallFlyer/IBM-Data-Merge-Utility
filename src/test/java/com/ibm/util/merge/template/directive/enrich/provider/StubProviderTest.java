package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.provider.StubProvider;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.StubSource;

public class StubProviderTest {
	private AbstractSource source;
	private StubProvider provider;
	private Template template;
	
	@Before
	public void setup() throws MergeException {
		source = new StubSource();
		provider = new StubProvider(source);
		template = new Template("test","stub","provider");
	}
	
	@Test
	public void testGet() throws MergeException {
		DataElement result = provider.get(template);
		assertTrue(result.isList());
		assertEquals(1, result.getAsList().size());
	}

}
