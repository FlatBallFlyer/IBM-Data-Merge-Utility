package com.ibm.util.merge.data.parser.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.CloudantSource;
import com.ibm.util.merge.template.directive.enrich.source.JdbcSource;
import com.ibm.util.merge.template.directive.enrich.source.MongoSource;
import com.ibm.util.merge.template.directive.enrich.source.RestSource;

public class SourceDeserializer implements JsonDeserializer<AbstractSource>{

	@Override
	public AbstractSource deserialize(JsonElement json, Type source, JsonDeserializationContext context)
			throws JsonParseException {
		int myType = json.getAsJsonObject().get("type").getAsInt();
		switch (myType) {
			case AbstractSource.SOURCE_CLOUDANT: 	return context.deserialize(json, CloudantSource.class);
			case AbstractSource.SOURCE_JDBC: 		return context.deserialize(json, JdbcSource.class);
			case AbstractSource.SOURCE_MONGO: 		return context.deserialize(json, MongoSource.class);
			case AbstractSource.SOURCE_REST: 		return context.deserialize(json, RestSource.class);
			default: return null;
		}
	}
}
