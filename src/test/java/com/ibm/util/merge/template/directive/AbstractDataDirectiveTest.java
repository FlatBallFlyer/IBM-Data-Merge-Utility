package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
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
		public void cachePrepare(Template template)
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
		assertEquals(test.getRawDataSource(), 	mergable.getRawDataSource());
		assertEquals(test.getDataDelimeter(), 	mergable.getDataDelimeter());
		assertEquals(test.getSourceHasTags(), 	mergable.getSourceHasTags());
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
	public void testSetGetDataSourceTag() throws MergeException {
		TemplateCache cache = new TemplateCache();
		Template template = new Template("test","","","Content","<",">");
		test.setDataSource("some-<key>-bar");
		test.setSourceHasTags(true);
		template.addDirective(test);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test..");
		template = context.getBaseTemplate();
		template.addReplace("key","foo");
		test = (AbstractDataTest) template.getDirectives().get(0);
		assertEquals("some-foo-bar", test.getDataSource());
	}

	@Test
	public void testSetGetDataDelimeter() {
		test.setDataDelimeter("Foo");
		assertEquals("Foo", test.getDataDelimeter());
	}
}
