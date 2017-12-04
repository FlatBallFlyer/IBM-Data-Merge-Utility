package com.ibm.util.merge.exception;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.*;

import com.ibm.util.merge.Cache;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.AbstractDirective;
import com.ibm.util.merge.template.directive.Replace;

public class MergeExceptionTest {
	private DataProxyJson proxy = new DataProxyJson(true);
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDefault403() throws MergeException {
		Cache cache = new Cache();
		Merger merger = new Merger(cache, "system.sample.");
		try {
			throw new Merge403("A Message");
		} catch (MergeException except) {
			assertEquals("A Message", except.getMessage());
			assertEquals("Error - Forbidden", except.getErrorMessage(merger));
		}
	}

	@Test
	public void testDefault404() throws MergeException {
		Cache cache = new Cache();
		Merger merger = new Merger(cache, "system.sample.");
		try {
			throw new Merge404("A Message");
		} catch (MergeException except) {
			assertEquals("A Message", except.getMessage());
			assertEquals("Error - Not Found", except.getErrorMessage(merger));
		}
	}

	@Test
	public void testDefault500() throws MergeException {
		Cache cache = new Cache();
		Merger merger = new Merger(cache, "system.sample.");
		try {
			throw new Merge500("A Message");
		} catch (MergeException except) {
			assertEquals("A Message", except.getMessage());
			assertEquals("Error - Merge Error", except.getErrorMessage(merger));
		}
	}
	
	@Test
	public void testCustom() throws Throwable {
		Cache cache = new Cache();
		cache.postTemplate(this.getFailTemplate("unit", "fail", ""));
		
		cache.putTemplate(this.getExceptionTemplate("system", Merge500.TEMPLATE, ""));
		
		Merger merger = new Merger(cache, "unit.fail.");
		try {
			merger.merge();
		} catch (MergeException e) {
			assertEquals("Source Data Missing for  in  at ", e.getMessage());
			assertEquals("Merge500", e.getType());
			String detailed = e.getErrorMessage(merger);
//			assertEquals("", detailed); // Commented out - cheat point to get output
			
			DataElement error = proxy.fromString(detailed, DataElement.class);
			assertTrue(error.isObject());
			assertTrue(error.getAsObject().containsKey("exception"));
			assertTrue(error.getAsObject().containsKey("stackTrace"));
			assertTrue(error.getAsObject().containsKey("dataManager"));
			assertTrue(error.getAsObject().get("exception").isObject());
			DataObject data = error.getAsObject().get("exception").getAsObject();

			assertTrue(data.containsKey("template"));
			DataObject template = data.get("template").getAsObject();
			assertEquals("unit", template.get("id").getAsObject().get("group").getAsPrimitive());
			assertEquals("fail", template.get("id").getAsObject().get("name").getAsPrimitive());

			assertTrue(data.containsKey("directive"));
			DataObject directive = data.get("directive").getAsObject();
			assertEquals(new DataPrimitive(AbstractDirective.TYPE_REPLACE).getAsPrimitive(), 
						directive.get("type").getAsPrimitive());
			
			return;
		}
		fail("Exception Not Thrown!");
	}
	
