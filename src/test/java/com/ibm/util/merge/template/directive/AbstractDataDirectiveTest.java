package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Cache;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class AbstractDataDirectiveTest {
	private class AbstractDataTest extends AbstractDataDirective {
		
		@Override
		public AbstractDirective getMergable(Merger context) throws MergeException {
			AbstractDataTest mergable = new AbstractDataTest();
			this.makeMergable(mergable, context);
			return mergable;			
		}
		
		@Override
		public void execute(Merger context) throws MergeException {}

		@Override
		public void setIfSourceMissing(int value) {
			super.setIfSourceMissing(value);
		}

		@Override
		public void setIfPrimitive(int value) {
			super.setIfPrimitive(value);
		}

		@Override
		public void setIfObject(int value) {
			super.setIfObject(value);
		}

		@Override
		public void setIfList(int value) {
			super.setIfList(value);
		}

		@Override
		public void cachePrepare(Template template, Config config) throws MergeException {
			super.cachePrepare(template, config);
		}

	}
	
	private AbstractDataTest test;
	
	@Before
	public void setUp() throws Exception {
		test = new AbstractDataTest();
	}

	@Test
	public void testAbstractDataDirective() {
		assertTrue(test instanceof AbstractDataTest);
	}

	@Test
	public void testGetMergable() throws MergeException {
		Merger context = new Merger(new Cache(), "system.sample.");
		test.cachePrepare(new Template(), new Config());
		AbstractDataTest mergable = (AbstractDataTest) test.getMergable(context);
		assertNotSame(mergable, test);
		assertEquals(test.getDataDelimeter(), 	mergable.getDataDelimeter());
		assertEquals(test.getIfList(), 			mergable.getIfList());
		assertEquals(test.getIfObject(), 		mergable.getIfObject());
		assertEquals(test.getIfPrimitive(), 	mergable.getIfPrimitive());
		assertEquals(test.getIfSourceMissing(), mergable.getIfSourceMissing());
	}

	@Test
	public void testSetGetDataSource() throws MergeException {
		test.setDataSource("Foo");
		assertEquals("Foo", test.getDataSource());
	}

	@Test
	public void testSetGetDataDelimeter() {
		test.setDataDelimeter("Foo");
		assertEquals("Foo", test.getDataDelimeter());
	}
}
