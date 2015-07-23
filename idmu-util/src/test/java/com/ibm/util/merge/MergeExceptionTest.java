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

import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.directive.Require;
import com.ibm.util.merge.template.Template;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MergeExceptionTest {
	Template template;
	AbstractDirective directive;

	private MergeContext rtc;

	@Before
	public void setUp() throws Exception {
		rtc = TestUtils.createDefaultRuntimeContext();
		TemplateFactory tf = rtc.getTemplateFactory();
		tf.reset();
		template = tf.getMergableTemplate("system.test.", "", new HashMap<>());
		directive = template.getDirectives().get(0);
	}

	@Test
	public void testMergeExceptionStringString() {
		MergeException e = new MergeException("Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}

	@Test
	public void testMergeExceptionExceptionStringString() {
		MergeException e = new MergeException(new Exception(), "Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}

	@Test
	public void testMergeExceptionTemplate() {
		MergeException e = new MergeException(template, new Exception(), "Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}
	
	@Test
	public void testMergeExceptionTemplateNull() {
		MergeException e = new MergeException(template, null, "Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}
	
	@Test
	public void testMergeExceptionDirective() {
		MergeException e = new MergeException(directive, new Exception(), "Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}
	
	public void testMergeExceptionDirectiveNull() {
		MergeException e = new MergeException(directive, null, "Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}
	
	@Test
	public void testMergeExceptionProvider() {
		MergeException e = new MergeException(directive.getProvider(), new Exception(), "Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}
	
	public void testMergeExceptionProviderNull() {
		MergeException e = new MergeException(directive.getProvider(), null, "Error", "Context");
		assertNotNull(e);
		assertEquals("Error", e.getError());
		assertEquals("Context", e.getContext());
	}
	
	@Test
	public void testGetHtmlErrorMessage() {
		Require req = new Require();
		req.setTags("ONE,TWO,THREE");
		template.addDirective(req);
		MergeException e = new MergeException(req, null, "Error", "Context");
		assertNotNull(e);
		String output = rtc.getHtmlErrorMessage(e);
		assertEquals("<html><head></head><body><p>A Merge Execption has occured: Error <br/> Context</p></body></html>", output);
	}

	@Test
	public void testGetJsonErrorMessage() {
		MergeException e = new MergeException(directive, null, "Error", "Context");
		assertNotNull(e);
		String output = rtc.getJsonErrorMessage(e);
		assertEquals("{\"message\":\"Error\",\"context\":\"Context\"}", output);
	}

}
