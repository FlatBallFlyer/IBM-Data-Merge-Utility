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
import com.ibm.idmu.api.PoolManagerConfiguration;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.persistence.JdbcTemplatePersistence;
import com.ibm.util.merge.template.Template;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TemplateFactoryJdbcPersistTest {
    private String templatesPersistencePoolName = "idmuTemplates";
    private File poolsPropertiesPath = new File("src/test/resources/properties/databasePools.properties");
    private File outputDirPath = new File("/src/test/resources/testout");
    private final TemplateFactory testFactory;


    public TemplateFactoryJdbcPersistTest() {
    	super();
        ConnectionPoolManager poolManager = new ConnectionPoolManager();
        PoolManagerConfiguration config = PoolManagerConfiguration.fromPropertiesFile(poolsPropertiesPath);
        poolManager.applyConfig(config);
        JsonProxy jsonProxy = new DefaultJsonProxy();
        JdbcTemplatePersistence jdbcPersistence = new JdbcTemplatePersistence(poolManager);
        jdbcPersistence.setPoolName(templatesPersistencePoolName);
        testFactory = new TemplateFactory(jdbcPersistence, jsonProxy, outputDirPath, poolManager);
    }
    
    @Before
    public void setUp() throws Exception {
    	testFactory.reset();
    }

    @Test
    public void testReset() throws MergeException {
        assertEquals(1, testFactory.size());
    }

    @Test
    public void testGetMergeOutput() {
        Map<String, String[]> parameters = new HashMap<String,String[]>();
    	assertEquals("This is the Default Template", testFactory.getMergeOutput(parameters));
    }
    
    @Test
    public void testGetTemplateFullNameFoundInCache() throws MergeException {
    	Template template = testFactory.getMergableTemplate("system.default.", "foo.fake", new HashMap<String,String>());
        assertNotNull(template);
        assertEquals("system.default.", template.getFullName());
    }

    @Test
    public void testGetTemplateShortNameFoundInCache() throws MergeException {
    	Template template = testFactory.getMergableTemplate("foo.fake", "system.default.", new HashMap<String,String>());
        assertNotNull(template);
        assertEquals("system.default.", template.getFullName());
    }

    @Test(expected = MergeException.class)
    public void testGetTemplateNotFoundInCache() throws MergeException {
    	testFactory.getMergableTemplate("foo.fake.one", "foo.fake.two", new HashMap<String,String>());
        fail("Template Not Found did not throw exception");
    }

    @Test
    public void testGetTemplateAsJson() throws MergeException, IOException {
    	String template = testFactory.getTemplateAsJson("system.default.");
    	String answer = "{\"collection\":\"system\",\"name\":\"default\",\"columnValue\":\"\",\"description\":\"\",\"outputFile\":\"\",\"content\":\"This is the Default Template\",\"directives\":[]}";
    	assertEquals(answer, template);
    }

    @Test
    public void testGetTemplateNamesJsonCollection() throws MergeException {
    	String templates = testFactory.getTemplateNamesJSON("system");
    	String answer = "[{\"collection\":\"system\",\"name\":\"default\",\"columnValue\":\"\"}]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testGetTemplateNamesJsonCollectionName() throws MergeException {
    	String templates = testFactory.getTemplateNamesJSON("system", "default");
    	String answer = "[{\"collection\":\"system\",\"name\":\"default\",\"columnValue\":\"\"}]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testGetDirectiveNamesJson() throws MergeException {
    	String templates = testFactory.getDirectiveNamesJSON();
    	String answer = "[{\"tags\":[],\"type\":0,\"softFail\":false,\"description\":\"Require Tags\"},{\"from\":\"\",\"to\":\"\",\"type\":1,\"softFail\":false,\"description\":\"Add a Repalce Value\"},{\"notLast\":[],\"onlyLast\":[],\"type\":2,\"softFail\":false,\"description\":\"Insert Subtemplates from Tagged Data\",\"provider\":{\"tag\":\"\",\"condition\":0,\"list\":false,\"value\":\"\",\"type\":2}},{\"notLast\":[],\"onlyLast\":[],\"type\":21,\"softFail\":false,\"description\":\"Insert Subtemplates from CSV Data\",\"provider\":{\"url\":\"\",\"tag\":\"\",\"staticData\":\"\",\"type\":3}},{\"fromColumn\":\"\",\"toColumn\":\"\",\"type\":23,\"softFail\":false,\"description\":\"Add Replace from CSV Column\",\"provider\":{\"url\":\"\",\"tag\":\"\",\"staticData\":\"\",\"type\":3}},{\"type\":22,\"softFail\":false,\"description\":\"Add Replace from CSV Row\",\"provider\":{\"url\":\"\",\"tag\":\"\",\"staticData\":\"\",\"type\":3}},{\"notLast\":[],\"onlyLast\":[],\"type\":10,\"softFail\":false,\"description\":\"Insert Subtemplates from SQL Data\",\"provider\":{\"source\":\"\",\"columns\":\"\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"type\":11,\"softFail\":false,\"description\":\"Add Replace from SQL Row\",\"provider\":{\"source\":\"\",\"columns\":\"\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"fromColumn\":\"\",\"toColumn\":\"\",\"type\":12,\"softFail\":false,\"description\":\"Add Replace from SQL Column\",\"provider\":{\"source\":\"\",\"columns\":\"\",\"from\":\"\",\"where\":\"\",\"type\":1}}]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testCollectionNamesJson() throws MergeException {
    	String templates = testFactory.getCollectionNamesJSON();
    	String answer = "[{\"collection\":\"system\"}]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testGetTemplatesJson() throws MergeException {
    	List<String> collections = new ArrayList<String>();
    	collections.add("root");
    	collections.add("system");
    	String templates = testFactory.getTemplatesJSON(collections);
    	String answer = "{\"templates\":[{\"collection\":\"system\",\"name\":\"default\",\"columnValue\":\"\",\"description\":\"\",\"outputFile\":\"\",\"content\":\"This is the Default Template\",\"directives\":[]}]}";
    	assertEquals(answer, templates);
    }

    @Test
    public void testSaveDeleteTemplateFromJson() throws MergeException, IOException {
    	File file = new File("src/test/resources/templates/system.test.all.json");
    	String template = new String(Files.readAllBytes(file.toPath()));;
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("system.test.all"));
    	testFactory.saveTemplateFromJson(template);
    	assertNotNull(testFactory.getMergableTemplate("system.test.all", "", new HashMap<String, String>()));
    	testFactory.reset();
    	assertNotNull(testFactory.getMergableTemplate("system.test.all", "", new HashMap<String, String>()));
    	testFactory.deleteTemplate("system.test.all");
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("system.test.all"));
    	testFactory.reset();
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("system.test.all"));
    }
}
 