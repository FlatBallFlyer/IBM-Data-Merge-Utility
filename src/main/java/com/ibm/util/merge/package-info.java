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
/**
 * Usage:
 *  // Optionally initialize a configuration
 *  Config.load(String, File or URL)
 *  
 *  // Create a template cache
 *  TemplateCache cache = new TemplateCache();
 *  
 *  // Get a Merger for your template
 *  Merger merger = new Merger(cache, "some.template.name");
 *  
 *  // Merge the template and get the output content
 *  Template template = merger.merge();
 *  
 *  // Process the output
 *  template.getMergedOutput().getValue() - as a string
 *  template.getMergedOutput().streamOutput(stream) - as an output stream
 * 
 * @author Mike Storey
 *
 */
package com.ibm.util.merge;