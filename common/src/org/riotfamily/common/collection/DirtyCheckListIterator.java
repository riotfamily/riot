package org.riotfamily.common.collection;

import java.util.ListIterator;

public class DirtyCheckListIterator<E> extends DirtyCheckIterator<E> 
		implements ListIterator<E> {

	private ListIterator<E> it;

	public DirtyCheckListIterator(ListIterator<E> it) {
		super(it);
		this.it = it;
	}

	// -----------------------------------------------------------------------
	// Methods that modify the backing list
	// -----------------------------------------------------------------------
	
	public void add(E e) {
		dirty();
		it.add(e);
	}

	public void set(E e) {
		dirty();
		it.set(e);
	}
	
	// -----------------------------------------------------------------------
	// Methods that don't modify the backing list
	// -----------------------------------------------------------------------
	
	public boolean hasPrevious() {
		return it.hasPrevious();
	}

	public int nextIndex() {
		return it.nextIndex();
	}

	public E previous() {
		return it.previous();
	}

	public int previousIndex() {
		return it.previousIndex();
	}
	
}
