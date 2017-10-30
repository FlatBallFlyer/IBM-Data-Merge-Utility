package com.ibm.util.merge.data.parser.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ibm.util.merge.data.*;

public class DataDeserializer implements JsonDeserializer<DataElement>{

	@Override
	public DataElement deserialize(JsonElement json, Type data, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonArray()) {
			return context.deserialize(json, DataList.class);
		} else if (json.isJsonObject()) {
			return context.deserialize(json, DataObject.class);
		} else if (json.isJsonPrimitive()) {
			return new DataPrimitive(json.getAsJsonPrimitive().getAsString());
		}
		return null;
	}
}
