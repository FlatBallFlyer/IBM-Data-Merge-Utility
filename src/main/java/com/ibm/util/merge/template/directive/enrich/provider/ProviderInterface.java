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
package com.ibm.util.merge.template.directive.enrich.provider;

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;

/**
 * Defines the simple Interface required by all Providers
 * 
 * @author Mike Storey
 *
 */
public interface ProviderInterface {

	/**
	 * The meat of a provider - go get the data and put it in the DataManager
	 * 
	 * @param enrichCommand
	 * @param wrapper
	 * @param context
	 * @param replace
	 * @return
	 * @throws MergeException
	 */
	public abstract DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException;

	/**
	 * @return Source Name
	 */
	public abstract String getSource();

	/**
	 * @return DB Name
	 */
	public abstract String getDbName();

	/**
	 * @return Context
	 */
	public abstract Merger getContext();

}
