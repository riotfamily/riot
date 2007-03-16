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
package org.riotfamily.riot.dao.support;

import java.util.Collection;
import java.util.List;

import org.riotfamily.riot.dao.ListParams;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class StaticRiotDao extends RiotDaoAdapter {

	private List items;
	
	private Class entityClass;
	
	public StaticRiotDao(List items) {
		Assert.notEmpty(items);
		this.items = items;
	}

	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}
	
	public Class getEntityClass() {
		if (entityClass == null) {
			entityClass = items.get(0).getClass(); 
		}
		return entityClass;
	}
	
	public Collection list(Object parent, ListParams params) {
		return items;
	}
	
	public String getObjectId(Object entity) {
		int index = items.indexOf(entity);
		Assert.isTrue(index != -1, "Item not found in list. Make sure " +
				"hashCode() and equals() are properly implemented.");
		
		return String.valueOf(index); 
	}
	
	public Object load(String id) throws DataAccessException {
		int index = Integer.parseInt(id);
		return items.get(index);
	}
}
