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
package com.ibm.util.merge.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ibm.util.merge.directive.*;

import java.lang.reflect.Type;

public class DirectiveSerializer implements JsonSerializer<AbstractDirective> {

	@Override
	public JsonElement serialize(AbstractDirective src, Type directive, JsonSerializationContext context) {
		switch (src.getType()) {
			case Directives.TYPE_REPLACE_VALUE: 	 return context.serialize(src);
			case Directives.TYPE_REQUIRE: 			 return context.serialize(src);
			case Directives.TYPE_TAG_INSERT:		 return context.serialize(src);
			case Directives.TYPE_SQL_INSERT: 		 return context.serialize(src);
			case Directives.TYPE_SQL_REPLACE_COL: 	 return context.serialize(src);
			case Directives.TYPE_SQL_REPLACE_ROW: 	 return context.serialize(src);
			case Directives.TYPE_CSV_INSERT: 		 return context.serialize(src);
			case Directives.TYPE_CSV_REPLACE_COL: 	 return context.serialize(src);
			case Directives.TYPE_CSV_REPLACE_ROW: 	 return context.serialize(src);
//			case Directives.TYPE_HTML_INSERT: 		 return context.serialize((InsertSubsHtml)src);
//			case Directives.TYPE_HTML_REPLACE_COL: 	 return context.serialize((ReplaceColHtml)src);
//			case Directives.TYPE_HTML_REPLACE_ROW: 	 return context.serialize((ReplaceRowHtml)src);
//			case Directives.TYPE_HTML_REPLACE_MARKUP:return context.serialize((ReplaceMarkupHtml)src);
			default: return null;
		}
	}
}
