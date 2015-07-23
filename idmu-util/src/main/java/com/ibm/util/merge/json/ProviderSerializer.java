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
import com.ibm.util.merge.directive.provider.*;

import java.lang.reflect.Type;

public class ProviderSerializer implements JsonSerializer<AbstractProvider> {

	@Override
	public JsonElement serialize(AbstractProvider src, Type provider, JsonSerializationContext context) {
		switch (src.getType()) {
			case Providers.TYPE_CSV: 	return context.serialize((ProviderCsv)src);
			case Providers.TYPE_HTML: 	return context.serialize((ProviderHtml)src);
			case Providers.TYPE_SQL:	return context.serialize((ProviderSql)src);
			case Providers.TYPE_TAG: 	return context.serialize((ProviderTag)src);
			default: return null;
		}
	}

}