	@Test
	public void testCustomVaryby() throws Throwable {
		Cache cache = new Cache();
		cache.postTemplate(this.getFailTemplate("unit", "fail", ""));
		
		cache.postTemplate(this.getExceptionTemplate("system", Merge500.TEMPLATE, "com.ibm.util.merge.template.directive.Replace.execute"));
		cache.putTemplate(this.getFailTemplate("system", Merge500.TEMPLATE, ""));
		
		Merger merger = new Merger(cache, "unit.fail.");
		try {
			merger.merge();
		} catch (MergeException e) {
			assertEquals("Source Data Missing for  in  at ", e.getMessage());
			assertEquals("Merge500", e.getType());
			String detailed = e.getErrorMessage(merger);
			
			DataElement error = proxy.fromString(detailed, DataElement.class);
			assertTrue(error.isObject());
			assertTrue(error.getAsObject().containsKey("exception"));
			assertTrue(error.getAsObject().containsKey("stackTrace"));
			assertTrue(error.getAsObject().containsKey("dataManager"));
			assertTrue(error.getAsObject().get("exception").isObject());
			DataObject data = error.getAsObject().get("exception").getAsObject();

			assertTrue(data.containsKey("template"));
			DataObject template = data.get("template").getAsObject();
			assertEquals("unit", template.get("id").getAsObject().get("group").getAsPrimitive());
			assertEquals("fail", template.get("id").getAsObject().get("name").getAsPrimitive());

			assertTrue(data.containsKey("directive"));
			DataObject directive = data.get("directive").getAsObject();
			assertEquals(new DataPrimitive(AbstractDirective.TYPE_REPLACE).getAsPrimitive(), 
						directive.get("type").getAsPrimitive());
			
			return;
		}
		fail("Exception Not Thrown!");
	}
	
	@Test
	public void testCustomFail() throws Throwable {
		Cache cache = new Cache();
		cache.postTemplate(this.getFailTemplate("unit", "fail", ""));
		
		cache.putTemplate(this.getFailTemplate("system", Merge500.TEMPLATE, ""));
		
		Merger merger = new Merger(cache, "unit.fail.");
		try {
			merger.merge();
		} catch (MergeException e) {
			assertEquals("Source Data Missing for  in  at ", e.getMessage());
			assertEquals("Merge500", e.getType());
			assertEquals("Error - Merge Error", e.getErrorMessage(merger));
			return;
		}
		fail("Exception Not Thrown!");
	}
	
	@Test
	public void testCustomDebugTemplates() throws Throwable {
		Cache cache = new Cache();
		cache.postTemplate(this.getFailTemplate("unit", "fail", ""));
		
		File testDebug = new File("WebContent/templates/system.debug.json");
		cache.putGroup(new String(Files.readAllBytes(testDebug.toPath()), "ISO-8859-1"));
		
		Merger merger = new Merger(cache, "unit.fail.");
		try {
			merger.merge();
		} catch (MergeException e) {
			assertEquals("Source Data Missing for  in  at ", e.getMessage());
			assertEquals("Merge500", e.getType());
			String detailed = e.getErrorMessage(merger);
			
			DataElement error = proxy.fromString(detailed, DataElement.class);
			assertTrue(error.isObject());
			assertTrue(error.getAsObject().containsKey("exception"));
			assertTrue(error.getAsObject().containsKey("stackTrace"));
			assertTrue(error.getAsObject().containsKey("dataManager"));
			return;
		}
		fail("Exception Not Thrown!");
	}
	
	private Template getFailTemplate(String group, String template, String varyBy) throws MergeException {
		Template theTemplate = new Template(group, template, varyBy);
		Replace replace = new Replace();
		replace.setIfSourceMissing(Replace.MISSING_THROW);
		theTemplate.addDirective(replace);
		return theTemplate;
	}

	private Template getExceptionTemplate(String group, String template, String varyBy) throws MergeException {
		Template theTemplate = new Template(group, template, varyBy, 
				"{ \"exception\": 	<idmuException>,"
				+ "\"dataManager\": <idmuExceptionData>,"
				+ "\"stackTrace\": \"<idmuStackTrace encode=\"json\">\""
				+ "}", "<", ">");
		Replace replace;
		replace = new Replace();
		replace.setIfPrimitive(Replace.PRIMITIVE_REPLACE);
		replace.setDataSource("idmuException");
		theTemplate.addDirective(replace);
		
		replace = new Replace();
		replace.setIfPrimitive(Replace.PRIMITIVE_REPLACE);
		replace.setDataSource("idmuExceptionData");
		theTemplate.addDirective(replace);

		replace = new Replace();
		replace.setIfPrimitive(Replace.PRIMITIVE_REPLACE);
		replace.setDataSource("idmuStackTrace");
		replace.setProcessAfter(true);
		theTemplate.addDirective(replace);

		return theTemplate;
	}
}
