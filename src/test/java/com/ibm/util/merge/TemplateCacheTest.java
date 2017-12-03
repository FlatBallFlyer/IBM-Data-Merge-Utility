package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Stat;
import com.ibm.util.merge.template.Stats;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.TemplateList;

public class TemplateCacheTest {
	private Cache cache;
	private Merger context;
	private HashMap<String,String> replace;
	private DataProxyJson gson;
	
	@Before
	public void setUp() throws Exception {
		Config config = new Config("{\"prettyJson\": false }");
		cache = new Cache(config);
		context = new Merger(cache, "system.sample.");

		replace = new HashMap<String,String>();
		replace .put("foo", "bar");
		replace .put("one", "two");
		
		gson = new DataProxyJson(false);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTemplateCache() {
		assertEquals(4, cache.getSize());
	}

//	@Test
//	public void testTemplateCacheFile() throws MergeException {
//		Config.load("{\"loadFolder\":\"src/test/resources\"}");
//		cache = new Cache();
//		assertEquals(8, cache.getSize());
//	}

	@Test
	public void testGetMergableStringStringHashMapOfStringString() throws MergeException {
		Template template; 
		template = cache.getMergable(context, "system.error403.", "foo", replace);
		assertEquals(template.getId().group, "system");
		assertEquals(template.getId().name, "error403");
		assertTrue(template.getId().variant.isEmpty());
		assertEquals(Template.STATE_MERGABLE, template.getState());
		assertEquals(2, template.getReplaceStack().size());

		template = cache.getMergable(context, "Foo", "system.error404.", replace);
		assertEquals(template.getId().group, "system");
		assertEquals(template.getId().name, "error404");
		assertTrue(template.getId().variant.isEmpty());
		assertEquals(Template.STATE_MERGABLE, template.getState());
		assertEquals(2, template.getReplaceStack().size());
	}

	@Test
	public void testGetMergableStringStringHashMapOfStringStringError1() throws MergeException {
		try {
			@SuppressWarnings("unused")
			Template template = cache.getMergable(context, "Foo", "Bar", replace);
		} catch (MergeException e) {
			return;
		}
		fail("bad template name failed to throw exception");
	}

	@Test
	public void testGetMergableStringHashMapOfStringString() throws MergeException {
		Template template = cache.getMergable(context, "system.error403.", replace);
		assertEquals(template.getId().group, "system");
		assertEquals(template.getId().name, "error403");
		assertTrue(template.getId().variant.isEmpty());
		assertEquals(Template.STATE_MERGABLE, template.getState());
		assertEquals(2, template.getReplaceStack().size());
	}

	public void testGetMergableStringHashMapOfStringStringError() throws MergeException {
		try {
			@SuppressWarnings("unused")
			Template template = cache.getMergable(context, "Foo", replace);
		} catch (MergeException e) {
			return;
		}
		fail("bad template name failed to throw exception");
	}

	@Test
	public void testPostGetPutDeleteTemplate() throws MergeException {
		Template newTemplate = new Template("new","test","");
		assertEquals(4, cache.getSize());
		cache.postTemplate(newTemplate);
		assertEquals(5, cache.getSize());
		cache.getTemplate("new.test.");
		assertEquals(5, cache.getSize());
		newTemplate = new Template("new","test","");
		cache.putTemplate(newTemplate);
		assertEquals(5, cache.getSize());
		cache.deleteTemplate("new.test");
		assertEquals(4, cache.getSize());
	}

	@Test
	public void testPostTemplateExisting() throws MergeException {
		Template newTemplate = new Template("new","test","");
		try {
			cache.postTemplate(newTemplate);
			cache.postTemplate(newTemplate);
		} catch (MergeException e) {
			return;
		}
		fail("Duplicate Post did not throw exception");
	}

	@Test
	public void testPostTemplateError() throws MergeException {
		try {
			cache.postTemplate("");
		} catch (MergeException e) {
			return;
		}
		fail("bad template failed to throw error");
	}

	@Test
	public void testPutTemplateError() throws MergeException {
		try {
			cache.putTemplate("");
		} catch (MergeException e) {
			return;
		}
		fail("bad template failed to throw error");
	}

	@Test
	public void testPostGroupError() throws MergeException {
		try {
			cache.postGroup("");
		} catch (MergeException e) {
			return;
		}
		fail("bad template failed to throw error");
	}

	@Test
	public void testPutGroupError() throws MergeException {
		try {
			cache.putGroup("");
		} catch (MergeException e) {
			return;
		}
		fail("bad template failed to throw error");
	}

	@Test
	public void testGetTemplate() throws MergeException {
		String template = cache.getTemplate("system.error404.");
		TemplateList list = gson.fromString(template, TemplateList.class);
		assertEquals(1,list.size());
		assertEquals("system", list.get(0).getId().group);
		assertEquals("error404",  list.get(0).getId().name);
		assertTrue(list.get(0).getId().variant.isEmpty());
	}

	@Test
	public void testGetTemplatesAll() throws MergeException {
		String templates = cache.getTemplate("");
		TemplateList list = gson.fromString(templates, TemplateList.class);
		assertEquals(4, list.size());
	}

	@Test
	public void testGetTemplatesTwo() throws MergeException {
		String templates = cache.getTemplate("system");
		TemplateList list = gson.fromString(templates, TemplateList.class);
		assertEquals(4, list.size());
	}

	@Test
	public void testGetTemplatesThree() {
		String templates = cache.getTemplate("system.sample");
		TemplateList list = gson.fromString(templates, TemplateList.class);
		assertEquals(1, list.size());
	}

	@Test
	public void testGetTemplatesFour() {
		String templates = cache.getTemplate("system.sample.");
		TemplateList list = gson.fromString(templates, TemplateList.class);
		assertEquals(1, list.size());
	}

	@Test
	public void testPutTemplateString() throws MergeException {
		cache.postTemplate(new Template("test","putpost","", "Very Simple Test"));
		Template template = cache.getMergable(context, "test.putpost.");
		assertEquals("Very Simple Test", template.getContent().toString());
		
		template = new Template("test","putpost","","Something New");
		assertEquals("Something New", template.getContent().toString());
		cache.putTemplate(gson.toString(template));
		template = cache.getMergable(context, "test.putpost.", replace);
		assertEquals("Something New", template.getContent().toString());
	}

	@Test
	public void testPutTemplateStringNotFound() throws MergeException {
		Template newTemplate = new Template("test","new","");
		try {
			cache.putTemplate(newTemplate);
		} catch (MergeException e) {
			return;
		}
		fail("update not-found failed to throw error");
	}

	@Test
	public void testDeleteTemplateString() throws MergeException {
		assertEquals(4, cache.getSize());
		cache.deleteTemplate("system.sample.");
		assertEquals(3, cache.getSize());
	}

	@Test
	public void testDeleteTemplateStringNotFOund() throws MergeException {
		try {
			cache.deleteTemplate("FOO");
		} catch (MergeException e) {
			return;
		}
		fail("delete not-found failed to throw error");
	}

	@Test
	public void testGetGroupListGroup() {
		String list = cache.getGroup("system");
		TemplateList templates = gson.fromString(list, TemplateList.class);
		assertEquals(4, templates.size());
	}

	@Test
	public void testGetGroupListAll() throws MergeException {
		cache.postTemplate(new Template("test","one","","content"));
		String list = cache.getGroup("");
		assertEquals("[\"system\",\"test\"]", list);
	}

	@Test
	public void testPostGroup() throws MergeException {
		cache.postTemplate(new Template("test","one","","content"));
		cache.postTemplate(new Template("test","two","","content"));
		cache.postTemplate(new Template("test","foo","","content"));
		cache.postTemplate(new Template("test","bar","","content"));
		assertEquals(8, cache.getSize());
		String test = cache.getGroup("test");
		cache.deleteGroup("test");
		assertEquals(4, cache.getSize());
		cache.postGroup(test);
		assertEquals(8, cache.getSize());
	}

	@Test
	public void testGetGroup() {
		String list = cache.getGroup("system");
		TemplateList templates = gson.fromString(list, TemplateList.class);
		assertEquals(4, templates.size());
	}

	@Test
	public void testPutGroup() throws MergeException {
		assertEquals(4, cache.getSize());
		cache.postTemplate(new Template("test","one","","content"));
		cache.postTemplate(new Template("test","two","","content"));
		cache.postTemplate(new Template("test","foo","","content"));
		cache.postTemplate(new Template("test","bar","","content"));
		assertEquals(8, cache.getSize());

		String list = cache.getGroup("test");
		TemplateList templates = gson.fromString(list, TemplateList.class);
		templates.remove(1);
		cache.putGroup(gson.toString(templates));
		assertEquals(7, cache.getSize());
	}

	@Test
	public void testGetStats() throws MergeException {
		Long response = new Long(5);
		cache.postTemplate(new Template("test","stats",""));
		cache.postStats("test.stats.", response);
		cache.postStats("test.stats.", response);
		cache.postStats("test.stats.", response);
		cache.postStats("test.stats.", response);
		Stats stats = cache.getStats();
		assertEquals(5,stats.size());
		for (Stat stat : stats) {
			if (stat.name.equals("test.stats.")) {
				assertEquals(4,stat.hits);
			}
		}
	}

	@Test
	public void testDeleteGroup() throws MergeException {
		cache.postTemplate(new Template("test","one","","content"));
		cache.postTemplate(new Template("test","two","","content"));
		cache.postTemplate(new Template("test","foo","","content"));
		cache.postTemplate(new Template("test","bar","","content"));
		assertEquals(8, cache.getSize());
		cache.deleteGroup("test");
		assertEquals(4, cache.getSize());
	}

	@Test
	public void testDeleteGroupErrorSystem() throws MergeException {
		try {
			cache.deleteGroup("system");
		} catch (MergeException e) {
			return;
		}
		fail("SYSTEM GROUP DELETED!!");
	}

	@Test
	public void testDeleteGroupNotFound() throws MergeException {
		try {
			cache.deleteGroup("FOO");
		} catch (MergeException e) {
			return;
		}
		fail("Group not found failed");
	}
}
