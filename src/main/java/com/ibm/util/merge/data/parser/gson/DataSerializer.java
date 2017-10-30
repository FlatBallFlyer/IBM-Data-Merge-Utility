package com.ibm.util.merge.data.parser.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.Merge500;

public class DataSerializer implements JsonSerializer<DataElement> {

	@Override
	public JsonElement serialize(DataElement src, Type dataElement, JsonSerializationContext context) {
		if (src.isPrimitive()) {
			try {
				return new JsonPrimitive(src.getAsPrimitive());
			} catch (Merge500 e) {
				return null;
			}
		} else {
			return context.serialize(src);
		}
		
	}
}
