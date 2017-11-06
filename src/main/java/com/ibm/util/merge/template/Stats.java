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
package com.ibm.util.merge.template;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Iterable List of Stat objects
 * 
 * @author Mike Storey
 *
 */
public class Stats implements Iterable<Stat> {
	private ArrayList<Stat> data = new ArrayList<Stat>();

	public Stats() {
		data = new ArrayList<Stat>();
	}

	public void add(Stat stat) {
		data.add(stat);
	}
	public ArrayList<Stat> getData() {
		return data;
	}
	public int size() {
		return data.size();
	}

	@Override
	public Iterator<Stat> iterator() {
		return data.iterator();
	}

}
