package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.AbstractDirective;
import com.ibm.util.merge.template.directive.SaveFile;

public class SaveFileTest {
	private TemplateCache cache;
	private Merger merger;
	private Template template;
	private SaveFile directive;
	
	@Before
	public void setUp() throws Exception {
		Config.initialize();
		cache = new TemplateCache();
		merger = new Merger(cache, "system.sample.");
		directive = new SaveFile();
	}

	@Test
	public void testGetMergable() throws MergeException {
		directive.cachePrepare(new Template());
		SaveFile mergable = (SaveFile) directive.getMergable();
		assertNotSame(mergable, directive);
		assertEquals(directive.getFilename(), mergable.getFilename());
		assertEquals(directive.getClearAfter(), mergable.getClearAfter());
		assertEquals(mergable.getType(), AbstractDirective.TYPE_SAVE_FILE);
		assertEquals(null, mergable.getTemplate());
	}
	
	@Test 
	public void testExecuteNoOp() throws MergeException {
		Config.load("{\"tempFolder\":\"src/test/resources/temp\"}");
		TemplateCache cache = new TemplateCache();
		Template template = new Template("test","noop","", "Simple Test");
		SaveFile directive = new SaveFile();
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "test.noop.");
		template = merger.merge();
		assertEquals("Simple Test", template.getMergedOutput().getValue());
		File file = new File(merger.getMergeData().get(Merger.IDMU_ARCHIVE_OUTPUT, "-").getAsPrimitive());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}
	
	@Test
	public void testExecute() throws MergeException {
		Config.load("{\"tempFolder\":\"src/test/resources/temp\"}");
		template = new Template("save", "test", "", "Some Simple Content");
		directive.setFilename("testMember.txt");
		directive.setClearAfter(true);
		template.addDirective(directive);
		template.cachePrepare();
		template = template.getMergable(merger);
		template.getMergedOutput();
		assertTrue(template.getContent().isEmpty());
		File file = new File(merger.getMergeData().get(Merger.IDMU_ARCHIVE_OUTPUT, "-").getAsPrimitive());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}

	@Test
	public void testExecuteNoClear() throws MergeException {
		Config.load("{\"tempFolder\":\"src/test/resources/temp\"}");
		template = new Template("save", "test", "", "Some Simple Content");
		directive.setFilename("testMember.txt");
		directive.setClearAfter(false);
		template.addDirective(directive);
		template.cachePrepare();
		template = template.getMergable(merger);
		template.getMergedOutput();
		assertEquals("Some Simple Content", template.getContent());
		File file = new File(merger.getMergeData().get(Merger.IDMU_ARCHIVE_OUTPUT,"-").getAsPrimitive());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}

	@Test
	public void testGetSetFilename() throws MergeException {
		directive.setFilename("Foo");
		assertEquals("Foo", directive.getFilename());
	}

	@Test
	public void testGetSetClearAfter() {
		directive.setClearAfter(true);
		assertTrue(directive.getClearAfter());
		directive.setClearAfter(false);
		assertFalse(directive.getClearAfter());
	}

}
