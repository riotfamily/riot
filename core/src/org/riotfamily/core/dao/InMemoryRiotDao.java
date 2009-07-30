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
package org.riotfamily.core.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.riotfamily.common.beans.property.ObjectWrapper;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;

public abstract class InMemoryRiotDao extends RiotDaoAdapter 
		implements Sortable {

	@Override
	public Collection<?> list(Object parent, ListParams params)
			throws DataAccessException {
		
		try {
			Collection<?> items = listInternal(parent);
			ArrayList<Object> list = Generics.newArrayList(items.size());
			for (Object item : items) {
				if (filterMatches(item, params) && searchMatches(item, params)) {
					list.add(item);
				}
			}
			if (params.getOrder() != null && params.getOrder().size() > 0) {
				Order order = params.getOrder().get(0);
				PropertyComparator.sort(list, order);
			}
			return list;
		}
		catch (Exception e) {
			throw new RecoverableDataAccessException(e.getMessage(), e);
		}
	}
	
	protected boolean filterMatches(Object item, ListParams params) {
		if (params.getFilteredProperties() != null) {
			ObjectWrapper itemWrapper = PropertyUtils.createWrapper(item);
			ObjectWrapper filterWrapper = PropertyUtils.createWrapper(params.getFilter());
			for (String prop : params.getFilteredProperties()) {
				Object filterValue = filterWrapper.getPropertyValue(prop);
				if (filterValue != null) {
					Object itemValue = itemWrapper.getPropertyValue(prop);
					if (itemValue instanceof Collection) {
						Collection<?> c = (Collection<?>) itemValue;
						if (!c.contains(filterValue)) {
							return false;
						}
					}
					else if (!filterValue.equals(itemValue)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	protected boolean searchMatches(Object item, ListParams params) {
		if (params.getSearch() == null) {
			return true;
		}
		ObjectWrapper itemWrapper = PropertyUtils.createWrapper(item);
		for (String prop : params.getSearchProperties()) {
			Object itemValue = itemWrapper.getPropertyValue(prop);
			if (itemValue != null && itemValue.toString().indexOf(params.getSearch()) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	protected abstract Collection<?> listInternal(Object parent) throws Exception;
	
}
