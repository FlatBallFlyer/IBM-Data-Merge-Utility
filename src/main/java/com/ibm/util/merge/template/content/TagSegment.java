package com.ibm.util.merge.template.content;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.util.merge.exception.Merge500;

public class TagSegment extends Segment {
	private static final Pattern TAG_PATTERN 		= Pattern.compile("tag.*?=.*?\"(.*?)\"");
	private static final Pattern ENCODE_PATTERN 	= Pattern.compile(" encode.*?=.*?\"(.*?)\"");
	private static final Pattern FORMAT_PATTERN 	= Pattern.compile(" format.*?=.*?\"(.*?)\"");
	private static final Pattern PARSE_PATTERN 		= Pattern.compile(" parseFirst");

	private String tag = "";
	private String open = "";
	private String close = "";
	private String source = "";
	private int encode = ENCODE_DEFAULT;
	private int defaultEncoding = ENCODE_NONE;
	private boolean parseFirst = false;
	private String format = "";
	
	public TagSegment(String open, String close, String source, Integer defaultEncode) throws Merge500 {
		super();
		if (defaultEncode == Segment.ENCODE_DEFAULT) throw new Merge500("Invalid Default Encoding value ");
		this.defaultEncoding = defaultEncode;
		this.open = open;
		this.close = close;
		this.source = source;
		this.parseFirst = false;
		
		Matcher matcher;
		matcher = TAG_PATTERN.matcher(source);
		if (matcher.find()) {
			this.tag = matcher.group(1);
		} else {
			if (source.indexOf(' ') > 0) {
				this.tag = source.substring(0, source.indexOf(' '));
			} else {
				this.tag = source;
			}
		}

		matcher = ENCODE_PATTERN.matcher(source);
		if (matcher.find()) {
			String encodeString = matcher.group(1); 
			if (ENCODE_VALUES().containsKey(encodeString) ) {
				this.encode = ENCODE_VALUES().get(encodeString);
			} else {
				throw new Merge500("Invalid Encoding in " + source);
			}
		}
		if (this.encode == ENCODE_DEFAULT) this.encode = this.defaultEncoding;

		matcher = FORMAT_PATTERN.matcher(source);
		if (matcher.find()) {
			this.format = matcher.group(1);
		}

		matcher = PARSE_PATTERN.matcher(source);
		if (matcher.find()) {
			this.parseFirst = true;
		}
	}

	public void replace(HashMap<String,String> values, boolean softFail, int nestLimit) throws Merge500 {
		String value;
		if (values.containsKey(tag)) {
			value = values.get(tag);
		} else {
			if (!softFail) throw new Merge500("Tag Not Found: " + tag);
			return;
		}
		
		if (this.parseFirst) {
			if (nestLimit < 1) throw new Merge500("Nest Limit Reached");
			Content newContent = new Content(open, close, value, encode);
			for (TextSegment seg : newContent.getTexts()) {
				seg.encode(encode);
			}
			newContent.replace(values, softFail, --nestLimit);
			this.replaceWith(newContent);
		} else {
			TextSegment newSeg = new TextSegment(value);
			newSeg.encode(encode);
			this.replaceWith(newSeg);
		}
	}

	@Override
	public String getValue() {
		return open + source + close;
	}

	public String getTag() {
		return tag;
	}

	public int getEncode() {
		return encode;
	}

	public String getFormat() {
		return format;
	}

	public boolean isParseFirst() {
		return parseFirst;
	}
	
	public void setParseFirst(boolean parse) {
		parseFirst = parse;
	}
	
}