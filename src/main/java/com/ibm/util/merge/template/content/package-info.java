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
 * Template Content is the optimized search/replace/insert framework for content. 
 * These optimizations replace string pattern matching and global search replace functions
 * with code that maintains and preserves string pointers and optimizes Java GC
 * 
 * @author Mike Storey
 *
 */
package com.ibm.util.merge.template.content;