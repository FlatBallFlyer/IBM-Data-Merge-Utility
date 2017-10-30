package com.ibm.util.merge.template.content;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.template.Wrapper;

public class Content extends Segment {
	static final String BOOKMARK = "bookmark";
	
	private String source;
	private String open;
	private String close;
	
	public Content(Wrapper wrapper, String content, int encodeDefault) throws Merge500 {
		initialize(wrapper.front, wrapper.back, content, encodeDefault);
	}
	
	public Content(String open, String close, String content, int encodeDefault) throws Merge500 {
		initialize(open, close, content, encodeDefault);
	}
	
	private void initialize(String open, String close, String content, int encodeDefault) throws Merge500 {
		this.source = content;
		this.open = open;
		this.close = close;
		this.setNext(this);
		this.setPrevious(this);

		int startIndex = 0;
		int endIndex = content.indexOf(open, startIndex);

		while (endIndex >= 0) {
			if (endIndex > startIndex) this.insert(new TextSegment(content.substring(startIndex,endIndex)));
			startIndex = endIndex + open.length();
			endIndex = content.indexOf(close, startIndex);
			if (endIndex < 0) throw new Merge500("Invalid Content - Missing End Tag!");
			String segment = content.substring(startIndex, endIndex);
			if (segment.startsWith(BOOKMARK)) {
				BookmarkSegment newSeg = new BookmarkSegment(segment);
				this.insert(newSeg);
			} else {
				TagSegment newSeg = new TagSegment(open, close, segment, encodeDefault);
				this.insert(newSeg);
			}
			startIndex = endIndex + close.length();
			endIndex = content.indexOf(open,startIndex);
		}

		if (startIndex < content.length()) {
			String value = content.substring(startIndex);
			this.insert(new TextSegment(value));
		}
		
	}
	
	@Override
	public String getValue() {
		StringBuilder value = new StringBuilder();
		Segment seg = this.getFirst();
		while (seg != this) {
			value.append(seg.getValue());
			seg = seg.getNext();
		}
		return value.toString();
	}
	
	public void streamValue(OutputStream stream) throws Merge500 {
		Segment seg = this.getFirst();
		while (seg != this) {
			try {
				stream.write(seg.getValue().getBytes());
			} catch (IOException e) {
				throw new Merge500(e.getMessage());
			}
			seg = seg.getNext();
		}
	}
	
	public void replace(HashMap<String,String> replace, boolean softFail, int nestLimit) throws Merge500 {
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof TagSegment) {
				((TagSegment) seg).replace(replace, softFail, nestLimit);
			}
			seg = seg.getNext();
		}
	}

	public void removeBookmarks() throws Merge500 {
		Segment seg = this.getFirst();
		while (seg != this) {
			Segment segNext = seg.getNext();
			if (seg instanceof BookmarkSegment) {
				seg.remove();
			}
			seg = segNext;
		}
	}

	public String getSource() {
		return source;
	}

	public String getOpen() {
		return open;
	}

	public String getClose() {
		return close;
	}

	public ArrayList<TagSegment> getTags() {
		ArrayList<TagSegment> tags = new ArrayList<TagSegment>();
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof TagSegment) {
				tags.add((TagSegment) seg);
			}
			seg = seg.getNext();
		}
		return tags;
	}

	public ArrayList<BookmarkSegment> getBookmarks() {
		ArrayList<BookmarkSegment> bookmarks = new ArrayList<BookmarkSegment>();
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof BookmarkSegment) {
				bookmarks.add((BookmarkSegment) seg);
			}
			seg = seg.getNext();
		}
		return bookmarks;
	}

	public ArrayList<TextSegment> getTexts() {
		ArrayList<TextSegment> texts = new ArrayList<TextSegment>();
		Segment seg = this.getFirst();
		while (seg != this) {
			if (seg instanceof TextSegment) {
				texts.add((TextSegment) seg);
			}
			seg = seg.getNext();
		}
		return texts;
	}

	public Segment getFirst() {
		return this.getNext();
	}
	
	public Segment getLast() {
		return this.getPrevious();
	}
}
