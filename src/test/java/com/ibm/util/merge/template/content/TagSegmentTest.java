package com.ibm.util.merge.template.content;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;

public class TagSegmentTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTagSegment1() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo", Segment.ENCODE_NONE);
		assertEquals("foo", test.getTag());
		assertEquals(Segment.ENCODE_NONE, test.getEncode());
		assertEquals("", test.getFormat());
		assertFalse(test.isParseFirst());
	}

	@Test
	public void testTagSegment2() throws Merge500 {
		TagSegment test = new TagSegment("{","}","tag=\"foo\"", Segment.ENCODE_NONE);
		assertEquals("foo", test.getTag());
		assertEquals(Segment.ENCODE_NONE, test.getEncode());
		assertEquals("", test.getFormat());
	}

	@Test
	public void testTagSegment3() throws Merge500 {
		for (String key : Segment.ENCODE_VALUES().keySet()) {
			if (Segment.ENCODE_VALUES().get(key) != Segment.ENCODE_DEFAULT) {
				TagSegment test = new TagSegment("{","}","tag=\"foo\" encode=\"" + key + "\"", Segment.ENCODE_VALUES().get(key));
				assertEquals("foo", test.getTag());
				assertEquals("", test.getFormat());
				assertEquals(Segment.ENCODE_VALUES().get(key).intValue(), test.getEncode());
			} else {
				try {
					@SuppressWarnings("unused")
					TagSegment test = new TagSegment("{","}","tag=\"foo\" encode=\"" + key + "\"", Segment.ENCODE_VALUES().get(key));
					fail("Invalid Default Encoding exception expected");
				} catch (Merge500 e) {
					continue;
				}
			}
		}
	}

	@Test
	public void testTagSegment4() {
		try {
			@SuppressWarnings("unused")
			TagSegment test = new TagSegment("{","}","tag=\"foo\" encode=\"fap\"", Segment.ENCODE_NONE);
		} catch (Merge500 e) {
			assertEquals("Invalid Encoding in tag=\"foo\" encode=\"fap\"", e.getMessage());
			return;
		}
		fail("Invalid Encode expected");
	}

	@Test
	public void testTagSegment5() throws Merge500 {
		TagSegment test = new TagSegment("{","}","tag=\"foo\" format=\"bar\"", Segment.ENCODE_NONE);
		assertEquals("foo", test.getTag());
		assertEquals(TagSegment.ENCODE_NONE, test.getEncode());
		assertEquals("bar", test.getFormat());
	}

	@Test
	public void testTagSegment6() throws Merge500 {
		TagSegment test = new TagSegment("{","}","tag=\"foo\" parseFirst", Segment.ENCODE_NONE);
		assertEquals("foo", test.getTag());
		assertEquals(TagSegment.ENCODE_NONE, test.getEncode());
		assertTrue(test.isParseFirst());
	}

	@Test
	public void testGetValue() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo", Segment.ENCODE_NONE);
		assertEquals("{foo}", test.getValue());
	}

	@Test
	public void testReplace1() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo", Segment.ENCODE_NONE);
		TextSegment before = new TextSegment("one");
		TextSegment after = new TextSegment("two");
		before.append(test);
		test.append(after);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("foo", "bar");
		
		test.replace(replace, true, 3);
		assertEquals("bar", before.getNext().getValue());
		assertEquals("bar", after.getPrevious().getValue());
	}

	@Test
	public void testReplace2() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo", Segment.ENCODE_NONE);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("fam", "bar");
		test.replace(replace, true, 3);
		assertEquals("{foo}", test.getValue());
		assertNull(test.getPrevious());
		assertNull(test.getNext());
	}

	@Test
	public void testReplace3() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo", Segment.ENCODE_NONE);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("fam", "bar");
		try {
			test.replace(replace, false, 3);
		} catch (Merge500 e) {
			assertEquals("Tag Not Found: foo", e.getMessage());
			return;
		}
		fail("Exception not thrown");
	}

	@Test
	public void testReplace4() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo parseFirst", Segment.ENCODE_NONE);
		TextSegment before = new TextSegment("one");
		TextSegment after = new TextSegment("two");
		before.append(test);
		test.append(after);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("foo", "bar");

		assertTrue(test.isParseFirst());
		assertEquals("foo", test.getTag());
		assertEquals("{foo parseFirst}", test.getValue());
		test.replace(replace, true, 3);
		assertEquals("bar", before.getNext().getValue());
		assertEquals("bar", after.getPrevious().getValue());
	}

	@Test
	public void testReplace5() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo parseFirst", Segment.ENCODE_NONE);
		TextSegment before = new TextSegment("one");
		TextSegment after = new TextSegment("two");
		before.append(test);
		test.append(after);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("foo", "text with {sub} tag");
		replace.put("sub", "embeded");

		assertTrue(test.isParseFirst());
		assertEquals("foo", test.getTag());
		assertEquals("{foo parseFirst}", test.getValue());
		test.replace(replace, true, 3);
		Segment seg = before;
		assertEquals("one", seg.getValue()); seg = seg.getNext();
		assertEquals("text with ", seg.getValue()); seg = seg.getNext();
		assertEquals("embeded", seg.getValue()); seg = seg.getNext();
		assertEquals(" tag", seg.getValue()); seg = seg.getNext();
	}

	@Test
	public void testReplace6() throws Merge500 {
		TagSegment test = new TagSegment("{","}","foo parseFirst", Segment.ENCODE_NONE);
		TextSegment before = new TextSegment("one");
		TextSegment after = new TextSegment("two");
		before.append(test);
		test.append(after);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("foo", "{bookmark=\"foo\" group=\"grp\" template=\"temp\"}");

		test.replace(replace, true, 3);
		assertTrue(before.getNext() instanceof BookmarkSegment);
		BookmarkSegment seg = (BookmarkSegment) before.getNext();
		assertEquals("foo", seg.getBookmarkName());
		assertEquals("grp", seg.getTemplateGroup());
		assertEquals("temp", seg.getTemplateName());
	}

}
 