package com.ibm.util.merge.template.directive.enrich.provider;
/*
 * SPECIAL TESTING REQUIREMENTS
 * HTTP localhost/idmuTest should refer to /src/test/resources/http
 * "idmu-rest1": [
        {
            "credentials": {
                "username": "user",
                "password": "pass",
                "host": "localhost",
                "port": 80,
                "url": "idmuTest/{path}"
            }
        }
    ]

 */
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.RestProvider;
import com.ibm.util.merge.template.directive.enrich.source.RestSource;

public class RestProviderTest {
	Config config;
	TemplateCache cache;
	Merger context;
	Template template;
	RestSource source;
	RestProvider provider;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		context = new Merger(cache, config, "system.sample.");
		template = new Template("test","provider","rest","Rest Provider Test");
		template.setWrapper("{", "}");
		source = new RestSource();
		source.setEnv("VCAP:idmu-rest1");
		source.setName("idmu-test");
		source.setGetCommand("idmuTest/{path}");
	}

	@Test
	public void testRestProvider() throws MergeException {
		provider = (RestProvider) source.getProvider();
		assertEquals(AbstractProvider.PROVIDER_REST, provider.getType());
	}
	
	@Test
	public void testGetObjectString() throws MergeException {
		template = template.getMergable(context);
		template.addReplace("path", "simple.txt");
		source.setParseAs(ParseData.PARSE_NONE);
		provider = (RestProvider) source.getProvider();
		DataElement element = provider.get(template);
		assertTrue(element.isPrimitive());
		assertEquals("Test Text File", element.getAsPrimitive());
	}
	
	@Test
	public void testGetObjectParsedCsv() throws MergeException {
		template = template.getMergable(context);
		template.addReplace("path", "simple.csv");
		source.setParseAs(ParseData.PARSE_CSV);
		provider = (RestProvider) source.getProvider();
		DataElement element = provider.get(template);
		assertTrue(element.isList());
		assertEquals(2, element.getAsList().size());
		DataObject row = element.getAsList().get(0).getAsObject(); 
		assertEquals("r1c1", row.getAsObject().get("col1").getAsPrimitive());
		assertEquals("r1c2", row.getAsObject().get("col2").getAsPrimitive());
		assertEquals("r1c3", row.getAsObject().get("col3").getAsPrimitive());
		row = element.getAsList().get(1).getAsObject();
		assertTrue(row.isObject());
		assertEquals("r2c1", row.getAsObject().get("col1").getAsPrimitive());
		assertEquals("r2c2", row.getAsObject().get("col2").getAsPrimitive());
		assertEquals("r2c3", row.getAsObject().get("col3").getAsPrimitive());
	}

	@Test
	public void testGetObjectParsedHtml() throws MergeException {
		template = template.getMergable(context);
		template.addReplace("path", "simple.html");
		source.setParseAs(ParseData.PARSE_HTML);
		provider = (RestProvider) source.getProvider();
		DataElement element = provider.get(template);
		assertTrue(element.isObject());
		DataObject object = element.getAsObject();
		assertTrue(object.containsKey("attribute"));
		assertEquals("value", object.get("attribute"));
	}

	@Test
	public void testGetObjectParsedJson() throws MergeException {
		template = template.getMergable(context);
		template.addReplace("path", "simple.json");
		source.setParseAs(ParseData.PARSE_JSON);
		provider = (RestProvider) source.getProvider();
		DataElement element = provider.get(template);
		assertTrue(element.isObject());
		DataObject object = element.getAsObject();
		assertTrue(object.containsKey("attribute"));
		assertEquals("value", object.get("attribute").getAsPrimitive());
	}

	@Test
	public void testGetObjectParsedXML() throws MergeException {
		template = template.getMergable(context);
		template.addReplace("path", "simple.xml");
		source.setParseAs(ParseData.PARSE_XML_DATA);
		provider = (RestProvider) source.getProvider();
		DataElement element = provider.get(template);
		assertTrue(element.isList());
		assertTrue(element.getAsList().get(0).isObject());
		DataObject object = element.getAsList().get(0).getAsObject();
		assertTrue(object.containsKey("attribute"));
		assertEquals("value", object.get("attribute"));
	}
	
}
