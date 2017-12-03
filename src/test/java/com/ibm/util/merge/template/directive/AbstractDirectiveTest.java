package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Cache;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.AbstractDirective;

public class AbstractDirectiveTest {
	private class AbstractTest extends AbstractDirective {

		@Override
		public void execute(Merger context) throws MergeException {
			return;
		}

		@Override
		public AbstractDirective getMergable(Merger context) {
			AbstractTest mergable = new AbstractTest();
			this.makeMergable(mergable, context);
			return mergable;
		}

		@Override
		public void cachePrepare(Template template, Config config)
				throws MergeException {
			super.cachePrepare(template, config);
		}

	}
	AbstractTest test;

	@Before
	public void setUp() throws Exception {
		 test = new AbstractTest();
	}

	@Test
	public void testAbstractDirective() {
		assertTrue(test instanceof AbstractDirective);
	}

	@Test
	public void testMakeMergable() throws MergeException {
		Cache cache = new Cache();
		Merger context = new Merger(cache, "system.sample.");
		AbstractDirective mergable = test.getMergable(context);
		assertNotSame(mergable, test);
		assertEquals(test.getType(), mergable.getType());
		assertEquals(test.getName(), mergable.getName());
		
	}

	@Test
	public void testGetSetTemplate() throws MergeException, MergeException {
		Template template = new Template();
		test.setTemplate(template);
		assertSame(template, test.getTemplate());
	}

	@Test
	public void testGetSetName() {
		test.setName("Foo");
		assertEquals("Foo", test.getName());
	}

	@Test
	public void testGetSetType() {
		for (int type : AbstractDirective.DIRECTIVE_TYPES().keySet()) {
			test.setType(type);
			assertEquals(type, test.getType());
		}
		test.setType(AbstractDirective.TYPE_INSERT);
		assertEquals(AbstractDirective.TYPE_INSERT, test.getType());
		test.setType(0);
		assertEquals(AbstractDirective.TYPE_INSERT, test.getType());
	}
}
