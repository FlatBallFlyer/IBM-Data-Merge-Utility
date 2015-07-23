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
import com.ibm.util.merge.directive.provider.ProviderHtml;

public class ReplaceRowHtml extends ReplaceRow {
	
	/**
	 * Simple Constructor
	 */
	public ReplaceRowHtml() {
		super();
		setType(Directives.TYPE_HTML_REPLACE_ROW);
		setProvider(new ProviderHtml());
	}
	
	public ReplaceRowHtml asNew() {
		ReplaceRowHtml to = new ReplaceRowHtml();
		to.copyFieldsFrom((ReplaceRow)this);
		return to;
	}

}
