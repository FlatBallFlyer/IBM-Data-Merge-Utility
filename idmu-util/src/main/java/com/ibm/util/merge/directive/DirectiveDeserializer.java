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
package com.ibm.util.merge.directive;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class DirectiveDeserializer implements JsonDeserializer<Directive> {

	public Directive deserialize(JsonElement json, Type directive, JsonDeserializationContext context) {
		int myType = json.getAsJsonObject().get("type").getAsInt();
		switch (myType) {
			case Directive.TYPE_REPLACE_VALUE: 		return context.deserialize(json, ReplaceValue.class);
			case Directive.TYPE_REQUIRE: 			return context.deserialize(json, Require.class);
			case Directive.TYPE_SQL_INSERT: 		return context.deserialize(json, InsertSubsSql.class);
			case Directive.TYPE_SQL_REPLACE_COL: 	return context.deserialize(json, ReplaceColSql.class);
			case Directive.TYPE_SQL_REPLACE_ROW: 	return context.deserialize(json, ReplaceRowSql.class);
			case Directive.TYPE_CSV_INSERT: 		return context.deserialize(json, InsertSubsCsv.class);
			case Directive.TYPE_CSV_REPLACE_COL: 	return context.deserialize(json, ReplaceColCsv.class);
			case Directive.TYPE_CSV_REPLACE_ROW: 	return context.deserialize(json, ReplaceRowCsv.class);
			case Directive.TYPE_HTML_INSERT: 		return context.deserialize(json, InsertSubsHtml.class);
			case Directive.TYPE_HTML_REPLACE_COL: 	return context.deserialize(json, ReplaceColHtml.class);
			case Directive.TYPE_HTML_REPLACE_ROW: 	return context.deserialize(json, ReplaceRowHtml.class);
			case Directive.TYPE_HTML_REPLACE_MARKUP: return context.deserialize(json, ReplaceMarkupHtml.class);
			case Directive.TYPE_TAG_INSERT: 		return context.deserialize(json, InsertSubsTag.class);
			default: return null;
		}
	}
}
