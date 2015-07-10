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
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.directive.provider.ProviderSql;

public class ReplaceColSql extends ReplaceCol implements Cloneable {
	
	/**
	 * Simple Constructor
	 */
	public ReplaceColSql() {
		super();
		setType(Directives.TYPE_SQL_REPLACE_COL);
		setProvider(new ProviderSql());
	}

	/** 
	 * Simple Clone constructor
	 * @see com.ibm.util.merge.directive.InsertSubs#clone()
	 */
	@Override
	public ReplaceColSql clone(Template owner) throws CloneNotSupportedException {
		return (ReplaceColSql) super.clone();
	}
}
