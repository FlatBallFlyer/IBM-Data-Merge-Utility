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
package com.ibm.util.merge.directive.provider;

import static org.junit.Assert.*;
import org.junit.*;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.Directive;

public abstract class ProviderTest {
	protected Provider provider;
	protected Directive directive;
	protected Template template;
	
	@Test
	public void testProviderCloneDirective() throws CloneNotSupportedException {
		Provider newProvider = provider.clone();
		assertNotEquals(provider, newProvider);
		assertEquals(0, newProvider.size());
		assertNull(newProvider.getDirective());
	}

	@Test
	public void testReset() {
		provider.reset();
		DataTable table = provider.getNewTable();
		assertNotNull(table);
		assertEquals(1, provider.size());
	}

	@Test
	public void testGetTable() {
		assertEquals(0, provider.size());
		provider.getNewTable();
		assertEquals(1, provider.size());
	}

	@Test
	public void testGetTableLessThanZero() {
		assertNull(provider.getTable(-1));
	}

	@Test
	public void testGetTableGreaterThanSize() {
		assertNull(provider.getTable(9));
	}

	@Test
	public void testGetNewTable() {
		provider.reset();
		DataTable newTable = provider.getNewTable();
		assertNotNull(newTable);
		assertEquals(1,provider.size());
	}
}