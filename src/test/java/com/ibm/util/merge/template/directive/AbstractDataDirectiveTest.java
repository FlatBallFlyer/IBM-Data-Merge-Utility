package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;

public class AbstractDataDirectiveTest {
	private class AbstractDataTest extends AbstractDataDirective {
		private static final int OPTION_THROW = 1;
		private static final int OPTION_SKIP = 2;
		private DataProxyJson proxy = new DataProxyJson();
		
		@Override
		public HashMap<Integer, String> missingOptions() {
			HashMap<Integer, String> options = new HashMap<Integer, String>();
			options.put(OPTION_THROW, "throw");
			options.put(OPTION_SKIP, "skip");
			return options;
		}
		
		@Override
		public HashMap<Integer, String> primitiveOptions() {
			HashMap<Integer, String> options = new HashMap<Integer, String>();
			options.put(OPTION_THROW, "throw");
			options.put(OPTION_SKIP, "skip");
			return options;
		}

		@Override
		public HashMap<Integer, String> objectOptions() {
			HashMap<Integer, String> options = new HashMap<Integer, String>();
			options.put(OPTION_THROW, "throw");
			options.put(OPTION_SKIP, "skip");
			return options;
		}
		
		@Override
		public HashMap<Integer, String> listOptions() {
			HashMap<Integer, String> options = new HashMap<Integer, String>();
			options.put(OPTION_THROW, "throw");
			options.put(OPTION_SKIP, "skip");
			return options;
		}

		@Override
		public AbstractDirective getMergable() {
			AbstractDataTest mergable = new AbstractDataTest();
			this.makeMergable(mergable);
			return mergable;			
		}
		
		@Override
		public void execute(Merger context) throws MergeException {}

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

	@Test
	public void testSetGetIfSourceMissing() {
		for (int option : test.missingOptions().keySet()) {
			test.setIfSourceMissing(option);
			assertEquals(option, test.getIfSourceMissing());
		}
		test.setIfSourceMissing(AbstractDataTest.OPTION_SKIP);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfSourceMissing());
		test.setIfSourceMissing(0);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfSourceMissing());
	}

	@Test
	public void testSetGetIfPrimitive() {
		for (int option : test.objectOptions().keySet()) {
			test.setIfPrimitive(option);
			assertEquals(option, test.getIfPrimitive());
		}
		test.setIfPrimitive(AbstractDataTest.OPTION_SKIP);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfPrimitive());
		test.setIfPrimitive(0);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfPrimitive());
	}

	@Test
	public void testSetGetIfObject() {
		for (int option : test.objectOptions().keySet()) {
			test.setIfObject(option);
			assertEquals(option, test.getIfObject());
		}
		test.setIfObject(AbstractDataTest.OPTION_SKIP);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfObject());
		test.setIfObject(0);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfObject());
	}

	@Test
	public void testSetGetIfList() {
		for (int option : test.listOptions().keySet()) {
			test.setIfList(option);
			assertEquals(option, test.getIfList());
		}
		test.setIfList(AbstractDataTest.OPTION_SKIP);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfList());
		test.setIfList(0);
		assertEquals(AbstractDataTest.OPTION_SKIP, test.getIfList());
	}
}
