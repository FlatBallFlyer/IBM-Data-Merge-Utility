/*
 * 
 * Copyright 2015-2017 IBM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
