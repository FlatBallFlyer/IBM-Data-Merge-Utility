package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.AbstractDirective;
import com.ibm.util.merge.template.directive.SaveFile;

public class SaveFileTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetMergable() throws MergeException {
		Merger context = new Merger(new Cache(), "system.sample.");
		SaveFile directive = new SaveFile();
		directive.cachePrepare(new Template(), new Config());
		SaveFile mergable = (SaveFile) directive.getMergable(context);
		assertNotSame(mergable, directive);
		assertEquals(directive.getFilename(), mergable.getFilename());
		assertEquals(directive.getClearAfter(), mergable.getClearAfter());
		assertEquals(mergable.getType(), AbstractDirective.TYPE_SAVE_FILE);
		assertEquals(null, mergable.getTemplate());
	}
	
	@Test 
	public void testExecuteNoOp() throws MergeException {
		Config config = new Config("{\"tempFolder\":\"src/test/resources/temp\"}");
		Cache cache = new Cache(config);
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
		Config config = new Config("{\"tempFolder\":\"src/test/resources/temp\"}");
		Cache cache = new Cache(config);
		SaveFile directive = new SaveFile();
		directive.setFilename("testMember.txt");
		directive.setClearAfter(true);
		Template template = new Template("save", "test", "", "Some Simple Content");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "save.test.");
		template = merger.merge();
		assertTrue(template.getContent().isEmpty());
		File file = new File(merger.getMergeData().get(Merger.IDMU_ARCHIVE_OUTPUT, "-").getAsPrimitive());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}

	@Test
	public void testExecuteNoClear() throws MergeException {
		Config config = new Config("{\"tempFolder\":\"src/test/resources/temp\"}");
		Cache cache = new Cache(config);
		Template template = new Template("save", "test", "", "Some Simple Content");
		SaveFile directive = new SaveFile();
		directive.setFilename("testMember.txt");
		directive.setClearAfter(false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "save.test.");
		template = merger.merge();
		assertEquals("Some Simple Content", template.getContent());
		File file = new File(merger.getMergeData().get(Merger.IDMU_ARCHIVE_OUTPUT,"-").getAsPrimitive());
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}

	@Test
	public void testGetSetFilename() throws MergeException {
		SaveFile directive = new SaveFile();
		directive.setFilename("Foo");
		assertEquals("Foo", directive.getFilename());
	}

	@Test
	public void testGetSetClearAfter() {
		SaveFile directive = new SaveFile();
		directive.setClearAfter(true);
		assertTrue(directive.getClearAfter());
		directive.setClearAfter(false);
		assertFalse(directive.getClearAfter());
	}

}
