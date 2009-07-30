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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.core.screen.list.ListParamsImpl;

public class HqlSortedCollectionDao extends HqlCollectionDao
		implements Swapping {

	private String positionProperty;
	
	public HqlSortedCollectionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void setPositionProperty(String positionProperty) {
		this.positionProperty = positionProperty;
	}

	public boolean canSwap(Object entity, Object parent, 
			ListParams params, int swapWith) {
		
		List<?> items = listInternal(parent, new ListParamsImpl(params));
		int i = items.indexOf(entity) + swapWith;
		return i >= 0 && i < items.size();
	}
	
	public void swapEntity(Object entity, Object parent, ListParams params, 
			int swapWith) {
		
		List<?> items = listInternal(parent, new ListParamsImpl(params));
		
		int i = items.indexOf(entity);
    	Object nextItem = items.get(i + swapWith);
    	
    	Collection<Object> c = getCollection(parent);
    	
    	c.remove(entity);
    	c.remove(nextItem);
    	
    	Object pos1 = PropertyUtils.getProperty(entity, positionProperty);
    	Object pos2 = PropertyUtils.getProperty(nextItem, positionProperty);
    	
    	PropertyUtils.setProperty(entity, positionProperty, pos2);
    	PropertyUtils.setProperty(nextItem, positionProperty, pos1);
    	
    	c.add(entity);
    	c.add(nextItem);
	}
	
}
