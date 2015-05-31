package com.ibm.util.merge.directive.provider;

import static org.junit.Assert.*;
import org.junit.*;

import com.ibm.util.merge.MergeException;

public abstract class ProviderHttpTest extends ProviderTest {
	
	@Test
	public void testGetDataFromUrl() {
		// TODO - How to Unit Test HTTP code?
		// provider.getData();
	}

	@Test
	public void testGetDataFromTag() throws MergeException {
		ProviderHttp myProvider = (ProviderHttp) provider; 
		myProvider.setUrl("");
		myProvider.getData();
		assertFalse(myProvider.getFetchedData().isEmpty());
	}

	@Test
	public void testGetDataFromStatic() throws MergeException {
		ProviderHttp myProvider = (ProviderHttp) provider; 
		myProvider.setUrl("");
		myProvider.setTag("");
		myProvider.getData();
		assertEquals(myProvider.getStaticData(), myProvider.getFetchedData());
	}

	@Test
	public void testGetDataEmpty() throws MergeException {
		ProviderHttp myProvider = (ProviderHttp) provider; 
		myProvider.setUrl("");
		myProvider.setTag("");
		myProvider.setStaticData("");
		myProvider.getData();
		assertEquals(1,myProvider.size());
		assertEquals(0,myProvider.getTable(0).size());
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
		assertEquals("{Test}", myProvider.getTag());
	}


}
