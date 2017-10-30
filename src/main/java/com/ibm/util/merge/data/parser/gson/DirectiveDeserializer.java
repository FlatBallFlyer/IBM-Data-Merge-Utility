package com.ibm.util.merge.data.parser.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ibm.util.merge.template.directive.*;

public class DirectiveDeserializer implements JsonDeserializer<AbstractDirective>{

	@Override
	public AbstractDirective deserialize(JsonElement json, Type directive, JsonDeserializationContext context)
			throws JsonParseException {
		int myType = json.getAsJsonObject().get("type").getAsInt();
		switch (myType) {
			case AbstractDirective.TYPE_ENRICH: 			return context.deserialize(json, Enrich.class);
			case AbstractDirective.TYPE_INSERT:				return context.deserialize(json, Insert.class);
			case AbstractDirective.TYPE_PARSE:				return context.deserialize(json, ParseData.class);
			case AbstractDirective.TYPE_REPLACE: 			return context.deserialize(json, Replace.class);
			case AbstractDirective.TYPE_SAVE_FILE:	 		return context.deserialize(json, SaveFile.class);
			default: return null;
		}
	}
}
