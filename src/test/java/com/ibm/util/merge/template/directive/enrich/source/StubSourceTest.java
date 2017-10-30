package com.ibm.util.merge.template.directive.enrich.source;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.StubProvider;
import com.ibm.util.merge.template.directive.enrich.source.StubSource;

public class StubSourceTest {

	@Test
	public void testGetProvider() throws MergeException {
		StubSource source = new StubSource();
		assertTrue(source.getProvider() instanceof StubProvider);
	}

}
