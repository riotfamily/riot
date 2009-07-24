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
package org.riotfamily.core.screen.list.command;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.RiotDao;
import org.springframework.util.Assert;

public class Selection implements Iterable<SelectionItem> {

	private RiotDao dao;
	
	private List<SelectionItem> items;
		
	public Selection(RiotDao dao, List<? extends ObjectReference> refs) {
		this.dao = dao;
		this.items = Generics.newArrayList();
		if (refs != null) {
			for (ObjectReference ref : refs) {
				this.items.add(new SelectionItemImpl(ref));
			}
		}
	}

	public Iterator<SelectionItem> iterator() {
		return items.iterator();
	}
	
	public int size() {
		return items.size();
	}
	
	public SelectionItem getFirstItem() {
		if (items.isEmpty()) {
			return null;
		}
		return items.get(0);
	}
	
	public SelectionItem getSingleItem() {
		SelectionItem first = getFirstItem();
		Assert.isTrue(first == null || items.size() == 1,
				"Selection must not contain more than one item");
		
		return first;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Selection) {
			Selection other = (Selection) obj;
			return items.size() == other.items.size() && 
					items.containsAll(other.items);
		}
		return false;
	}
			
	private class SelectionItemImpl implements SelectionItem {
		
		private String objectId;
		
		private Object object;
		
		private String parentNodeId;

		public SelectionItemImpl(ObjectReference ref) {
			this.objectId = ref.getObjectId();
			this.parentNodeId = ref.getParentNodeId();
		}
		
		public String getObjectId() {
			return objectId;
		}
		
		public String getParentNodeId() {
			return parentNodeId;
		}
		
		public Object getObject() {
			if (object == null && objectId != null) {
				object = dao.load(objectId);
			}
			return object;
		}
		
		public void resetObject() {
			object = null;
		}
		
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof ObjectReference) {
				ObjectReference other = (ObjectReference) obj;
				return objectId != null && objectId.equals(other.getObjectId()); 
			}
			return false;
		}

	}

}
