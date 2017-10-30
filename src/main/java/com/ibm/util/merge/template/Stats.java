package com.ibm.util.merge.template;

import java.util.ArrayList;
import java.util.Iterator;

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
