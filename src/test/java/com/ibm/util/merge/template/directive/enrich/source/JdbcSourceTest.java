package com.ibm.util.merge.template.directive.enrich.source;
/*
 *   "idmu-jdbc1": [
        {
            "credentials": {
                "db_type": "sql",
                "name": "testsql",
                "uri_cli": "testcli",
                "ca_certificate_base64": "test64",
                "deployment_id": "testid",
                "uri": "testuri"
            }]
 */

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.JdbcProvider;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.JdbcSource;

public class JdbcSourceTest {
	private JdbcSource source;

	@Before
	public void setup() {
		source = new JdbcSource();
	}

	@Test
	public void testJdbcSource() {
		assertEquals(AbstractSource.SOURCE_JDBC, source.getType());
	}

	@Test
	public void testGetNewProvider() throws MergeException {
		AbstractProvider provider = source.getProvider();
		assertTrue(provider instanceof JdbcProvider);
	}
	
	@Test
	public void testGetSetDatabase() throws MergeException {
		JdbcSource source = new JdbcSource();
		source.setDatabase("Foo");
		assertEquals("Foo", source.getDatabase());
	}
	
	@Test
	public void testGetSetJndiDataSourceName() throws MergeException {
		JdbcSource source = new JdbcSource();
		source.setJndiDataSourceName("Foo");
		assertEquals("Foo", source.getJndiDataSourceName());
	}
	
}
