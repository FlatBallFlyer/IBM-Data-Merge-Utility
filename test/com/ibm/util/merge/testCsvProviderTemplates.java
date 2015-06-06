package com.ibm.util.merge;

import static org.junit.Assert.*;
import com.meterware.httpunit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

public class testCsvProviderTemplates {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TemplateFactory.reset();
		TemplateFactory.setDbPersistance(false);
		TemplateFactory.setTemplateFolder("test/templatesFunctional/");
		TemplateFactory.loadAll();
	}

	@Test
	public void testContactsCsv() throws MergeException, IOException {
		 WebConversation wc = new WebConversation();
		    WebRequest     req = new GetMethodWebRequest( "http://www.meterware.com/testpage.html" );
		    WebResponse   resp = wc.getResponse( req );		    
		    
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("collection", "provider");
		map.put("name", "ContactsCsv");
		map.put("condition", "");
		Template template = TemplateFactory.getTemplate("provider.ContactsCsv.", "", map);
		String output = template.merge();
		template.packageOutput();
		assertEquals(output, String.join("\n", Files.readAllLines(Paths.get("functionalTest/providerContactsCsv.csv"))));
	}

	@Test
	public void testCustomersCsv() throws MergeException, IOException {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("collection", "provider");
		map.put("name", "CustomerCsv");
		map.put("condition", "");
		Template template = TemplateFactory.getTemplate("provider.CustomersCsv.", "", map);
		String output = template.merge();
		template.packageOutput();
		assertEquals(output, String.join("\n", Files.readAllLines(Paths.get("functionalTest/providerCustomersCsv.csv"))));
	}

	@Test
	public void testCorporateCsv() throws MergeException, IOException {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("collection", "provider");
		map.put("name", "CorporateCsv");
		map.put("condition", "");
		Template template = TemplateFactory.getTemplate("provider.CorporateCsv.", "", map);
		String output = template.merge();
		template.packageOutput();
		assertEquals(output, String.join("\n", Files.readAllLines(Paths.get("functionalTest/providerCorporateCsv.csv"))));
	}

}
