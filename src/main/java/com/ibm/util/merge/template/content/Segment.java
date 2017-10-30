package com.ibm.util.merge.template.content;

import java.util.HashMap;


public abstract class Segment {
	public static final int ENCODE_NONE 	= 1;
	public static final int ENCODE_HTML 	= 2;
	public static final int ENCODE_SQL 		= 3;
	public static final int ENCODE_JSON 	= 4;
	public static final int ENCODE_XML 		= 5;
	public static final int ENCODE_DEFAULT 	= 6;
	public static final HashMap<String, Integer> ENCODE_VALUES() {
		HashMap<String, Integer> values = new HashMap<String, Integer>();
		values.put("none", 		ENCODE_NONE);
		values.put("html", 		ENCODE_HTML	);
		values.put("sql", 		ENCODE_SQL	);
		values.put("json", 		ENCODE_JSON	);
		values.put("xml", 		ENCODE_XML	);
		values.put("default",	ENCODE_DEFAULT);
		return values;
	}


	private Segment previous;
	private Segment next;
	
	public Segment() {
	}
	
	abstract public String getValue();
	
	public void insert(Segment newSeg) {
		newSeg.next = this;
		newSeg.previous = this.previous;
		if (null != this.previous) {
			this.previous.setNext(newSeg);
		}
		this.previous = newSeg;
	}
	
	public void insert(Content newSeg) {
		for (BookmarkSegment seg : newSeg.getBookmarks()) {
			seg.remove();
		}
		newSeg.getFirst().setPrevious(this.previous);
		newSeg.getLast().setNext(this);
		if (this.previous != null) {
			this.previous.setNext(newSeg.getFirst());
		}
		this.previous = newSeg.getLast();
	}
	
	public void append(Segment newSeg) {
		newSeg.next = this.next;
		newSeg.previous = this;
		if (this.next != null) {
			this.next.setPrevious(newSeg);
		}
		this.next = newSeg;
	}

	public void append(Content newSeg) {
		for (BookmarkSegment seg : newSeg.getBookmarks()) {
			seg.remove();
		}
		newSeg.getFirst().setPrevious(this);
		newSeg.getLast().setNext(this.next);
		if (this.next != null) {
			this.next.setPrevious(newSeg.getLast());
		}
		this.next = newSeg.getFirst();
	}
	
	public void remove() {
		this.getPrevious().setNext(this.next);
		this.getNext().setPrevious(this.previous);
		this.next = null;
		this.previous = null;
	}
	
	public void replaceWith(Segment newSeg) {
		if (null != this.getPrevious()) {
			this.getPrevious().setNext(newSeg);
		}
		if (null != this.getNext()) {
			this.getNext().setPrevious(newSeg);
		}
		newSeg.setNext(this.getNext());
		newSeg.setPrevious(this.getPrevious());
	}
	
	public void replaceWith(Content newContent) {
		if (null != this.getPrevious()) this.getPrevious().setNext(newContent.getFirst());
		if (null != this.getNext()) this.getNext().setPrevious(newContent.getLast());
		newContent.getFirst().setPrevious(this.getPrevious());
		newContent.getLast().setNext(this.getNext());
	}
	
	public Segment getPrevious() {
		return previous;
	}

	public Segment getNext() {
		return next;
	}

	public void setPrevious(Segment previous) {
		if (previous != null) {
			this.previous = previous;
		}
	}

	public void setNext(Segment next) {
		if (next != null) {
			this.next = next;
		}
	}
}

