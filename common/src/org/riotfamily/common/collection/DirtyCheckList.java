package org.riotfamily.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class DirtyCheckList<E> extends DirtyCheckSet<E> implements List<E> {

	private List<E> list;

	public DirtyCheckList(List<E> list) {
		super(list);
		this.list = list;
	}

	public void add(int index, E element) {
		dirty();
		list.add(index, element);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		dirty();
		return list.addAll(index, c);
	}

	public E remove(int index) {
		dirty();
		return list.remove(index);
	}

	public E set(int index, E element) {
		dirty();
		return null;
	}
	
	public E get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return new DirtyCheckListIterator<E>(list.listIterator()) {
			protected void dirty() {
				DirtyCheckList.this.dirty();
			}
		};
	}

	public ListIterator<E> listIterator(int index) {
		return new DirtyCheckListIterator<E>(list.listIterator(index)) {
			protected void dirty() {
				DirtyCheckList.this.dirty();
			}
		};
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
	
}
