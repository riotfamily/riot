/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	public boolean isEmpty() {
		return size() == 0;
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
	
	public void resetObjects() {
		for (SelectionItem item : items) {
			item.resetObject();
		}
	}
	
	@Override
	public int hashCode() {
		return 0;
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
		
		@Override
		public int hashCode() {
			return objectId != null ? objectId.hashCode() : 0;
		}
		
		@Override
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
