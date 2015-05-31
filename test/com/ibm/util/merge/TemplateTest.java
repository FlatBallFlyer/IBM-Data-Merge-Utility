package com.ibm.util.merge;

import static org.junit.Assert.*;
import org.junit.*;
import com.ibm.util.merge.directive.*;

public class TemplateTest {
	private Template template;
	private String templateString = "";
	
	@Before
	public void setUp() throws Exception {
		template = new Template();
		template.setIdtemplate(1);
		template.setCollection("root");
		template.setName("test");
		template.setColumnValue("type");
		template.setDescription("Testing Root Template");
		template.setOutputFile("{folder}/filename.txt");
		template.setContent("Test Template <tkBookmark name=\"foo\"/> with two bookmarks <tkBookmark name=\"bar\"/>");
		template.addReplace("folder", "/tmp/output/");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWrap() {
	}

	@Test
	public void testAsJson() {
		String json = template.asJson();
		assertEquals(templateString, json);
	}

	@Test
	public void testClone() {
		// TODO "Not yet implemented"
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
		for (Bookmark bk : template.getBookmarks()) {
			template.insertText("InsertedText", bkm);
		}
		assertEquals(55, template.getContent());
	}

	@Test
	public void testReplaceThis() throws MergeException {
		template.replaceThis("SomeValue", "SomeNewValue");
		assertEquals(55, template.getContent());
	}

	@Test
	public void testReplaceAllThis() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testHasReplaceKey() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testHasReplaceValue() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testAddEmptyReplace() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testReplaceProcess() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testGetReplaceValue() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testSetContent() {
		// TODO "Not yet implemented"
	}

	@Test
	public void testMerge() {
		// TODO "Not yet implemented"
	}
	
}
