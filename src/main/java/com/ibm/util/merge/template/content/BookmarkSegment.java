package com.ibm.util.merge.template.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class BookmarkSegment extends Segment {
	private static final Pattern NAME_PATTERN 		= Pattern.compile("bookmark.*?=.*?\"(.*?)\"");
	private static final Pattern GROUP_PATTERN 		= Pattern.compile("group.*?=.*?\"(.*?)\"");
	private static final Pattern TEMPLATE_PATTERN 	= Pattern.compile("template.*?=.*?\"(.*?)\"");
	private static final Pattern VARYBY_PATTERN 	= Pattern.compile("varyby.*?=.*?\"(.*?)\"");

	private String bookmarkName = "";
	private String templateGroup = "";
	private String templateName = "";
	private String varyByAttribute = "";

	public BookmarkSegment(String source) throws Merge500 {
		super();
		Matcher matcher;
		matcher = NAME_PATTERN.matcher(source);
		if (matcher.find()) {
			this.bookmarkName = matcher.group(1);
		} else {
			throw new Merge500("Malformed Bookmark found " + source);
		}

		matcher = TEMPLATE_PATTERN.matcher(source);
		if (matcher.find()) {
			this.templateName = matcher.group(1);
		} else {
			throw new Merge500("Malformed Bookmark found " + source);
		}

		matcher = GROUP_PATTERN.matcher(source);
		if (matcher.find()) {
			templateGroup = matcher.group(1);
		} else {
			throw new Merge500("Malformed Bookmark found " + source);
		}

		matcher = VARYBY_PATTERN.matcher(source);
		if (matcher.find()) {
			varyByAttribute = matcher.group(1);
		} 
	}

	@Override
	public String getValue() {
		return "";
	}

	public String getBookmarkName() {
		return bookmarkName;
	}

	public String getTemplateGroup() {
		return templateGroup;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getVaryByAttribute() {
		return varyByAttribute;
	}

	public String getDefaultShorthand() {
		return templateGroup + "." + templateName + ".";
	}
	
	public String getTemplateShorthand(DataElement value) throws MergeException {
		String reply = getDefaultShorthand();
		if (!this.varyByAttribute.isEmpty()) {
			if (value.isObject()) {
				if (!value.getAsObject().keySet().contains(varyByAttribute)) {
					throw new Merge500("Vary by Attribute not found in Insert Context:" + varyByAttribute);
				} else {
					reply += value.getAsObject().get(varyByAttribute).getAsPrimitive();
				}
			} else if (value.isPrimitive()) {
				reply += value.getAsPrimitive();
			} else if (value.isList()) {
				throw new Merge500("Insert Context is a list!");
			}
		}
		return reply;
	}
	
}
