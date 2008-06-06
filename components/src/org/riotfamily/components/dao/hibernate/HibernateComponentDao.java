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

import org.hibernate.SessionFactory;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.riot.hibernate.support.HibernateHelper;

/**
 * Default ComponentDao implementation that uses Hibernate. All mappings
 * a specified in <code>component.hbm.xml</code> which can be found in the
 * same package.
 */
public class HibernateComponentDao implements ComponentDao {

	private CacheService cacheService;
	
	private HibernateHelper hibernate;
	
	public HibernateComponentDao(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernate = new HibernateHelper(sessionFactory, "components");
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
		hibernate.save(list);
	}

	public void saveContentContainer(ContentContainer container) {
		hibernate.save(container);
	}
	
	public void saveContent(Content version) {
		hibernate.save(version);
	}
	
	public void updateComponentList(ComponentList list) {
		ComponentCacheUtils.invalidateContainer(cacheService, list.getContainer());
		hibernate.update(list);
	}

	public void updateContentContainer(ContentContainer container) {
		if (container.getId() != null) {
			hibernate.update(container);
		}
	}
	
	public void updateContent(Content content) {
		hibernate.update(content);
	}
	
	public boolean publishContainer(ContentContainer container) {
		boolean published = false;
		Content preview = container.getPreviewVersion();
		if (preview != null) {
			Content liveVersion = container.getLiveVersion();
			container.setLiveVersion(preview.createCopy());
			if (liveVersion != null) {
				deleteContent(liveVersion);
			}
			container.setDirty(false);
			updateContentContainer(container);
			ComponentCacheUtils.invalidateContainer(cacheService, container);
			published = true;
		}
		return published;
	}
	
	public boolean discardContainer(ContentContainer container) {		
		boolean discarded = false;
		Content live = container.getLiveVersion();
		if (live != null) {
			Content preview = container.getPreviewVersion();
			container.setPreviewVersion(live.createCopy());
			if (preview != null) {
				deleteContent(preview);
			}
			container.setDirty(false);
			updateContentContainer(container);
			discarded = true;
		}
		return discarded;
	}

}
