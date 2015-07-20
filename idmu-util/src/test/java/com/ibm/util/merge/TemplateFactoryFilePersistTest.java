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

import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.AbstractPersistence;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import com.ibm.util.merge.persistence.HibernatePersistence;
import com.ibm.util.merge.template.Template;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

// todo Finalize Testing Templates and Collections
public class TemplateFactoryFilePersistTest {
	private String templatesFolder = "/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-util/src/test/resources/templates";
	private File templatesDir = new File(templatesFolder);
	private AbstractPersistence filePersist = new FilesystemPersistence(templatesDir, new PrettyJsonProxy());
    private TemplateFactory testFactory = new TemplateFactory(filePersist);
    private int count = 20;
    
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
    	Template template = testFactory.getTemplate("root.default.fake", "root.default", new HashMap<String,String>());
        assertNotNull(template);
        assertEquals("root.default.", template.getFullName());
    }

    @Test(expected = TemplateFactory.TemplateNotFoundException.class)
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
    	assertEquals("This is the default template", testFactory.getMergeOutput(parameters));
    }
    
    @Test
    public void testGetTemplateAsJson() throws MergeException, IOException {
    	String template = testFactory.getTemplateAsJson("root.allDirectives.");
    	File file = new File(templatesDir + File.separator + "root.allDirectives..json");
    	assertEquals(Files.readAllLines(file.toPath()), template);
    }

    @Test
    public void testGetTemplateNamesJson() throws MergeException {
    	String templates = testFactory.getTemplateNamesJSON("root");
    	assertEquals("[\"root.default\",\"root.allDirectives\"]", templates);
    }

    @Test
    public void testSaveDeleteTemplateFromJson() throws MergeException {
    	String template = "[collection='',name='',column='',content='']";
    	assertEquals("errormessage", testFactory.getTemplateAsJson("test.foo."));
    	testFactory.saveTemplateFromJson(template);
    	assertNotNull(testFactory.getTemplate("test.foo.", "", new HashMap<String,String>()));
    	testFactory.reset();
    	assertNotNull(testFactory.getTemplate("test.foo.", "", new HashMap<String,String>()));
    	testFactory.deleteTemplate(template);
    	assertEquals("errormessage", testFactory.getTemplateAsJson("test.foo."));
    	testFactory.reset();
    	assertEquals("errormessage", testFactory.getTemplateAsJson("test.foo."));
    }
}
