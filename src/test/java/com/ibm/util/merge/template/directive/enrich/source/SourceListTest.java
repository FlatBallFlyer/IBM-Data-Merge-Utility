package com.ibm.util.merge.template.directive.enrich.source;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.template.directive.enrich.source.CloudantSource;
import com.ibm.util.merge.template.directive.enrich.source.JdbcSource;
import com.ibm.util.merge.template.directive.enrich.source.MongoSource;
import com.ibm.util.merge.template.directive.enrich.source.RestSource;
import com.ibm.util.merge.template.directive.enrich.source.SourceList;
import com.ibm.util.merge.template.directive.enrich.source.StubSource;

public class SourceListTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddGetSize() {
		SourceList list = new SourceList();
		assertEquals(0, list.size());
		list.add("cloudant", new CloudantSource());
		assertEquals(1, list.size());
		assertTrue(list.get("cloudant") instanceof CloudantSource);
		
		list.add("jdbc", new JdbcSource());
		assertEquals(2, list.size());
		assertTrue(list.get("jdbc") instanceof JdbcSource);
		
		list.add("mongo", new MongoSource());
		assertEquals(3, list.size());
		assertTrue(list.get("mongo") instanceof MongoSource);
		
		list.add("rest", new RestSource());
		assertEquals(4, list.size());
		assertTrue(list.get("rest") instanceof RestSource);
		
		list.add("stub", new StubSource());
		assertEquals(5, list.size());
		assertTrue(list.get("stub") instanceof StubSource);
	}

	@Test
	public void testRemove() {
		SourceList list = new SourceList();
		list.add("cloud", new CloudantSource());
		list.add("jdbc", new JdbcSource());
		assertEquals(2, list.size());
		list.remove("jdbc");
		assertEquals(1, list.size());
	}

	@Test
	public void testIterator() {
		SourceList list = new SourceList();
		list.add("1", new JdbcSource());
		list.add("2", new JdbcSource());
		list.add("3", new JdbcSource());
		list.add("4", new JdbcSource());
		list.add("4", new JdbcSource());
		list.add("4", new JdbcSource());
		assertEquals(4, list.size());
		for (String source : list.getSources().keySet()) {
			assertTrue(list.get(source) instanceof JdbcSource);
		}
	}

}
