/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.model.wrapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ListWrapper extends ValueWrapper implements List {

	private List wrapperList;

	public ListWrapper() {
	}

	public void wrap(Object value) {
		addAll((Collection) value);
	}

	public Object getValue() {
		return this;
	}
	
	public void setValue(Object value) {
	}

	public List getWrapperList() {
		return wrapperList;
	}
	
	public Object unwrap() {
		if (wrapperList == null) {
			return null;
		}
		ArrayList result = new ArrayList(wrapperList.size());
		Iterator it = wrapperList.iterator();
		while (it.hasNext()) {
			ValueWrapper wrapper = (ValueWrapper) it.next();
			if (wrapper != null) {
				result.add(wrapper.unwrap());
			}
			else {
				result.add(null);
			}
		}
		return Collections.unmodifiableList(result);
	}
	
	public ValueWrapper deepCopy() {
		ArrayList list = new ArrayList(wrapperList.size());
		Iterator it = wrapperList.iterator();
		while (it.hasNext()) {
			ValueWrapper wrapper = (ValueWrapper) it.next();
			if (wrapper != null) {
				list.add(wrapper.deepCopy());
			}
			else {
				list.add(null);
			}
		}
		ListWrapper copy = new ListWrapper();
		copy.wrap(list);
		return copy;
	}
	
	public Collection getCacheTags() {
		if (wrapperList == null) {
			return null;
		}
		HashSet result = new HashSet();
		Iterator it = wrapperList.iterator();
		while (it.hasNext()) {
			ValueWrapper wrapper = (ValueWrapper) it.next();
			if (wrapper != null) {
				Collection tags = wrapper.getCacheTags();
				if (tags != null) {
					result.addAll(tags);
				}
			}
		}
		return result;
	}

	public void clear() {
		if (wrapperList != null) {
			wrapperList.clear();
		}
	}

	public boolean add(Object item) {
		add(size(), item);
		return true;
	}

	public void add(int index, Object item) {
		if (wrapperList == null) {
			wrapperList = new ArrayList();
		}
		ValueWrapper wrapper = null;
		if (item != null) { 
			if (item instanceof ValueWrapper) {
				wrapper = (ValueWrapper) item;
			}
			else {
				wrapper = ValueWrapperService.wrap(item);
			}
		}
		wrapperList.add(index, wrapper);
		
	}

	public boolean addAll(Collection items) {
		return addAll(size(), items);
	}

	public boolean addAll(int index, Collection items) {
		if (items == null || items.isEmpty()) {
			return false;
		}
		Iterator it = items.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			add(index++, item);
		}
		return true;
	}

	public boolean contains(Object o) {
		return wrapperList != null && wrapperList.contains(o);
	}

	public boolean containsAll(Collection items) {
		return wrapperList != null && wrapperList.containsAll(items);
	}

	public Object get(int index) {
		if (wrapperList == null) {
			throw new IndexOutOfBoundsException();
		}
		return wrapperList.get(index);
	}

	public int indexOf(Object o) {
		if (wrapperList == null) {
			return 0;	
		}
		return wrapperList.indexOf(o);
	}
	
	public int lastIndexOf(Object o) {
		if (wrapperList == null) {
			return 0;	
		}
		return wrapperList.lastIndexOf(o);
	}

	public boolean isEmpty() {
		return wrapperList == null || wrapperList.isEmpty();
	}

	public Iterator iterator() {
		if (wrapperList == null) {
			return Collections.EMPTY_LIST.iterator();
		}
		return wrapperList.iterator();
	}

	public ListIterator listIterator() {
		if (wrapperList == null) {
			return Collections.EMPTY_LIST.listIterator();
		}
		return wrapperList.listIterator();
	}

	public ListIterator listIterator(int index) {
		if (wrapperList == null) {
			return Collections.EMPTY_LIST.listIterator(index);
		}
		return wrapperList.listIterator(index);
	}

	public boolean remove(Object o) {
		if (wrapperList == null) {
			return false;	
		}
		return wrapperList.remove(o);
	}

	public Object remove(int index) {
		if (wrapperList == null) {
			throw new IndexOutOfBoundsException();
		}
		return wrapperList.remove(index);
	}

	public boolean removeAll(Collection items) {
		if (wrapperList == null) {
			return false;
		}
		return wrapperList.removeAll(items);
	}

	public boolean retainAll(Collection items) {
		if (wrapperList == null) {
			return false;
		}
		return wrapperList.retainAll(items);
	}

	public Object set(int index, Object o) {
		if (wrapperList == null) {
			throw new IndexOutOfBoundsException();
		}
		return wrapperList.set(index, o);
	}

	public int size() {
		if (wrapperList == null) {
			return 0;
		}
		return wrapperList.size();
	}

	public List subList(int fromIndex, int toIndex) {
		if (wrapperList == null) {
			throw new IndexOutOfBoundsException();
		}
		return wrapperList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		if (wrapperList == null) {
			return new Object[0];
		}
		return wrapperList.toArray();
	}

	public Object[] toArray(Object[] a) {
		if (wrapperList == null) {
			return (Object[]) Array.newInstance(
					a.getClass().getComponentType(), 0);
		}
		return wrapperList.toArray(a);
	}
	
}
