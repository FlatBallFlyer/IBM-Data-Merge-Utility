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

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ibm.util.merge.template.directive.AbstractDirective;

public class DirectiveSerializer implements JsonSerializer<AbstractDirective> {

	@Override
	public JsonElement serialize(AbstractDirective src, Type directive, JsonSerializationContext context) {
		return context.serialize(src);
		
		/* ====================================
		 * If we need to implement different serialization
		switch (src.getType()) {
			case AbstractDirective.TYPE_REPLACE_LIST: 	return context.serialize(src);
			case AbstractDirective.TYPE_REPLACE_HASH: 	return context.serialize(src);
			case AbstractDirective.TYPE_REPLACE_VALUE:	return context.serialize(src);
			case AbstractDirective.TYPE_ENRICH_REST: 	return context.serialize(src);
			case AbstractDirective.TYPE_ENRICH_CLOUD: 	return context.serialize(src);
			case AbstractDirective.TYPE_ENRICH_MONGO: 	return context.serialize(src);
			case AbstractDirective.TYPE_ENRICH_JDBC: 	return context.serialize(src);
			case AbstractDirective.TYPE_PARSE_JSON: 	return context.serialize(src);
			case AbstractDirective.TYPE_PARSE_XML:	 	return context.serialize(src);
			case AbstractDirective.TYPE_PARSE_CSV:	 	return context.serialize(src);
			case AbstractDirective.TYPE_PARSE_HTML:	 	return context.serialize(src);
			case AbstractDirective.TYPE_REQUIRE:	 	return context.serialize(src);
			default: return null;
		}
		 */
	}
}
