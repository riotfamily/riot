package org.riotfamily.common.collection;

import java.util.Iterator;

public class DirtyCheckIterator<E> implements Iterator<E> {

	private Iterator<E> it;

	private boolean dirty;

	public DirtyCheckIterator(Iterator<E> it) {
		this.it = it;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	protected void dirty() {
		this.dirty = true;
	}

	// -----------------------------------------------------------------------
	// Methods that modify the backing list
	// -----------------------------------------------------------------------
	
	public void remove() {
		dirty();
		it.remove();
	}
	
	// -----------------------------------------------------------------------
	// Methods that don't modify the backing list
	// -----------------------------------------------------------------------
	
	public boolean hasNext() {
		return it.hasNext();
	}

	public E next() {
		return it.next();
	}
	
}
