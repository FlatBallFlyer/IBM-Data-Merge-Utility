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

import com.ibm.util.merge.*;
import com.ibm.util.merge.directive.provider.ProviderHtml;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.template.Template;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class InsertSubsHtmlTest extends InsertSubsTest {
	private String subTemplate = "{\"collection\":\"root\",\"name\":\"sub\",\"content\":\"Row: {A}, Val: {B}\\n\"}";
	private String masterTemplate = "{\"collection\":\"root\",\"name\":\"master\",\"content\":\"Test \\u003ctkBookmark name\\u003d\\\"sub\\\" collection\\u003d\\\"root\\\"/\\u003e\"}";
	private String masterOutput= "Test Row: 1, Val: 2\nRow: 4, Val: 5\n<tkBookmark name=\"sub\" collection=\"root\"/>";


	private JsonProxy jsonProxy;
	private RuntimeContext rtc;

	@Before
	public void setUp() throws Exception {

		rtc = TestUtils.createDefaultRuntimeContext();

		directive = new InsertSubsHtml();
		jsonProxy = new DefaultJsonProxy();
		InsertSubsHtml myDirective = (InsertSubsHtml) directive;
		ProviderHtml myProvider = (ProviderHtml) myDirective.getProvider();
		myProvider.setStaticData("<table><tr><th>A</th><th>B</th><th>C</th></tr><tr><td>1</td><td>2</td><td>3</td></tr><tr><td>4</td><td>5</td><td>6</td></tr></table>");
		TemplateFactory tf = rtc.getTemplateFactory();
		tf.reset();
		Template template2 = jsonProxy.fromJSON(subTemplate, Template.class);
		tf.cache(template2);
		Template template1 = jsonProxy.fromJSON(masterTemplate, Template.class);
		tf.cache(template1);
		template = tf.getTemplate("root.master.", "", new HashMap<>());
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneReplaceColHtml() throws CloneNotSupportedException {
		InsertSubsHtml newDirective = (InsertSubsHtml) directive.clone();
		InsertSubsHtml myDirective = (InsertSubsHtml) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective(rtc);
		assertEquals(masterOutput, template.getContent());
	}

}
