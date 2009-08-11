package org.riotfamily.website.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class ObjectGroup<T, I> {
	
	private T title;
	
	private Collection<I> items;

	ObjectGroup(T title, I item, boolean sorted) {
		this.title = title;
		if (sorted) {
			this.items = new TreeSet<I>();
		}
		else {
			this.items = new ArrayList<I>();
		}
		this.items.add(item);
	}

	public void add(I item) {
		items.add(item);
	}
	
	public T getTitle() {
		return this.title;
	}
	
	public Collection<I> getItems() {
		return this.items;
	}
	
	public static<T, I> ObjectGroup<T, I> newInstance(T title, I item, boolean sorted) {
		return new ObjectGroup<T, I>(title, item, sorted);
	}

}