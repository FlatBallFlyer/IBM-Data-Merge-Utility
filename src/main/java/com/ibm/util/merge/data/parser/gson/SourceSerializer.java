package com.ibm.util.merge.data.parser.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;

public class SourceSerializer implements JsonSerializer<AbstractSource> {

	@Override
	public JsonElement serialize(AbstractSource src, Type directive, JsonSerializationContext context) {
		switch (src.getType()) {
			case AbstractSource.SOURCE_CLOUDANT: 	return context.serialize(src);
			case AbstractSource.SOURCE_JDBC: 		return context.serialize(src);
			case AbstractSource.SOURCE_MONGO: 		return context.serialize(src);
			case AbstractSource.SOURCE_REST: 		return context.serialize(src);
			default: return null;
		}
	}
}
