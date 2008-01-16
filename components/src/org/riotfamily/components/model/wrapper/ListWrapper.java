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

	private List contentList;

	public ListWrapper() {
	}

	public ListWrapper(List list) {
		addAll(list);
	}

	public Object getValue() {
		return this;
	}
	
	public void setValue(Object value) {
		//contentList = (List) value;
	}

	public Object unwrap() {
		if (contentList == null) {
			return null;
		}
		ArrayList result = new ArrayList(contentList.size());
		Iterator it = contentList.iterator();
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
		ArrayList copy = new ArrayList(contentList.size());
		Iterator it = contentList.iterator();
		while (it.hasNext()) {
			ValueWrapper wrapper = (ValueWrapper) it.next();
			if (wrapper != null) {
				copy.add(wrapper.deepCopy());
			}
			else {
				copy.add(null);
			}
		}
		return new ListWrapper(copy);
	}
	
	public Collection getCacheTags() {
		if (contentList == null) {
			return null;
		}
		HashSet result = new HashSet();
		Iterator it = contentList.iterator();
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
		if (contentList != null) {
			contentList.clear();
		}
	}

	public boolean add(Object item) {
		add(size(), item);
		return true;
	}

	public void add(int index, Object item) {
		if (contentList == null) {
			contentList = new ArrayList();
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
		contentList.add(index, wrapper);
		
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
		return contentList != null && contentList.contains(o);
	}

	public boolean containsAll(Collection items) {
		return contentList != null && contentList.containsAll(items);
	}

	public Object get(int index) {
		if (contentList == null) {
			throw new IndexOutOfBoundsException();
		}
		return contentList.get(index);
	}

	public int indexOf(Object o) {
		if (contentList == null) {
			return 0;	
		}
		return contentList.indexOf(o);
	}
	
	public int lastIndexOf(Object o) {
		if (contentList == null) {
			return 0;	
		}
		return contentList.lastIndexOf(o);
	}

	public boolean isEmpty() {
		return contentList == null || contentList.isEmpty();
	}

	public Iterator iterator() {
		if (contentList == null) {
			return Collections.EMPTY_LIST.iterator();
		}
		return contentList.iterator();
	}

	public ListIterator listIterator() {
		if (contentList == null) {
			return Collections.EMPTY_LIST.listIterator();
		}
		return contentList.listIterator();
	}

	public ListIterator listIterator(int index) {
		if (contentList == null) {
			return Collections.EMPTY_LIST.listIterator(index);
		}
		return contentList.listIterator(index);
	}

	public boolean remove(Object o) {
		if (contentList == null) {
			return false;	
		}
		return contentList.remove(o);
	}

	public Object remove(int index) {
		if (contentList == null) {
			throw new IndexOutOfBoundsException();
		}
		return contentList.remove(index);
	}

	public boolean removeAll(Collection items) {
		if (contentList == null) {
			return false;
		}
		return contentList.removeAll(items);
	}

	public boolean retainAll(Collection items) {
		if (contentList == null) {
			return false;
		}
		return contentList.retainAll(items);
	}

	public Object set(int index, Object o) {
		if (contentList == null) {
			throw new IndexOutOfBoundsException();
		}
		return contentList.set(index, o);
	}

	public int size() {
		if (contentList == null) {
			return 0;
		}
		return contentList.size();
	}

	public List subList(int fromIndex, int toIndex) {
		if (contentList == null) {
			throw new IndexOutOfBoundsException();
		}
		return contentList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		if (contentList == null) {
			return new Object[0];
		}
		return contentList.toArray();
	}

	public Object[] toArray(Object[] a) {
		if (contentList == null) {
			return (Object[]) Array.newInstance(
					a.getClass().getComponentType(), 0);
		}
		return contentList.toArray(a);
	}
	
}
