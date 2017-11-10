package com.ibm.util.merge.template;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.exception.MergeException;

public class TemplateListTest {
	
	@Test
	public void testAdd() throws MergeException {
		TemplateList list = new TemplateList();
		assertEquals(0, list.size());
		list.add(new Template());
		assertEquals(1, list.size());
	}

	@Test
	public void testGet() throws MergeException {
		TemplateList list = new TemplateList();
		assertEquals(0, list.size());
		Template newTemplate = new Template();
		list.add(newTemplate);
		assertEquals(1, list.size());
		Template again = list.get(0);
		assertSame(newTemplate, again);
	}

}
