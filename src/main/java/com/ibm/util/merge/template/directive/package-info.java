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
/**
 * Directives that implement the logic of the merge process. All directives have a type and name, and 
 * implement an execute method. 
 * <p>See Also</p>
 * <ul>	<li>Enrich - Fetch Data: {@link com.ibm.util.merge.template.directive.Enrich}</li>
 * 		<li>Replace - Replace Tags: {@link com.ibm.util.merge.template.directive.Replace}</li>
 * 		<li>Insert - SubTempalts at Book-marks: {@link com.ibm.util.merge.template.directive.Insert}</li>
 * 		<li>Parse - Parse Data: {@link com.ibm.util.merge.template.directive.ParseData}</li>
 * 		<li>Save - A file to archive output: {@link com.ibm.util.merge.template.directive.SaveFile}</li>
 *  </ul>
 * 
 * 
 * @author Mike Storey
 *
 */
package com.ibm.util.merge.template.directive;