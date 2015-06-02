package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.*;

import com.ibm.util.merge.directive.*;

public class TemplateTest {
	private Template template;
	private Template cachedTemplate;
	private String templateString = "Testing {Foo} Template <tkBookmark name=\"BKM1\"/> and {empty} <tkBookmark name=\"BKM2\"/> save to {folder}";
	private String mergeOutput = "Testing Bar Template  and   save to /tmp/output/";
	
	@Before
	public void setUp() throws Exception {
		cachedTemplate = new Template();
		cachedTemplate.setIdtemplate(1);
		cachedTemplate.setCollection("root");
		cachedTemplate.setName("test");
		cachedTemplate.setColumnValue("type");
		cachedTemplate.setDescription("Testing Root Template");
		cachedTemplate.setContent(templateString);
		
		ReplaceValue directive = new ReplaceValue();
		directive.setFrom("TestTag1");
		directive.setTo("Test Value1");
		directive.setDescription("Test Replace1");
		cachedTemplate.addDirective(directive);
		directive = new ReplaceValue();
		directive.setFrom("TestTag2");
		directive.setTo("Test Value2");
		directive.setDescription("Test Replace2");
		cachedTemplate.addDirective(directive);
		
		HashMap<String,String> testMap = new HashMap<String,String>();
		testMap.put("{TestSeed}", "SeedValue");
		template = cachedTemplate.clone(testMap);
		template.addReplace("folder", "/tmp/output/");
		template.addReplace("Foo", "Bar");
		template.addReplace("empty", "");
	}

	@Test
	public void testWrap() {
		String test = Template.wrap("Test");
		assertEquals("{Test}", test);
	}

	@Test
	public void testAsJson() {
		assertEquals(cachedTemplate.asJson(), template.asJson());
	}

	@Test
	public void testClone() throws MergeException {
		assertNotEquals(cachedTemplate, template);
		assertEquals(cachedTemplate.getCollection(), 		template.getCollection());
		assertEquals(cachedTemplate.getName(), 				template.getName());
		assertEquals(cachedTemplate.getColumnValue(), 		template.getColumnValue());
		assertEquals(cachedTemplate.getDescription(), 		template.getDescription());
		assertEquals(cachedTemplate.getContent(), 			template.getContent());
		assertNotEquals(cachedTemplate.getBookmarks(), 		template.getBookmarks());
		assertEquals(cachedTemplate.getBookmarks().size(), 	template.getBookmarks().size());
		assertTrue(template.hasReplaceKey("{TestSeed}"));
		assertEquals("SeedValue", template.getReplaceValue("{TestSeed}"));
		template.setContent("Foo");
		assertNotEquals(cachedTemplate.getContent(), template.getContent());
	}

	@Test
	public void testSaveOutputAs() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testPackageOutput() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testInsertText() {
		String testInsert1 = "Testing {Foo} Template Test<tkBookmark name=\"BKM1\"/> and {empty} <tkBookmark name=\"BKM2\"/> save to {folder}";
		String testInsert2 = "Testing {Foo} Template Test<tkBookmark name=\"BKM1\"/> and {empty} Test<tkBookmark name=\"BKM2\"/> save to {folder}";
		assertEquals(templateString, template.getContent());
		assertEquals(2, template.getBookmarks().size());
		assertEquals(23, template.getBookmarks().get(0).getStart());
		assertEquals(61, template.getBookmarks().get(1).getStart());
		
		template.insertText("Test", template.getBookmarks().get(0));
		assertEquals(testInsert1, template.getContent());
		assertEquals(27, template.getBookmarks().get(0).getStart());
		assertEquals(65, template.getBookmarks().get(1).getStart());
		
		template.insertText("Test", template.getBookmarks().get(1));
		assertEquals(testInsert2, template.getContent());
		assertEquals(27, template.getBookmarks().get(0).getStart());
		assertEquals(69, template.getBookmarks().get(1).getStart());
	}

	@Test
	public void testReplaceThis() throws MergeException {
		String test = "Testing Fixed <tkBookmark name=\"BKM1\"/> and {empty} <tkBookmark name=\"BKM2\"/> save to {folder}";
		template.replaceThis("{Foo} Template", "Fixed");
		assertEquals(test, template.getContent());
	}

	@Test
	public void testReplaceAllThis() {
		String test = "Testing {Foo} Template Gone and {empty} Gone save to {folder}";
		template.replaceAllThis(Template.BOOKMARK_PATTERN , "Gone");
		assertEquals(test, template.getContent());
	}

	@Test
	public void testHasReplaceKey() {
		assertTrue(template.hasReplaceKey("{Foo}"));
		assertFalse(template.hasReplaceKey("FooBar"));
	}

	@Test
	public void testHasReplaceValue() {
		assertTrue(template.hasReplaceValue("{Foo}"));
		assertFalse(template.hasReplaceValue("FooBar"));
	}

	@Test
	public void testAddEmptyReplace() {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("Foo");
		tags.add("Bar");
		template.addEmptyReplace(tags);
		assertTrue(template.hasReplaceKey("{Foo}"));
		assertTrue(template.hasReplaceKey("{Bar}"));
		assertFalse(template.hasReplaceValue("Foo"));
		assertFalse(template.hasReplaceValue("Bar"));
	}

	@Test
	public void testReplaceProcess() {
		String test = "Test {Foo} - to {folder}";
		assertEquals("Test Bar - to /tmp/output/", template.replaceProcess(test));
	}

	@Test
	public void testGetReplaceValue() throws MergeException {
		assertEquals("Bar", template.getReplaceValue("{Foo}"));
	}

	@Test
	public void testSetContent() {
		assertEquals(templateString, template.getContent());
		assertEquals(2, template.getBookmarks().size());
	}

	@Test
	public void testMerge() throws MergeException {
		assertEquals(mergeOutput, template.merge());
	}
	
}
