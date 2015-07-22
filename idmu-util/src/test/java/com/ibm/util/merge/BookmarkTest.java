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

import com.ibm.util.merge.template.Bookmark;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BookmarkTest {
	private Bookmark bookmark1;
	private Bookmark bookmark2;
	private Bookmark bookmark3;

	@Before
	public void setUp() throws Exception {
		bookmark1 = new Bookmark("<tkBookmark name=\"Test1\" collection=\"full\" column=\"type\"/>", 20);
		bookmark2 = new Bookmark("<tkBookmark name=\"Test2\" collection=\"short\" />", 30);
		bookmark3 = new Bookmark("<tkBookmark name=\"Test3\" collection=\"long\" column=\"special\"/>", 40);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClone() throws CloneNotSupportedException {
		Bookmark newBookmark = new Bookmark(bookmark1);
		assertNotEquals(newBookmark, bookmark1);
		assertEquals(bookmark1.getName(), newBookmark.getName());
		assertEquals(bookmark1.getSize(), newBookmark.getSize());
		assertEquals(bookmark1.getStart(), newBookmark.getStart());
		assertEquals(bookmark1.getCollection(), newBookmark.getCollection());
		assertEquals(bookmark1.getColumn(), newBookmark.getColumn());
	}

	@Test
	public void testOffset() throws MergeException {
		assertEquals(20, bookmark1.getStart());
		bookmark1.offest(20);
		assertEquals(40, bookmark1.getStart());
	}

	@Test
	public void testConstructedName() {
		assertEquals("Test1", bookmark1.getName());
		assertEquals("Test2", bookmark2.getName());
		assertEquals("Test3", bookmark3.getName());
	}

	@Test
	public void testConstructedCollection() {
		assertEquals("full",  bookmark1.getCollection());
		assertEquals("short", bookmark2.getCollection());
		assertEquals("long",  bookmark3.getCollection());
	}

	@Test
	public void testConstructedColumn() {
		assertEquals("type", 	bookmark1.getColumn());
		assertEquals("", 		bookmark2.getColumn());
		assertEquals("special", bookmark3.getColumn());
	}

	@Test
	public void testConstructedStart() {
		assertEquals(20, bookmark1.getStart());
		assertEquals(30, bookmark2.getStart());
		assertEquals(40, bookmark3.getStart());
	}

	@Test
	public void testConstructedSize() {
		assertEquals(58, bookmark1.getSize());
		assertEquals(46, bookmark2.getSize());
		assertEquals(61, bookmark3.getSize());
	}

}
