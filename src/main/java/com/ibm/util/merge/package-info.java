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
 * 		// Get a config object
 * 		Config config = new Config();
 * 
 * 		// Create and Load a template cache
 * 		TemplateCache cache = new TemplateCache(config);
 * 		cache.post(some templates);
 * 
 *		// Get a Merger and Merge the template
 * 		Merger merger = new Merger(cache, config, "template.name.");
 * 
 * 		String output = merger.getMergedOutput().getValue();
 * 
 * @author mikestorey
 *
 */
package com.ibm.util.merge;