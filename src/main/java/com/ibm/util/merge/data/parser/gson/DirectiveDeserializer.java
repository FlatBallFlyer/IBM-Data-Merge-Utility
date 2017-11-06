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
