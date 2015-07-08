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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class DirectiveDeserializer implements JsonDeserializer<AbstractDirective> {

	@Override
	public AbstractDirective deserialize(JsonElement json, Type directive, JsonDeserializationContext context) {
		int myType = json.getAsJsonObject().get("type").getAsInt();
		switch (myType) {
			case TYPE_REPLACE_VALUE: 		return context.deserialize(json, ReplaceValue.class);
			case TYPE_REQUIRE: 			return context.deserialize(json, Require.class);
			case TYPE_SQL_INSERT: 		return context.deserialize(json, InsertSubsSql.class);
			case TYPE_SQL_REPLACE_COL: 	return context.deserialize(json, ReplaceColSql.class);
			case TYPE_SQL_REPLACE_ROW: 	return context.deserialize(json, ReplaceRowSql.class);
			case TYPE_CSV_INSERT: 		return context.deserialize(json, InsertSubsCsv.class);
			case TYPE_CSV_REPLACE_COL: 	return context.deserialize(json, ReplaceColCsv.class);
			case TYPE_CSV_REPLACE_ROW: 	return context.deserialize(json, ReplaceRowCsv.class);
			case TYPE_HTML_INSERT: 		return context.deserialize(json, InsertSubsHtml.class);
			case TYPE_HTML_REPLACE_COL: 	return context.deserialize(json, ReplaceColHtml.class);
			case TYPE_HTML_REPLACE_ROW: 	return context.deserialize(json, ReplaceRowHtml.class);
			case TYPE_HTML_REPLACE_MARKUP: return context.deserialize(json, ReplaceMarkupHtml.class);
			case TYPE_TAG_INSERT: 		return context.deserialize(json, InsertSubsTag.class);
			default: return null;
		}
	}
}
