package org.riotfamily.pages.riot.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.RiotDaoAdapter;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.model.Site;
import org.springframework.dao.DataAccessException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SiteRiotDao extends RiotDaoAdapter implements Swapping {

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
	
	public boolean canSwap(Object entity, Object parent, ListParams params,
			int swapWith) {
		
		Site site = (Site) entity;
		List<Site> sites = Site.findAll();
		int i = sites.indexOf(site) + swapWith;
		return i >= 0 && i < sites.size();
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
