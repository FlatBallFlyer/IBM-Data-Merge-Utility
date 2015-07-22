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

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class ProviderHttpTest extends ProviderTest {
	private MergeContext cf;

	@Before
	public void setup(){
//		cf = new ConnectionFactory(new ConnectionPoolManager());
	}

	@Test
	public void testGetDataFromTag() throws MergeException {
		ProviderHttp myProvider = (ProviderHttp) provider; 
		myProvider.setUrl("");
		myProvider.getData(cf);
		assertFalse(myProvider.getFetchedData().isEmpty());
	}

	@Test
	public void testGetDataFromStatic() throws MergeException {
		ProviderHttp myProvider = (ProviderHttp) provider; 
		myProvider.setUrl("");
		myProvider.setTag("");
		myProvider.getData(cf);
		assertEquals(myProvider.getStaticData(), myProvider.getFetchedData());
	}

	@Test
	public void testGetDataEmpty() throws MergeException {
		ProviderHttp myProvider = (ProviderHttp) provider; 
		myProvider.setUrl("");
		myProvider.setTag("");
		myProvider.setStaticData("");
		myProvider.getData(cf);
		assertEquals(0,myProvider.size());
	}

	@Test
	public void testProviderHttpCloneDirective() throws CloneNotSupportedException {
		ProviderHttp newProvider = (ProviderHttp) provider.clone();
		ProviderHttp myProvider = (ProviderHttp) provider;
		assertNotEquals(myProvider, newProvider);
		assertEquals(0, newProvider.size());
		assertNull(newProvider.getDirective());
		assertEquals(myProvider.getUrl(), 			newProvider.getUrl());
		assertEquals(myProvider.getStaticData(), 	newProvider.getStaticData());
		assertEquals(myProvider.getTag(), 			newProvider.getTag());
	}

	@Test
	public void testSetTag() {
		ProviderHttp myProvider = (ProviderHttp) provider; 
		myProvider.setTag("Test");
		assertEquals("Test", myProvider.getTag());
	}


}
