/**
 * 
 */
package com.ibm.util.merge.template.directive.enrich.source;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.RestProvider;
import com.ibm.util.merge.template.directive.enrich.source.RestSource;

/**
 * @author mikestorey
 *
 */
public class RestSourceTest {
	RestSource source;

	@Before
	public void setup() {
		source = new RestSource();
	}

	@Test
	public void testRestSource() {
		assertTrue(source instanceof RestSource);
		assertEquals(AbstractSource.SOURCE_REST, source.getType());
	}
	
	@Test
	public void testGetNewProvider() throws MergeException {
		AbstractProvider provider = source.getProvider();
		assertTrue(provider instanceof RestProvider);
	}
}
