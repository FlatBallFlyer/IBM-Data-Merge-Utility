package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.Archive;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.provider.ProviderInterface;
import com.ibm.util.merge.template.directive.enrich.provider.StubProvider;

public class MergerTest {
	private TemplateCache cache;
	
	@Before
	public void setUp() throws Exception {
		Config.initialize();
		cache = new TemplateCache();
	}

	@Test
	public void testMergerCacheConfigTemplate() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		assertSame(cache, merger.getCahce());
		assertTrue(merger.getBaseTemplate().isMergable());
		assertTrue(merger.getTemplateStack().isEmpty());
		assertTrue(merger.getMergeData() instanceof DataManager);
		assertEquals(4, merger.getCahce().getSize());
	}

	@Test
	public void testMergerCacheConfigTemplateParametersRequest() throws MergeException {
		HashMap<String,String[]> parameters = new HashMap<String, String[]>();
		String request = "Some Input Data";
		String[] option = {"yes"};
		String[] names = {"fred","betty","barney"};
		parameters.put("names", names);
		parameters.put("option", option);
		Merger merger = new Merger(cache, "system.sample.", parameters, request);
		assertEquals(request, merger.getMergeData().get(Merger.IDMU_PAYLOAD, "-").getAsPrimitive());
		assertEquals("yes", merger.getMergeData().get(Merger.IDMU_PARAMETERS + "-option-[0]", "-").getAsPrimitive());
		assertEquals("betty", merger.getMergeData().get(Merger.IDMU_PARAMETERS + "-names-[1]", "-").getAsPrimitive());
	}

	@Test
	public void testMerge() throws MergeException {
		Merger merger = new Merger(cache, "system.error403.");
		Template template = merger.merge();
		assertEquals("Error - Forbidden", template.getContent());
	}

	@Test
	public void testGetMergable() throws MergeException {
		Merger merger = new Merger(cache, "system.error403.");
		Template template = merger.getMergable("system.error403.",  "", new HashMap<String,String>());
		assertTrue(template.isMergable());
		assertEquals("Error - Forbidden", template.getContent());
		assertEquals(0, merger.getTemplateStack().size());
	}

	@Test
	public void testPushPopGetStack() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		DataObject object = new DataObject();
		assertEquals(0, merger.getStackSize());
		merger.pushTemplate("Foo", object);
		assertEquals(1, merger.getStackSize());
		merger.pushTemplate("Bar", object);
		assertEquals(2, merger.getStackSize());
		merger.popTemplate();
		assertEquals(1, merger.getStackSize());
		merger.popTemplate();
		assertEquals(0, merger.getStackSize());
	}

	@Test
	public void testGetArchiveSimple() throws MergeException {
		Config.get().setTempFolder("test");
		Merger merger = new Merger(cache, "system.sample.");
		Archive archive = merger.getArchive();
		assertEquals("test", archive.getFilePath());
		assertEquals(Archive.ARCHIVE_TAR, archive.getArchiveType());
		assertFalse(archive.getFileName().isEmpty());
	}
	
	@Test
	public void testGetArchiveType() throws MergeException {
		for (String type : Archive.ARCHIVE_TYPES()) {
			Config.get().setTempFolder("test");
			Merger merger = new Merger(cache, "system.sample.");
			DataObject parameters = new DataObject();
			DataList values = new DataList();
			values.add(new DataPrimitive("Overide"));
			parameters.put(Merger.IDMU_ARCHIVE_NAME, values);
			values = new DataList();
			values.add(new DataPrimitive(type));
			parameters.put(Merger.IDMU_ARCHIVE_TYPE, values);
			merger.getMergeData().put(Merger.IDMU_PARAMETERS, "-", parameters);
			Archive archive = merger.getArchive();
			assertEquals("test", archive.getFilePath());
			assertEquals(type, archive.getArchiveType());
			assertFalse(archive.getFileName().isEmpty());
		}
	}
	
	@Test
	public void testGetArchiveName() throws MergeException {
		Config.get().setTempFolder("test");
		Merger merger = new Merger(cache, "system.sample.");
		DataObject parameters = new DataObject();
		DataList values = new DataList();
		values.add(new DataPrimitive("Overide"));
		parameters.put(Merger.IDMU_ARCHIVE_NAME, values);
		merger.getMergeData().put(Merger.IDMU_PARAMETERS, "-", parameters);
		Archive archive = merger.getArchive();
		assertEquals("test", archive.getFilePath());
		assertEquals(Archive.ARCHIVE_TAR, archive.getArchiveType());
		assertEquals("Overide", archive.getFileName());
	}

	@Test
	public void testGetMergeData() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		assertTrue(merger.getMergeData() instanceof DataManager);
	}
	
	@Test
	public void testGetCache() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		assertSame(cache, merger.getCahce());
	}
	
	@Test
	public void testGetBaseTemplate() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		assertEquals("system", merger.getBaseTemplate().getId().group);
		assertEquals("sample", merger.getBaseTemplate().getId().name);
		assertEquals("", merger.getBaseTemplate().getId().variant);
	}
	
	@Test
	public void testGetProviders() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		assertEquals(0, merger.getProviders().size());
		ProviderInterface provider = merger.getProvider("com.ibm.util.merge.template.directive.enrich.provider.StubProvider", "TheSource", "TheDB");
		assertTrue(provider instanceof StubProvider);
		provider = merger.getProvider("Foo", "TheSource", "TheDb");
		assertTrue(provider instanceof StubProvider);
	}
	
	@Test
	public void testGetStack() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		DataObject object = new DataObject();
		assertEquals(0, merger.getTemplateStack().size());
		merger.pushTemplate("Foo", object);
		assertEquals(1, merger.getTemplateStack().size());
	}
	@Test
	public void testClearData() throws MergeException {
		Merger merger = new Merger(cache, "system.sample.");
		merger.getMergeData().put("Test", "-", "Test Value");
		assertTrue(merger.getMergeData().contians("Test", "-"));
		merger.clearMergeData();
		assertFalse(merger.getMergeData().contians("Test", "-"));
	}
}
