package com.ibm.util.merge.template.content;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.*;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class BookmarkSegmentTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBookmarkSegment1() throws Merge500 {
		BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" template=\"template\" ");
		assertEquals("name", test.getBookmarkName());
		assertEquals("group", test.getTemplateGroup());
		assertEquals("template", test.getTemplateName());
		assertEquals("", test.getVaryByAttribute());
	}
	
	@Test
	public void testBookmarkSegment2() throws Merge500 {
		BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" template=\"template\" varyby=\"vary\" ");
		assertEquals("name", test.getBookmarkName());
		assertEquals("group", test.getTemplateGroup());
		assertEquals("template", test.getTemplateName());
		assertEquals("vary", test.getVaryByAttribute());
	}
	
	@Test
	public void testBookmarkSegment3() {
		try {
			@SuppressWarnings("unused")
			BookmarkSegment test = new BookmarkSegment("group=\"group\" template=\"template\" varyby=\"vary\" ");
		} catch (Merge500 e) {
			return;
		}
		fail("Exception Expected");
	}
	
	@Test
	public void testBookmarkSegment4() {
		try {
			@SuppressWarnings("unused")
			BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" template=\"template\" varyby=\"vary\" ");
		} catch (Merge500 e) {
			return;
		}
		fail("Exception Expected");
	}
	
	@Test
	public void testBookmarkSegment5() {
		try {
			@SuppressWarnings("unused")
			BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" varyby=\"vary\" ");
		} catch (Merge500 e) {
			return;
		}
		fail("Exception Expected");
	}
	
	@Test
	public void testGetValue() throws Merge500 {
		BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" template=\"template\" ");
		assertEquals("", test.getValue());
	}

	@Test
	public void testGetDefaultShorthand() throws Merge500 {
		BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" template=\"template\" ");
		assertEquals("group.template.", test.getDefaultShorthand());
	}

	@Test
	public void testGetTemplateShorthand() throws MergeException {
		BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" template=\"template\" varyby=\"vary\"");
		DataElement data = new DataPrimitive("Foo");
		assertEquals("group.template.Foo", test.getTemplateShorthand(data));
	}

	@Test
	public void testGetTemplateShorthand2() throws MergeException {
		BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" template=\"template\" varyby=\"vary\"");
		DataElement data = new DataObject();
		data.getAsObject().put("vary", new DataPrimitive("Foo"));
		assertEquals("group.template.Foo", test.getTemplateShorthand(data));
	}

	@Test
	public void testGetTemplateShorthand3() throws Merge500 {
		BookmarkSegment test = new BookmarkSegment("bookmark=\"name\" group=\"group\" template=\"template\" varyby=\"0\"");
		DataElement data = new DataList();
		data.getAsList().add(new DataPrimitive("Foo"));
		try {
			test.getTemplateShorthand(data);
		} catch (MergeException e) {
			return;
		}
		fail("Exception Expected!");
	}

}
