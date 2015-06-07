package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

public class FunctionalCsvTest {
	Template root = null;
	String output = "";
	HashMap<String, String[]> parameterMap;
	String templateDir = "integration/templates/";
	String templateOut = "integration/output/";
	
	@Before
	public void test() throws MergeException, IOException {
		// Setup Factory
		TemplateFactory.reset();
		TemplateFactory.setDbPersistance(false);
		TemplateFactory.setTemplateFolder(templateDir);
		TemplateFactory.loadAll();
		
		// Initialize requestMap (usually from request.getParameterMap())
		parameterMap = new HashMap<String,String[]>();
	}

	@Test
	public void testDefaultTemplate() throws MergeException, IOException {
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(output, String.join("\n", Files.readAllLines(Paths.get(templateOut + "merge1.html"))));
	}
}
