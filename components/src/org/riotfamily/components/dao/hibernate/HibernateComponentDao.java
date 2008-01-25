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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.cachius.Cache;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentListLocation;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.riotfamily.riot.security.AccessController;

/**
 * Default ComponentDao implementation that uses Hibernate. All mappings
 * a specified in <code>component.hbm.xml</code> which can be found in the
 * same package.
 */
public class HibernateComponentDao implements ComponentDao {

	private Cache cache;
	
	private HibernateHelper hibernate;
	
	public HibernateComponentDao(Cache cache) {
		this.cache = cache;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernate = new HibernateHelper(sessionFactory, "components");
	}

	public ComponentList findComponentList(ComponentListLocation location) {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName()	+ " list where "
				+ "list.location = :location and list.parent is null");

		hibernate.setParameter(query, "location", location);
		return (ComponentList) hibernate.uniqueResult(query);
	}

	public ComponentList findComponentList(Component parent, String slot) {
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

	public void deleteContent(Content version) {
		hibernate.delete(version);
	}

	public void deleteContentContainer(ContentContainer container) {
		hibernate.delete(container);
	}

	public ComponentList loadComponentList(Long id) {
		return (ComponentList) hibernate.load(ComponentList.class, id);
	}

	public Content loadContent(Long id) {
		return (Content) hibernate.load(Content.class, id);
	}

	public ContentContainer loadContentContainer(Long id) {
		return (ContentContainer) hibernate.get(ContentContainer.class, id);
	}
	
	public Component loadComponent(Long id) {
		return (Component) hibernate.load(Component.class, id);
	}

	public void saveComponentList(ComponentList list) {
		list.setLastModified(new Date());
		list.setLastModifiedBy(AccessController.getCurrentUser().getUserId());
		hibernate.save(list);
	}

	public void saveContentContainer(ContentContainer container) {
		hibernate.save(container);
	}
	
	public void saveContent(Content version) {
		hibernate.save(version);
	}
	
	public void updateComponentList(ComponentList list) {
		list.setLastModified(new Date());
		list.setLastModifiedBy(AccessController.getCurrentUser().getUserId());
		hibernate.update(list);
	}

	public void saveOrUpdateContent(Content version) {
		hibernate.saveOrUpdate(version);
		ComponentCacheUtils.invalidateContent(cache, version);
	}

	public void updateContentContainer(ContentContainer container) {
		if (container.getId() != null) {
			hibernate.update(container);
		}
	}
	
	public boolean publishComponentList(ComponentList componentList) {
		boolean published = false;
		if (componentList.isDirty()) {
			//log.debug("List " + componentList + " is dirty and will be published.");
			published = true;
			List previewList = componentList.getPreviewComponents();
			List liveList = componentList.getLiveComponents();
			if (liveList == null) {
				liveList = new ArrayList();
			}
			else {
				Iterator it = liveList.iterator();
				while (it.hasNext()) {
					Component container = (Component) it.next();
					if (!previewList.contains(container)) {
						deleteContentContainer(container);
						it.remove();
					}
				}
				liveList.clear();
			}
			liveList.addAll(previewList);
			previewList.clear();
			componentList.setDirty(false);
			updateComponentList(componentList);
		}

		Iterator it = componentList.getLiveComponents().iterator();
		while (it.hasNext()) {
			Component container = (Component) it.next();
			published |= publishContainer(container);
		}

		if (published && cache != null) {
			ComponentCacheUtils.invalidateList(cache, componentList);
		}

		return published;
	}
	
	public boolean discardComponentList(ComponentList componentList) {
		boolean discarded = false;
		if (componentList.isDirty()) {
			discarded = true;
			List previewList = componentList.getPreviewComponents();
			List liveList = componentList.getLiveComponents();
			Iterator it = previewList.iterator();
			while (it.hasNext()) {
				Component container = (Component) it.next();
				if (liveList == null || !liveList.contains(container)) {
					deleteContentContainer(container);
					it.remove();
				}
			}
			componentList.setPreviewComponents(null);
			componentList.setDirty(false);
			updateComponentList(componentList);
		}
		Iterator it = componentList.getLiveComponents().iterator();
		while (it.hasNext()) {
			Component container = (Component) it.next();
			discarded |= discardContainer(container);
		}
		return discarded;
	}
	
	public boolean publishContainer(ContentContainer container) {
		boolean published = false;
		if (container instanceof Component) {
			Component component = (Component) container;
			Set childLists = component.getChildLists();
			if (childLists != null) {
				Iterator it = childLists.iterator();
				while (it.hasNext()) {
					ComponentList childList = (ComponentList) it.next();
					published |= publishComponentList(childList);
				}
			}
		}
		Content preview = container.getPreviewVersion();
		if (preview != null) {
			Content liveVersion = container.getLiveVersion();
			container.setLiveVersion(preview);
			container.setPreviewVersion(null);
			if (liveVersion != null) {
				deleteContent(liveVersion);
			}
			updateContentContainer(container);
			ComponentCacheUtils.invalidateContainer(cache, container);
			published = true;
		}
		return published;
	}
	
	public boolean discardContainer(ContentContainer container) {		
		boolean discarded = false;
		if (container instanceof Component) {
			Component component = (Component) container;
			Set childLists = component.getChildLists();
			if (childLists != null) {
				Iterator it = childLists.iterator();
				while (it.hasNext()) {
					ComponentList childList = (ComponentList) it.next();
					discarded |= discardComponentList(childList);
				}
			}
		}
		Content preview = container.getPreviewVersion();
		if (preview != null) {
			container.setPreviewVersion(null);
			updateContentContainer(container);
			deleteContent(preview);
			discarded = true;
		}
		return discarded;
	}

}
