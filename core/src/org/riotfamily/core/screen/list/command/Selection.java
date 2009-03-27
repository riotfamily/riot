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
	
	private List<String> objectIds;
	
	private List<Object> objects;
	
	public Selection(RiotDao dao, List<String> objectIds) {
		this.dao = dao;
		this.objectIds = objectIds;
	}

	public int size() {
		return objectIds.size();
	}
	
	public List<String> getObjectIds() {
		return objectIds;
	}
	
	public List<Object> getObjects() {
		if (objects == null) {
			objects = Generics.newArrayList();
			for (String objectId : objectIds) {
				objects.add(dao.load(objectId));
			}
		}
		return objects;
	}
	
	public String getSingleObjectId() {
		if (objectIds.isEmpty()) {
			return null;
		}
		Assert.isTrue(objectIds.size() == 1,
				"Selection must not contain more than one item");
		
		return objectIds.get(0);
	}
	
	public Object getSingleObject() {
		if (objectIds.isEmpty()) {
			return null;
		}
		Assert.isTrue(objectIds.size() == 1,
				"Selection must not contain more than one item");
		
		return getObjects().get(0);
	}
}
