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

import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.RiotDao;
import org.springframework.util.Assert;

public class Selection {

	private RiotDao dao;
	
	private List<? extends SelectionItem> items;
	
	private List<String> objectIds;
	
	private List<Object> objects;
	
	public Selection(RiotDao dao, List<? extends SelectionItem> items) {
		this.dao = dao;
		this.items = items;
	}

	public int size() {
		return items.size();
	}
	
	protected List<? extends SelectionItem> getItems() {
		return items;
	}
	
	public List<String> getObjectIds() {
		if (objectIds == null) {
			objectIds = Generics.newArrayList(items.size());
			for (SelectionItem item : items) {
				objectIds.add(item.getObjectId());
			}
		}
		return objectIds;
	}
	
	public List<Object> getObjects() {
		if (objects == null) {
			objects = Generics.newArrayList();
			for (SelectionItem item : items) {
				objects.add(dao.load(item.getObjectId()));
			}
		}
		return objects;
	}
	
	public String getSingleObjectId() {
		if (items.isEmpty()) {
			return null;
		}
		Assert.isTrue(items.size() == 1,
				"Selection must not contain more than one item");
		
		return items.get(0).getObjectId();
	}
	
	public Object getSingleObject() {
		if (items.isEmpty()) {
			return null;
		}
		Assert.isTrue(items.size() == 1,
				"Selection must not contain more than one item");
		
		return getObjects().get(0);
	}
	
	public int getFirstRowIndex() {
		if (items.isEmpty()) {
			return -1;
		}
		return items.get(0).getRowIndex();
	}
	
	public int getLastRowIndex() {
		if (items.isEmpty()) {
			return -1;
		}
		return items.get(items.size() - 1).getRowIndex();
	}
}
