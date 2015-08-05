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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TemplateFactoryJdbcPersistTest {
	private final JsonProxy proxy;
	private final File outputDir;
	private final File poolsPropertiesPath;
    private final ConnectionPoolManager poolManager;
    private final PoolManagerConfiguration config;
	private final JdbcTemplatePersistence persist;
    private final TemplateFactory testFactory;

    public TemplateFactoryJdbcPersistTest() {
    	super();
    	this.proxy = new DefaultJsonProxy();
    	this.outputDir = new File("src/test/resources/testout/");
    	this.poolsPropertiesPath = new File("src/test/resources/properties/testPools.properties");
    	this.poolManager = new ConnectionPoolManager();
    	this.config = PoolManagerConfiguration.fromPropertiesFile(poolsPropertiesPath);
        poolManager.applyConfig(config);
        this.persist = new JdbcTemplatePersistence(poolManager, null);
        this.testFactory = new TemplateFactory(persist, proxy, outputDir, poolManager);
    	
    }
    @Before
    public void setUp() throws Exception {
    	testFactory.reset();
    }

    @Test
    public void testReset() throws MergeException {
        assertNotEquals(0, testFactory.size());
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
    public void testGetMergeOutput() {
        Map<String, String[]> parameters = new HashMap<String,String[]>();
        parameters.put(TemplateFactory.KEY_FULLNAME, new String[]{"system.default."});
    	assertEquals("This is the Default Template", testFactory.getMergeOutput(parameters));
    }
    
    @Test
    public void testGetTemplateAsJson() throws MergeException, IOException {
    	String template = testFactory.getTemplateAsJson("root.allDirectives.");
    	String answer = "{\"collection\":\"root\",\"name\":\"allDirectives\",\"columnValue\":\"\",\"description\":\"\",\"outputFile\":\"\",\"content\":\"This is a test of the encodeing \\u003ctkBookmark collection\\u003d\\\"root\\\" name\\u003d\\\"test\\\" /\\u003e\",\"directives\":[{\"tags\":[\"Foo\",\"empty\"],\"type\":0,\"softFail\":false,\"description\":\"TestRequire\"},{\"from\":\"Foo\",\"to\":\"Test Foo Value\",\"type\":1,\"softFail\":false,\"description\":\"Test Replace1\"},{\"notLast\":[\"empty\"],\"onlyLast\":[\"\"],\"type\":2,\"softFail\":false,\"description\":\"TestInsertSubsTag\",\"provider\":{\"tag\":\"Foo\",\"condition\":0,\"list\":false,\"value\":\"\",\"type\":2}},{\"notLast\":[\"empty\"],\"onlyLast\":[\"\"],\"type\":10,\"softFail\":false,\"description\":\"TestInsertSubsSql\",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"type\":11,\"softFail\":false,\"description\":\"TestReplaceRowSql\",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"type\":12,\"softFail\":false,\"description\":\"TestReplaceColSql\",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"notLast\":[\"empty\"],\"onlyLast\":[\"\"],\"type\":21,\"softFail\":false,\"description\":\"TestInsertSubsCsv\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":3}},{\"type\":22,\"softFail\":false,\"description\":\"TestReplaceRowCsv\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":3}},{\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"type\":23,\"softFail\":false,\"description\":\"TestReplaceColCsv\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":3}},{\"notLast\":[\"empty\"],\"onlyLast\":[\"\"],\"type\":31,\"softFail\":false,\"description\":\"TestInsertSubsHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}},{\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"type\":33,\"softFail\":false,\"description\":\"TestReplaceColHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}},{\"type\":32,\"softFail\":false,\"description\":\"TestReplaceRowHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}},{\"pattern\":\"TestPattern\",\"type\":34,\"softFail\":false,\"description\":\"TestMarkupSubsHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}}]}";
    	assertEquals(answer, template);
    }

    @Test
    public void testGetTemplateNamesJsonCollection() throws MergeException {
    	String templates = testFactory.getTemplateNamesJSON("root");
    	String answer = "[{\"collection\":\"root\",\"name\":\"allDirectives\",\"columnValue\":\"\"},{\"collection\":\"root\",\"name\":\"csvDefault\",\"columnValue\":\"\"},{\"collection\":\"root\",\"name\":\"default\",\"columnValue\":\"\"},{\"collection\":\"root\",\"name\":\"generate\",\"columnValue\":\"\"},{\"collection\":\"root\",\"name\":\"list\",\"columnValue\":\"\"},{\"collection\":\"root\",\"name\":\"listMember\",\"columnValue\":\"\"},{\"collection\":\"root\",\"name\":\"markup\",";
    	assertEquals(answer, templates.substring(0, answer.length()));
    }

    @Test
    public void testGetTemplateNamesJsonCollectionName() throws MergeException {
    	String templates = testFactory.getTemplateNamesJSON("system", "errHtml");
    	String answer = "[{\"collection\":\"system\",\"name\":\"errHtml\",\"columnValue\":\"\"},{\"collection\":\"system\",\"name\":\"errHtml\",\"columnValue\":\"com.ibm.util.merge.directive.Require\"}]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testGetDirectiveNamesJson() throws MergeException {
    	String templates = testFactory.getDirectiveNamesJSON();
    	String answer = "[{\"tags\":[],\"type\":0,\"softFail\":false,\"description\":\"Require Tags\"},{\"from\":\"\",\"to\":\"\",\"type\":1,\"softFail\":false,\"description\":\"Add a Repalce Value\"},{\"notLast\":[],\"onlyLast\":[],\"type\":2,\"softFail\":false,\"description\":\"Insert Subtemplates from Tagged Data\",\"provider\":{\"tag\":\"\",\"condition\":0,\"list\":false,\"value\":\"\",\"type\":2}},{\"notLast\":[],\"onlyLast\":[],\"type\":10,\"softFail\":false,\"description\":\"Insert Subtemplates from SQL Data\",\"provider\":{\"source\":\"\",\"columns\":\"\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"type\":11,\"softFail\":false,\"description\":\"Add Replace from SQL Row\",\"provider\":{\"source\":\"\",\"columns\":\"\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"fromColumn\":\"\",\"toColumn\":\"\",\"type\":12,\"softFail\":false,\"description\":\"Add Replace from SQL Column\",\"provider\":{\"source\":\"\",\"columns\":\"\",\"from\":\"\",\"where\":\"\",\"type\":1}},{\"notLast\":[],\"onlyLast\":[],\"type\":21,\"softFail\":false,\"description\":\"Insert Subtemplates from CSV Data\",\"provider\":{\"staticData\":\"\",\"url\":\"\",\"tag\":\"\",\"type\":3}},{\"type\":22,\"softFail\":false,\"description\":\"Add Replace from CSV Row\",\"provider\":{\"staticData\":\"\",\"url\":\"\",\"tag\":\"\",\"type\":3}},{\"fromColumn\":\"\",\"toColumn\":\"\",\"type\":23,\"softFail\":false,\"description\":\"Add Replace from CSV Column\",\"provider\":{\"staticData\":\"\",\"url\":\"\",\"tag\":\"\",\"type\":3}},{\"notLast\":[],\"onlyLast\":[],\"type\":31,\"softFail\":false,\"description\":\"Insert Subtemplates from HTML Data\",\"provider\":{\"staticData\":\"\",\"url\":\"\",\"tag\":\"\",\"type\":4}},{\"type\":32,\"softFail\":false,\"description\":\"Add Replace from Html Row\",\"provider\":{\"staticData\":\"\",\"url\":\"\",\"tag\":\"\",\"type\":4}},{\"fromColumn\":\"\",\"toColumn\":\"\",\"type\":33,\"softFail\":false,\"description\":\"Add Replace from Html Column\",\"provider\":{\"staticData\":\"\",\"url\":\"\",\"tag\":\"\",\"type\":4}},{\"type\":34,\"softFail\":false,\"description\":\"Add Replace from HTML Markup\",\"provider\":{\"staticData\":\"\",\"url\":\"\",\"tag\":\"\",\"type\":4}}]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testCollectionNamesJson() throws MergeException {
    	String templates = testFactory.getCollectionNamesJSON();
    	String answer = "[{\"collection\":\"contact\"},{\"collection\":\"csvDef\"},{\"collection\":\"csvTag\"},{\"collection\":\"csvUrl\"},{\"collection\":\"htmlDef\"},{\"collection\":\"htmlTag\"},{\"collection\":\"htmlUrl\"},{\"collection\":\"jdbc\"},{\"collection\":\"root\"},{\"collection\":\"safety\"},{\"collection\":\"special\"},{\"collection\":\"system\"}]";
    	assertEquals(answer, templates);
    }

    @Test
    public void testGetTemplatesJson() throws MergeException {
    	List<String> collections = new ArrayList<String>();
    	collections.add("root");
    	collections.add("system");
    	String templates = testFactory.getTemplatesJSON(collections);
    	String answer = "[{\"collection\":\"system\",\"name\":\"test\",\"columnValue\":\"\",\"description\":\"The Testing Template\",\"outputFile\":\"\",\"content\":\"This is the Testing Template Content\",\"directives\":[{\"fromColumn\":\"FROM_VALUE\",\"toColumn\":\"TO_VALUE\",\"type\":23,\"softFail\":false";
    	assertEquals(answer, templates.substring(0, answer.length()));
    }

    @Test
    public void testSaveDeleteTemplateFromJson() throws MergeException {
    	String template = "{collection=\"test\",name=\"foo\",column=\"\",content=\"Some Content\"}";
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("test.foo."));
    	testFactory.saveTemplateFromJson(template);
    	assertNotNull(testFactory.getMergableTemplate("test.foo.", "", new HashMap<String, String>()));
    	testFactory.reset();
    	assertNotNull(testFactory.getMergableTemplate("test.foo.", "", new HashMap<String, String>()));
    	testFactory.deleteTemplate("test.foo.");
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("test.foo."));
    	testFactory.reset();
    	assertEquals("NOT FOUND", testFactory.getTemplateAsJson("test.foo."));
    }
}
 