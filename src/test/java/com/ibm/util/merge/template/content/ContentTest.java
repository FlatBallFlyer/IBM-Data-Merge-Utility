package com.ibm.util.merge.template.content;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.template.Wrapper;
public class ContentTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testContent() throws Merge500 {
		Wrapper wrapper = new Wrapper("{","}");
		Content test = new Content(wrapper,"This is a test string", TagSegment.ENCODE_NONE);
		assertEquals("This is a test string", test.getValue());
		assertEquals("This is a test string", test.getSource());
		assertEquals("{", test.getOpen());
		assertEquals("}", test.getClose());
		assertEquals(0, test.getBookmarks().size());
		assertEquals(0, test.getTags().size());
	}

	@Test
	public void testContent1() throws Merge500 {
		Content test = new Content("{","}","This is a test string", TagSegment.ENCODE_NONE);
		assertEquals("This is a test string", test.getValue());
		assertEquals("This is a test string", test.getSource());
		assertEquals("{", test.getOpen());
		assertEquals("}", test.getClose());
		assertEquals(0, test.getBookmarks().size());
		assertEquals(0, test.getTags().size());
	}

	@Test
	public void testContent2() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string", TagSegment.ENCODE_NONE);
		assertEquals("This is a {test} string", test.getValue());
		assertEquals(1, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
	}
	
	@Test
	public void testContent3() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string with {multiple} tags", TagSegment.ENCODE_NONE);
		assertEquals("This is a {test} string with {multiple} tags", test.getValue());
		assertEquals(2, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("multiple", test.getTags().get(1).getTag());
	}
	
	@Test
	public void testContent4() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string with {tag=\"multiple\"} tags", TagSegment.ENCODE_NONE);
		assertEquals("This is a {test} string with {tag=\"multiple\"} tags", test.getValue());
		assertEquals(2, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("multiple", test.getTags().get(1).getTag());
	}
	
	@Test
	public void testContent5() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string with {tag=\"multiple\"} tags and a {bookmark=\"foo\" group=\"grp\" template=\"bar\"} bookmark", TagSegment.ENCODE_NONE);
		assertEquals("This is a {test} string with {tag=\"multiple\"} tags and a  bookmark", test.getValue());
		assertEquals(2, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("multiple", test.getTags().get(1).getTag());
		assertEquals(1, test.getBookmarks().size());
		assertEquals("foo", test.getBookmarks().get(0).getBookmarkName());
	}
	
	@Test
	public void testContent6() throws Merge500 {
		Content test = new Content("{","}","{test}", TagSegment.ENCODE_NONE);
		assertEquals("{test}", test.getValue());
		assertEquals(1, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("{test}", test.getFirst().getValue());
	}
	
	@Test
	public void testContent7() throws Merge500 {
		Content test = new Content("{","}","a{test}", TagSegment.ENCODE_NONE);
		assertEquals("a{test}", test.getValue());
		assertEquals(1, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("{test}", test.getLast().getValue());
	}
	
	@Test
	public void testContent8() throws Merge500 {
		Content test = new Content("{","}","{test}b", TagSegment.ENCODE_NONE);
		assertEquals("{test}b", test.getValue());
		assertEquals(1, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("{test}", test.getFirst().getValue());
	}
	
	@Test
	public void testContent9() throws Merge500 {
		Content test = new Content("{","}","{test1}{test2}{test3}", TagSegment.ENCODE_NONE);
		assertEquals("{test1}{test2}{test3}", test.getValue());
		assertEquals(3, test.getTags().size());
		assertEquals("test2", test.getTags().get(1).getTag());
		assertEquals("{test1}", test.getFirst().getValue());
		assertEquals("{test3}", test.getLast().getValue());
	}
	
	@Test
	public void testContent10() throws Merge500 {
		Content test = new Content("{--","--}","{--test1--}{--test2--}{--test3--}", TagSegment.ENCODE_NONE);
		assertEquals("{--test1--}{--test2--}{--test3--}", test.getValue());
		assertEquals(3, test.getTags().size());
		assertEquals("test2", test.getTags().get(1).getTag());
		assertEquals("{--test1--}", test.getFirst().getValue());
		assertEquals("{--test3--}", test.getLast().getValue());
	}
	
	@Test
	public void testGetMergable() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string with {tag=\"multiple\"} tags and a {bookmark=\"foo\" group=\"grp\" template=\"bar\"} bookmark", TagSegment.ENCODE_NONE);
		assertEquals("This is a {test} string with {tag=\"multiple\"} tags and a  bookmark", test.getValue());
		Segment seg = test.getFirst();
		assertEquals("This is a ", seg.getValue());
		seg = seg.getNext();
		assertEquals("{test}", seg.getValue());
		seg = seg.getNext();
		assertEquals(" string with ", seg.getValue());
		seg = seg.getNext();
		assertEquals("{tag=\"multiple\"}", seg.getValue());
		seg = seg.getNext();
		assertEquals(" tags and a ", seg.getValue());
		seg = seg.getNext();
		assertEquals("", seg.getValue());
		seg = seg.getNext();
		assertEquals(" bookmark", seg.getValue());
		assertEquals(2, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("multiple", test.getTags().get(1).getTag());
		assertEquals(1, test.getBookmarks().size());
		assertEquals("foo", test.getBookmarks().get(0).getBookmarkName());
		test = test.getMergable();
		assertEquals("This is a {test} string with {tag=\"multiple\"} tags and a  bookmark", test.getValue());
		seg = test.getFirst();
		assertEquals("This is a ", seg.getValue());
		seg = seg.getNext();
		assertEquals("{test}", seg.getValue());
		seg = seg.getNext();
		assertEquals(" string with ", seg.getValue());
		seg = seg.getNext();
		assertEquals("{tag=\"multiple\"}", seg.getValue());
		seg = seg.getNext();
		assertEquals(" tags and a ", seg.getValue());
		seg = seg.getNext();
		assertEquals("", seg.getValue());
		seg = seg.getNext();
		assertEquals(" bookmark", seg.getValue());
		assertEquals(2, test.getTags().size());
		assertEquals("test", test.getTags().get(0).getTag());
		assertEquals("multiple", test.getTags().get(1).getTag());
		assertEquals(1, test.getBookmarks().size());
		assertEquals("foo", test.getBookmarks().get(0).getBookmarkName());
	}
	
	@Test
	public void testContentFail1() {
		try {
			@SuppressWarnings("unused")
			Content test = new Content("{--","--}","{--test1--}{--test2", TagSegment.ENCODE_NONE);
		} catch (Merge500 e) {
			return;
		}
		fail("Exception Expected");
	}
	
	@Test
	public void testReplace1() throws Merge500 {
		Content test = new Content("{","}","{test1}{test2}{test3}", TagSegment.ENCODE_NONE);
		assertEquals("{test1}{test2}{test3}", test.getValue());
		assertSame(test.getFirst().getPrevious(), test);
		assertSame(test.getLast().getNext(), test);
		assertEquals("{test1}", test.getFirst().getValue());
		assertEquals("{test2}", test.getFirst().getNext().getValue());
		assertEquals("{test3}", test.getLast().getValue());
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("test1", "value1");
		replace.put("test2", "value2");
		replace.put("test3", "value3");
		test.replace(replace, false, 3);
		assertEquals("value1value2value3", test.getValue());
		assertEquals(0, test.getTags().size());
		assertEquals("value1", test.getFirst().getValue());
		assertEquals("value2", test.getFirst().getNext().getValue());
		assertEquals("value3", test.getLast().getValue());
	}
	
	@Test
	public void testReplace2() throws Merge500 {
		Content test = new Content("{","}","here is {foo parseFirst} for us", Segment.ENCODE_NONE);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("foo", "text with {sub} tags");
		replace.put("sub", "embeded");
		test.replace(replace, true, 3);
		assertEquals("here is text with embeded tags for us", test.getValue());
	}
	
	@Test
	public void testReplace3() throws Merge500 {
		// replace series
		Content test = new Content("{","}","this is {rock parseFirst} test", Segment.ENCODE_NONE);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("rock", "{paper parseFirst}");
		replace.put("paper", "{scissors parseFirst}");
		replace.put("scissors", "{rock parseFirst}");
		try {
			test.replace(replace, true, 3);
		} catch (Merge500 e) {
			return;
		}
		fail("Exception Expected");
	}

	@Test
	public void testReplace4() throws Merge500 {
		// replace with parseFirst=true and looping replace
		Content test = new Content("{","}","this is {foo parseFirst} test", Segment.ENCODE_NONE);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("foo", "{bar parseFirst}");
		replace.put("bar", "bam");
		test.replace(replace, true, 3);
		assertEquals("this is bam test", test.getValue());
	}

	@Test
	public void testReplace5() throws Merge500 {
		Content test = new Content( "<", ">", "<A><B><C><D><E><F><G><H><I><J>", Segment.ENCODE_NONE);
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("A", "a");
		replace.put("B", "b");
		replace.put("C", "c");
		replace.put("D", "d");
		replace.put("E", "e");
		replace.put("F", "f");
		replace.put("G", "g");
		replace.put("H", "h");
		replace.put("I", "i");
		replace.put("J", "j");
		test.replace(replace, true, 3);
		assertEquals("abcdefghij", test.getValue());
	}

	@Test
	public void testGetFirst() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string with {multiple} tags", TagSegment.ENCODE_NONE);
		assertEquals("This is a ", test.getFirst().getValue());
	}

	@Test
	public void testGetLast() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string", TagSegment.ENCODE_NONE);
		assertEquals(" string", test.getLast().getValue());
	}

	@Test
	public void testGetSource() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string", TagSegment.ENCODE_NONE);
		assertEquals("This is a {test} string", test.getSource());
	}

	@Test
	public void testGetOpen() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string", TagSegment.ENCODE_NONE);
		assertEquals("{", test.getOpen());
	}

	@Test
	public void testGetClose() throws Merge500 {
		Content test = new Content("{","}","This is a {test} string", TagSegment.ENCODE_NONE);
		assertEquals("}", test.getClose());
	}

	@Test
	public void testGetTags() throws Merge500 {
		Content test = new Content("{","}","{test1}{test2}{test3}", TagSegment.ENCODE_NONE);
		assertEquals(3, test.getTags().size());
	}

	@Test
	public void testGetBookmarks() throws Merge500 {
		Content test = new Content("{","}","{bookmark=\"foo\" group=\"grp\" template=\"temp\"}{bookmark=\"foo\" group=\"grp\" template=\"temp\"}{bookmark=\"foo\" group=\"grp\" template=\"temp\"}", TagSegment.ENCODE_NONE);
		assertEquals(3, test.getBookmarks().size());
	}

	@Test
	public void testGetBookmarks2() throws Merge500 {
		Content test = new Content("<",">","\t{\n\t\t\"company\" : \"<company>\",\n\t\t\"owners\" : \"<bookmark=\"owners\" group=\"test\" template=\"owner\">\",\n\t\t\"employees\" : \"<bookmark=\"employees\" group=\"test\" template=\"employee\">\",\n\t\t\"customers\" : \"<bookmark=\"customers\" group=\"test\" template=\"customer\">\",\n\t}", TagSegment.ENCODE_NONE);
		assertEquals(3, test.getBookmarks().size());
	}

	@Test
	public void testGetBookmarks3() throws Merge500 {
		Content test = new Content("<",">","{owner=\"<idmuContext>\", <bookmark=\"owner\" group=\"test\" template=\"anOwner\">}<,>", TagSegment.ENCODE_NONE);
		assertEquals(1, test.getBookmarks().size());
	}
	
	@Test
	public void testRemoveBookmarks() throws Merge500 {
		Content test = new Content("{","}","{bookmark=\"foo\" group=\"grp\" template=\"temp\"}{bookmark=\"foo\" group=\"grp\" template=\"temp\"}{bookmark=\"foo\" group=\"grp\" template=\"temp\"}", TagSegment.ENCODE_NONE);
		assertEquals(3, test.getBookmarks().size());
		test.removeBookmarks();
		assertEquals(0, test.getBookmarks().size());
	}

	@Test
	public void testStremValue1() throws Merge500 {
		String content = "This is a {test} string with {tag=\"multiple\"} tags and a {bookmark=\"foo\" group=\"grp\" template=\"bar\"} bookmark";
		String value = "This is a {test} string with {tag=\"multiple\"} tags and a  bookmark";
		Content test = new Content("{","}",content, TagSegment.ENCODE_NONE);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		test.streamValue(bos);
		assertEquals(bos.toString(), value);
	}


}
