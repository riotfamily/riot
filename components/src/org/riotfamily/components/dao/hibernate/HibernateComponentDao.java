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
package org.riotfamily.components.dao.hibernate;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.Location;
import org.riotfamily.components.model.VersionContainer;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.riotfamily.riot.security.AccessController;

/**
 * Default ComponentDao implementation that uses Hibernate. All mappings
 * a specified in <code>component.hbm.xml</code> which can be found in the
 * same package.
 */
public class HibernateComponentDao implements ComponentDao {

	private HibernateHelper hibernate;
	
	public HibernateComponentDao() {
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernate = new HibernateHelper(sessionFactory, "components");
	}

	public ComponentList findComponentList(Location location) {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName()	+ " list where "
				+ "list.location = :location and list.parent is null");

		hibernate.setParameter(query, "location", location);
		return (ComponentList) hibernate.uniqueResult(query);
	}

	public void deleteComponentLists(String type, String path) {
		Query query = hibernate.createQuery("delete "
				+ ComponentList.class.getName()	+ " list join list.location l " 
				+ "where l.type = :type and l.path = :path");

		hibernate.setParameter(query, "type", type);
		hibernate.setParameter(query, "path", path);
		hibernate.executeUpdate(query);
	}
	
	public ComponentList findComponentList(VersionContainer parent, String slot) {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName() + " list where list.parent = "
				+":parent and list.location.slot = :slot");

		hibernate.setParameter(query, "parent", parent);
		hibernate.setParameter(query, "slot", slot);
		return (ComponentList) query.uniqueResult();
	}

	public List findComponentLists(String type, String path) {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName() + " list where "
				+ "list.location.type = :type "
				+ "and list.location.path = :path "
				+ "and list.parent is null");

		hibernate.setParameter(query, "type", type);
		hibernate.setParameter(query, "path", path);
		return hibernate.list(query);
	}

	public List findDirtyComponentLists() {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName()
				+ " list where list.dirty = true");

		return hibernate.list(query);
	}
	
	public void copyComponentLists(String type, String oldPath, String newPath) {
		List lists = findComponentLists(type, oldPath);
		if (lists != null) {
			Iterator it = lists.iterator();
			while (it.hasNext()) {
				ComponentList list = (ComponentList) it.next();
				ComponentList copy = list.createCopy(newPath);
				saveComponentList(copy);
			}
		}
	}

	public void deleteComponentList(ComponentList list) {
		hibernate.delete(list);
	}

	public void deleteComponentVersion(ComponentVersion version) {
		hibernate.delete(version);
	}

	public void deleteVersionContainer(VersionContainer container) {
		hibernate.delete(container);
	}

	public ComponentList loadComponentList(Long id) {
		return (ComponentList) hibernate.load(ComponentList.class, id);
	}

	public ComponentVersion loadComponentVersion(Long id) {
		return (ComponentVersion) hibernate.load(ComponentVersion.class, id);
	}

	public VersionContainer loadVersionContainer(Long id) {
		return (VersionContainer) hibernate.load(VersionContainer.class, id);
	}

	public void saveComponentList(ComponentList list) {
		list.setLastModified(new Date());
		list.setLastModifiedBy(AccessController.getCurrentUser().getUserId());
		hibernate.save(list);
	}

	public void saveVersionContainer(VersionContainer container) {
		hibernate.save(container);
	}
	
	public void saveComponentVersion(ComponentVersion version) {
		hibernate.save(version);
	}
	
	public void updateComponentList(ComponentList list) {
		list.setLastModified(new Date());
		list.setLastModifiedBy(AccessController.getCurrentUser().getUserId());
		hibernate.update(list);
		
	}

	public void updateComponentVersion(ComponentVersion version) {
		hibernate.update(version);
	}

	public void updateVersionContainer(VersionContainer container) {
		if (container.getId() != null) {
			hibernate.update(container);
		}
	}
	
	public ComponentVersion getOrCreateVersion(
			VersionContainer container, boolean live) {

		if (live) {
			ComponentVersion liveVersion = container.getLiveVersion();
			if (liveVersion == null) {
				liveVersion = new ComponentVersion();
				container.setLiveVersion(liveVersion);
				saveComponentVersion(liveVersion);
				updateVersionContainer(container);
			}
			return liveVersion;
		}
		else {
			ComponentList list = container.getList();
			if (list != null && !list.isDirty()) {
				list.getOrCreatePreviewContainers();
				updateComponentList(list);
			}
			ComponentVersion previewVersion = container.getPreviewVersion();
			if (previewVersion == null) {
				ComponentVersion liveVersion = container.getLiveVersion();
				if (liveVersion != null) {
					previewVersion = new ComponentVersion(liveVersion);
				}
				else {
					previewVersion = new ComponentVersion();
				}
				saveComponentVersion(previewVersion);
				container.setPreviewVersion(previewVersion);
				updateVersionContainer(container);
			}
			return previewVersion;
		}
	}

}
