package com.ibm.util.merge.template.content;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;

public class SegmentTest {
	private TextSegment testOne;
	private TextSegment testTwo;
	private TextSegment testThree;
	
	@Before
	public void setUp() throws Exception {
		testOne = new TextSegment("one");
		testTwo = new TextSegment("two");
		testThree = new TextSegment("three");
	}

	@Test
	public void testSegment() {
		assertNull(testOne.getNext());
		assertNull(testOne.getPrevious());
	}

	@Test
	public void testSetGetPrevious() {
		assertNull(testOne.getPrevious());
		testOne.setPrevious(testThree);
		assertEquals(testThree, testOne.getPrevious());
	}

	@Test
	public void testSetGetNext() {
		assertNull(testOne.getNext());
		testOne.setNext(testThree);
		assertEquals(testThree, testOne.getNext());
	}

	@Test
	public void testInsertSegment() {
		testThree.insert(testTwo);
		testTwo.insert(testOne);
		assertEquals(testTwo, 	testThree.getPrevious());
		assertNull(				testThree.getNext());
		assertNull(				testOne.getPrevious());
		assertEquals(testOne, 	testTwo.getPrevious());
		assertEquals(testThree, testTwo.getNext());
		assertEquals(testTwo, 	testOne.getNext());
	}

	@Test
	public void testAppendSegment() {
		testOne.append(testTwo);
		testTwo.append(testThree);
		assertNull(				testOne.getPrevious());
		assertEquals(testTwo, 	testOne.getNext());
		assertEquals(testOne, 	testTwo.getPrevious());
		assertEquals(testThree, testTwo.getNext());
		assertEquals(testTwo, 	testThree.getPrevious());
		assertNull(				testThree.getNext());
	}

	@Test
	public void testCircularAppend() {
		TextSegment master = new TextSegment("Master");
		master.setNext(master);
		master.setPrevious(master);
		master.insert(testOne);
		master.insert(testTwo);
		master.insert(testThree);
		assertEquals(testThree,	master.getPrevious());
		assertEquals(testOne,	master.getNext());
		assertEquals(master,	testOne.getPrevious());
		assertEquals(testTwo, 	testOne.getNext());
		assertEquals(testOne, 	testTwo.getPrevious());
		assertEquals(testThree, testTwo.getNext());
		assertEquals(testTwo, 	testThree.getPrevious());
		assertEquals(master,	testThree.getNext());
	}

	@Test
	public void testCircularInsert() {
		TextSegment master = new TextSegment("Master");
		master.setNext(master);
		master.setPrevious(master);
		master.append(testThree);
		master.append(testTwo);
		master.append(testOne);
		assertEquals(testThree,	master.getPrevious());
		assertEquals(testOne,	master.getNext());
		assertEquals(master,	testOne.getPrevious());
		assertEquals(testTwo, 	testOne.getNext());
		assertEquals(testOne, 	testTwo.getPrevious());
		assertEquals(testThree, testTwo.getNext());
		assertEquals(testTwo, 	testThree.getPrevious());
		assertEquals(master,	testThree.getNext());
	}

	@Test
	public void testInsertContent() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string", TagSegment.ENCODE_NONE);
		testOne.append(testTwo);
		testTwo.append(testThree);
		testTwo.insert(test);
		assertEquals(" string", testTwo.getPrevious().getValue());
	}
	
	@Test
	public void testInsertContentBookmarks() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string with a {bookmark=\"name\" group=\"group\" template=\"template\"}", TagSegment.ENCODE_NONE);
		testOne.append(testTwo);
		testTwo.append(testThree);
		testTwo.insert(test);
		assertEquals(" string with a ", testTwo.getPrevious().getValue());
		assertEquals("This is a ", testOne.getNext().getValue());
	}
	
	@Test
	public void testAppendContent() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string", TagSegment.ENCODE_NONE);
		testOne.append(testTwo);
		testTwo.append(testThree);
		testTwo.append(test);
		assertEquals("This is a ", testTwo.getNext().getValue());
	}

	@Test
	public void testAppendContentBookmarks() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string with a {bookmark=\"name\" group=\"group\" template=\"template\"}", TagSegment.ENCODE_NONE);
		testOne.append(testTwo);
		testTwo.append(testThree);
		testTwo.append(test);
		assertEquals("This is a ", testTwo.getNext().getValue());
		assertEquals(" string with a ", testThree.getPrevious().getValue());
	}

	@Test
	public void testRemove() throws Merge500 {
		testOne.append(testTwo);
		testTwo.append(testThree);
		testTwo.remove();
		assertEquals("one", testThree.getPrevious().getValue());
		assertEquals("three", testOne.getNext().getValue());
	}

	@Test
	public void testReplaceWithSegment() throws Merge500 {
		TextSegment newSeg = new TextSegment("Foo");
		testOne.append(testTwo);
		testTwo.append(testThree);
		testTwo.replaceWith(newSeg);
		assertEquals(newSeg, testOne.getNext());
		assertEquals(newSeg, testThree.getPrevious());
	}

	@Test
	public void testReplaceWithContent() throws Merge500 {
		Content newContent = new Content("{","}","This is a {test} string with a {bookmark=\"name\" group=\"group\" template=\"template\"}", TagSegment.ENCODE_NONE);
		testOne.append(testTwo);
		testTwo.append(testThree);
		testTwo.replaceWith(newContent);
		assertEquals(newContent.getFirst(), testOne.getNext());
		assertEquals(newContent.getLast(), testThree.getPrevious());
		Segment seg = testOne;
		assertEquals("one", seg.getValue());		seg = seg.getNext();
		assertEquals("This is a ", seg.getValue());	seg = seg.getNext();
		assertEquals("{test}", seg.getValue());		seg = seg.getNext();
		assertEquals(" string with a ", seg.getValue());	seg = seg.getNext();
		assertTrue(seg instanceof BookmarkSegment);			seg = seg.getNext();
		assertEquals("three", seg.getValue());
		assertNull(seg.getNext());
	}

}
