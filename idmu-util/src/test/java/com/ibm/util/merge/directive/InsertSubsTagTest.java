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
package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import java.util.HashMap;

import com.ibm.util.merge.*;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.json.JsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import org.junit.Before;
import org.junit.Test;
import com.ibm.util.merge.directive.provider.ProviderTag;

public class InsertSubsTagTest extends InsertSubsTest {
	private String subTemplate = "{\"collection\":\"root\",\"name\":\"sub\",\"content\":\"Foo Is: {Foo}, \"}";
	private String masterTemplate = "{\"collection\":\"root\",\"name\":\"master\",\"content\":\"Test \\u003ctkBookmark name\\u003d\\\"sub\\\" collection\\u003d\\\"root\\\"/\\u003e\"}";
	private String masterOutput= "Test Foo Is: SomeValue1, Foo Is: SomeValue2, Foo Is: SomeValue3, <tkBookmark name=\"sub\" collection=\"root\"/>";
	private ProviderTag myProvider;
	private InsertSubsTag myDirective;
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;
	private JsonProxy jsonProxy;

	@Before
	public void setUp() throws Exception {
		tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates"));
		zf = new ZipFactory();
		cf = new ConnectionFactory();
		jsonProxy = new DefaultJsonProxy();
		directive = new InsertSubsTag();
		myDirective = (InsertSubsTag) directive;
		myProvider = (ProviderTag) myDirective.getProvider();

		tf.reset();
		Template template2 = jsonProxy.fromJSON(subTemplate, Template.class);
		tf.cache(template2);
		Template template1 = jsonProxy.fromJSON(masterTemplate, Template.class);
		tf.cache(template1);
		template = tf.getTemplate("root.master.", "", new HashMap<>());
		template.addDirective(myDirective);

		myProvider.setTag("Foo");
		myProvider.setList(true);
		myProvider.setCondition(ProviderTag.CONDITION_EXISTS);
		template.addReplace("Foo", "SomeValue1,SomeValue2,SomeValue3");
	}

	@Test
	public void testInsertSubsTagClone() throws CloneNotSupportedException {
		InsertSubsTag newDirective = (InsertSubsTag) directive.clone();
		InsertSubsTag myDirective = (InsertSubsTag) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirectiveExistsList() throws MergeException {
		directive.executeDirective(tf, cf, zf);
		assertEquals(masterOutput, template.getContent());
	}
}
