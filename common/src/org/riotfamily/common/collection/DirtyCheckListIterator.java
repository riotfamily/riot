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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
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
