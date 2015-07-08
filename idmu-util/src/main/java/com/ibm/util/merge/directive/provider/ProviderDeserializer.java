/*
 * Copyright 2015, 2015 IBM
 * 
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
package com.ibm.util.merge.directive.provider;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class ProviderDeserializer implements JsonDeserializer<AbstractProvider> {

	@Override
	public AbstractProvider deserialize(JsonElement json, Type provider, JsonDeserializationContext context) {
		JsonElement jsonType = json.getAsJsonObject().get("type");
		if (jsonType == null) {return null;}
		int myType = jsonType.getAsInt();
		switch (myType) {
			case TYPE_CSV: return context.deserialize(json, ProviderCsv.class);
			case TYPE_SQL: return context.deserialize(json, ProviderSql.class);
			case TYPE_TAG: return context.deserialize(json, ProviderTag.class);
			case TYPE_HTML:return context.deserialize(json, ProviderHtml.class);
			default: return null;
		}
	}
}
