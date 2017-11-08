package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class AbstractDataDirectiveTest {
	private class AbstractDataTest extends AbstractDataDirective {
		
		@Override
		public AbstractDirective getMergable() {
			AbstractDataTest mergable = new AbstractDataTest();
			this.makeMergable(mergable);
			return mergable;			
		}
		
		@Override
		public void execute(Merger context) throws MergeException {}

		@Override
		public void setIfSourceMissing(int value) {
		}

		@Override
		public void setIfPrimitive(int value) {
		}

		@Override
		public void setIfObject(int value) {
		}

		@Override
		public void setIfList(int value) {
		}

		@Override
		public void cleanup(Config config, Template template)
				throws MergeException {
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
	public void testGetMergable() {
		AbstractDataTest mergable = (AbstractDataTest) test.getMergable();
		assertNotSame(mergable, test);
		assertEquals(test.getDataSource(), 		mergable.getDataSource());
		assertEquals(test.getDataDelimeter(), 	mergable.getDataDelimeter());
		assertEquals(test.getIfList(), 			mergable.getIfList());
		assertEquals(test.getIfObject(), 		mergable.getIfObject());
		assertEquals(test.getIfPrimitive(), 	mergable.getIfPrimitive());
		assertEquals(test.getIfSourceMissing(), mergable.getIfSourceMissing());
	}

	@Test
	public void testSetGetDataSource() {
		test.setDataSource("Foo");
		assertEquals("Foo", test.getDataSource());
	}

	@Test
	public void testSetGetDataDelimeter() {
		test.setDataDelimeter("Foo");
		assertEquals("Foo", test.getDataDelimeter());
	}
}
