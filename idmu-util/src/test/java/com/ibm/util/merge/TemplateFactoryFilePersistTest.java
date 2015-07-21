/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.util.merge;

import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.AbstractPersistence;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import com.ibm.util.merge.template.Template;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

// TODO Finalize Testing Templates and Collections
public class TemplateFactoryFilePersistTest {
	private String templatesFolder = "src/test/resources/templates";
	private File templatesDir = new File(templatesFolder);
	private JsonProxy proxy = new PrettyJsonProxy();
	private AbstractPersistence filePersist = new FilesystemPersistence(templatesDir, new PrettyJsonProxy());
    private TemplateFactory testFactory = new TemplateFactory(filePersist, proxy);
    private int count = 56;
    
    @Before
    public void setUp() throws Exception {
    	testFactory.reset();
    }

    @Test
    public void testReset() throws MergeException {
        assertEquals(count, testFactory.size());
    }

    @Test
    public void testGetTemplateFullNameFoundInCache() throws MergeException {
    	Template template = testFactory.getTemplate("root.default.", "root.foo", new HashMap<String,String>());
        assertNotNull(template);
        assertEquals("root.default.", template.getFullName());
    }

    @Test
    public void testGetTemplateShortNameFoundInCache() throws MergeException {
    	Template template = testFactory.getTemplate("root.default.fake", "root.default.", new HashMap<String,String>());
        assertNotNull(template);
        assertEquals("root.default.", template.getFullName());
    }

    @Test(expected = MergeException.class)
    public void testGetTemplateNotFoundInCache() throws MergeException {
    	testFactory.setPersistence(filePersist);
    	testFactory.reset();
    	testFactory.getTemplate("root.bad.fake", "root.fake.", new HashMap<String,String>());
        fail("Template Not Found did not throw exception");
    }

    @Test
    public void testGetMergeOutput() {
        Map<String, String[]> parameters = new HashMap<String,String[]>();
        parameters.put(TemplateFactory.KEY_FULLNAME , 	new String[]{"root.default."});
    	assertEquals("This is the Default Template", testFactory.getMergeOutput(parameters));
    }
    
    @Test
    public void testGetTemplateAsJson() throws MergeException, IOException {
    	String template = testFactory.getTemplateAsJson("root.allDirectives.");
    	File file = new File(templatesDir + File.separator + "root.allDirectives..json");
    	assertEquals(String.join("\n", Files.readAllLines(file.toPath())), template);
    }

    @Test
    public void testGetTemplateNamesJson() throws MergeException {
    	String templates = testFactory.getTemplateNamesJSON("root");
    	String answer = "[\n  \"root.default.\",\n  \"root.allDirectives.\"\n]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testGetDirectiveNamesJson() throws MergeException {
    	String templates = testFactory.getDirectiveNamesJSON();
    	String answer = "[\n  {\n    \"type\": 0,\n    \"name\": \"Require Tags\"\n  },\n  {\n    \"type\": 1,\n    \"name\": \"Replace Value\"\n  },\n  {\n    \"type\": 2,\n    \"name\": \"Insert Subs from Tag\"\n  },\n  {\n    \"type\": 10,\n    \"name\": \"Insert Subs from SQL\"\n  },\n  {\n    \"type\": 11,\n    \"name\": \"Replace Row from SQL\"\n  },\n  {\n    \"type\": 12,\n    \"name\": \"Ceplace Col from SQL\"\n  },\n  {\n    \"type\": 21,\n    \"name\": \"Insert Subs from CSV\"\n  },\n  {\n    \"type\": 22,\n    \"name\": \"Replace Row from CSV\"\n  },\n  {\n    \"type\": 23,\n    \"name\": \"Replace Col from CSV\"\n  },\n  {\n    \"type\": 31,\n    \"name\": \"Insert Subs from HTML\"\n  },\n  {\n    \"type\": 32,\n    \"name\": \"Replace Row from HTML\"\n  },\n  {\n    \"type\": 33,\n    \"name\": \"Replace Col from HTML\"\n  },\n  {\n    \"type\": 34,\n    \"name\": \"Replace from HTML Markup\"\n  }\n]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testSaveDeleteTemplateFromJson() throws MergeException {
    	String template = "{collection=\"test\",name=\"foo\",column=\"\",content=\"Some Content\"}";
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("test.foo."));
    	testFactory.saveTemplateFromJson(template);
    	assertNotNull(testFactory.getTemplate("test.foo.", "", new HashMap<String,String>()));
    	testFactory.reset();
    	assertNotNull(testFactory.getTemplate("test.foo.", "", new HashMap<String,String>()));
    	testFactory.deleteTemplate(template);
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("test.foo."));
    	testFactory.reset();
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("test.foo."));
    }
}
