package com.ibm.util.merge.template.content;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;

public class TextSegmentTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		TextSegment test = new TextSegment("one");
		assertEquals("one", test.getValue());
	}

	@Test
	public void testFormat() {
		TextSegment test = new TextSegment("123");
		test.format("$ %,.2f"); // TODO - Test Format Strings
		assertEquals("$ 123.00", test.getValue());
	}

	@Test
	public void testEncodeDefault() throws Merge500 {
		TextSegment segment = new TextSegment("This is <a test> String with Ampersand& NewLine/n Tab/t Slash//");
		segment.encode(Segment.ENCODE_NONE);
		assertEquals("This is <a test> String with Ampersand& NewLine/n Tab/t Slash//", segment.getValue());
	}

	@Test
	public void testEncodeHtml() throws Merge500 {
		TextSegment segment = new TextSegment("This is <a test> String with Ampersand& NewLine/n Tab/t Slash//");
		segment.encode(Segment.ENCODE_HTML);
		assertEquals("This is &lt;a test&gt; String with Ampersand&amp; NewLine/n Tab/t Slash//", segment.getValue());
	}

	@Test
	public void testEncodeJson() throws Merge500 {
		TextSegment segment = new TextSegment("This is \"a test\" String with NewLine\n Tab	 Slash\\");
		segment.encode(Segment.ENCODE_JSON);
		assertEquals("This is \\\"a test\\\" String with NewLine\\n Tab\\t Slash\\\\", segment.getValue());
	}

	@Test
	public void testEncodeSql() throws Merge500 {
		TextSegment segment = new TextSegment("This is <a test> String with Ampersand& NewLine/n Tab/t Slash//");
		segment.encode(Segment.ENCODE_SQL);
		assertEquals("This is <a test> String with Ampersand& NewLine/n Tab/t Slash//", segment.getValue());
	}

	@Test
	public void testEncodeXml() throws Merge500 {
		TextSegment segment = new TextSegment("This is <a test> String with Ampersand& NewLine/n Tab/t Slash//");
		segment.encode(Segment.ENCODE_XML);
		assertEquals("This is &lt;a test&gt; String with Ampersand&amp; NewLine/n Tab/t Slash//", segment.getValue());
	}

}
