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
package org.riotfamily.components.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.wrapper.ComponentListWrapper;
import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default ComponentDao implementation that uses Hibernate. All mappings
 * a specified in <code>component.hbm.xml</code> which can be found in the
 * same package.
 */
@Transactional
public class HibernateComponentDao implements ComponentDao {

	private CacheService cacheService;
	
	private HibernateHelper hibernate;
	
	public HibernateComponentDao(SessionFactory sessionFactory, CacheService cacheService) {
		this.hibernate = new HibernateHelper(sessionFactory, "components");
		this.cacheService = cacheService;
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
		
	public void updateContent(Content content) {
		hibernate.update(content);
	}

	public ContentContainer findContainerForComponent(Component component) {
		Query query = hibernate.createQuery(
				"from ComponentListWrapper where value = :list")
				.setParameter("list", component.getList());
		
		ComponentListWrapper wrapper = hibernate.uniqueResult(query);
		return findContainerForWrapper(wrapper);
	}
	
	public ContentContainer findContainerForWrapper(ValueWrapper<?> wrapper) {
		if (wrapper == null) {
			return null;
		}
		Query query = hibernate.createQuery("from Content c where :wrapper in elements(c.wrappers)");
		query.setParameter("wrapper", wrapper);
		Content content = hibernate.uniqueResult(query);
		if (content != null) {
			query = hibernate.createQuery("from ContentContainer contentContainer where contentContainer.liveVersion = :content or contentContainer.previewVersion = :content");
			query.setParameter("content", content);
			return hibernate.uniqueResult(query);
		}
		else {
			query = hibernate.createQuery("from ListWrapper l where :wrapper in elements(l.wrapperList)");
			query.setParameter("wrapper", wrapper);
			ValueWrapper<?> parent = hibernate.uniqueResult(query);
			
			if (parent == null) {			
				query = hibernate.createQuery("from MapWrapper m where :wrapper in elements(l.wrapperMap)");
				query.setParameter("wrapper", wrapper);
				parent = hibernate.uniqueResult(query);
			}
			if (parent != null) {
				return findContainerForWrapper(parent);
			}
		}
		return null;
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
			discarded = true;
		}
		return discarded;
	}

}
