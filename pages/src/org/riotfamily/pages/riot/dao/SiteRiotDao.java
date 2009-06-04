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
package org.riotfamily.pages.riot.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.RiotDaoAdapter;
import org.riotfamily.core.dao.SwappableItemDao;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.model.Site;
import org.springframework.dao.DataAccessException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SiteRiotDao extends RiotDaoAdapter implements SwappableItemDao {

	public Class<?> getEntityClass() {
		return Site.class;
	}

	public Collection<?> list(Object parent, ListParams params) throws DataAccessException {
		List<Site> allSites = Site.findAll();
		ArrayList<Site> result = new ArrayList<Site>(allSites.size());
		for (Site site : allSites) {
			if (AccessController.isGranted("list", site)) {
				result.add(site);
			}
		}
		return result;
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		((Site) entity).save();
	}

	public Object update(Object entity) throws DataAccessException {
		Site site = (Site) entity;
		return ((Site) entity).merge();
	}

	public void delete(Object entity, Object parent) throws DataAccessException {
		((Site) entity).delete();
	}

	public String getObjectId(Object entity) {
		return ((Site) entity).getId().toString();
	}

	public Object load(String id) throws DataAccessException {
		return Site.load(Long.valueOf(id));
	}
	
	public void swapEntity(Object entity, Object parent, ListParams params, 
			int swapWith) {
		
		Site site = (Site) entity;
		List<Site> sites = Site.findAll();
		int i = sites.indexOf(site);
		Site other = sites.get(i + swapWith);
		
		long pos = site.getPosition();
		site.setPosition(other.getPosition());
		other.setPosition(pos);
	}
	
}
