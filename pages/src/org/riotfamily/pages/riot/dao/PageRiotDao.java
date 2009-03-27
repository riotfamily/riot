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
import java.util.Collections;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.CutAndPasteEnabledDao;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.ParentChildDao;
import org.riotfamily.core.dao.SwappableItemDao;
import org.riotfamily.core.dao.TreeDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.model.SiteMapItem;
import org.springframework.dao.DataAccessException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageRiotDao implements ParentChildDao, TreeDao, 
		SwappableItemDao, CutAndPasteEnabledDao {

	public PageRiotDao() {
	}

	public Object getParent(Object entity) {
		Page page = (Page) entity;
		return page.getParent();
	}

	public void delete(Object entity, Object parent) throws DataAccessException {
		Page page = (Page) entity;
		page.delete();
	}

	public Class<?> getEntityClass() {
		return Page.class;
	}

	public int getListSize(Object parent, ListParams params)
			throws DataAccessException {

		return -1;
	}

	public String getObjectId(Object entity) {
		Page page = (Page) entity;
		return page.getId().toString();
	}

	public Collection<Page> list(Object parent, ListParams params)
			throws DataAccessException {

		SiteMapItem parentItem = (SiteMapItem) parent;
		if (parentItem == null) {
			parentItem = Site.loadDefaultSite();
		}
		return parentItem.getChildPages(); //FIXME getChildPagesWithFallback();
		
	}

	public boolean isNode(Object object) {
		return object instanceof Page;
	}
	
	public boolean hasChildren(Object parent, Object root, ListParams params) {
		Page page = (Page) parent;
		if (root != null) {
			Site rootSite;
			if (root instanceof Site) {
				rootSite = (Site) root;
			}
			else {
				Page rootPage = (Page) root;
				rootSite = rootPage.getSite();
			}
			if (!page.getSite().equals(rootSite)) {
				return false;
			}
		}
		return page.getChildPagesWithFallback().size() > 0;
	}
	
	public Object load(String id) throws DataAccessException {
		return Page.load(Long.valueOf(id));
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		Page page = (Page) entity;
		SiteMapItem parentItem = (SiteMapItem) parent;
		if (parentItem == null) {
			parentItem = Site.loadDefaultSite();
		}
		parentItem.addPage(page);
		page.save();
	}

	public Object merge(Object entity) throws DataAccessException {
		Page page = (Page) entity;
		return page.merge();
	}
	
	public void update(Object entity) throws DataAccessException {
		Page page = (Page) entity;
		page.update();
	}

	public void swapEntity(Object entity, Object parent, ListParams params,
			int swapWith) {

		Page page = (Page) entity;
	
		List<Page> pages = new ArrayList<Page>(list(parent, params));
		int i = pages.indexOf(page);
		
		Page otherPage = pages.get(i + swapWith);
		List<Page> siblings = Generics.newArrayList(page.getSiblings());

		int pos = siblings.indexOf(page);
		int otherPos = siblings.indexOf(otherPage);
		
		Collections.swap(siblings, pos, otherPos);
		//TODO PageCacheUtils.invalidateNode(cacheService, parent);
	}

	public void addChild(Object entity, Object parent) {
		Page page = (Page) entity;
		SiteMapItem parentItem = (SiteMapItem) parent;
		if (parentItem == null) {
			parentItem = Site.loadDefaultSite();
		}
		page.getParentPage().removePage(page);
		parentItem.addPage(page);
		
		updatePaths(page);
		//FIXME Invalidate cache items
		//PageCacheUtils.invalidateNode(cacheService, node);
		//PageCacheUtils.invalidateNode(cacheService, parentNode);
		//PageCacheUtils.invalidateNode(cacheService, newParent);
		
	}
	
	private void updatePaths(Page page) {
		String oldPath = page.getPath();
		page.setPath(page.buildPath());
		//FIXME createAlias(page, oldPath);
		for (Page child : page.getChildPages()) {
			updatePaths(child);
		}
	}

	public void removeChild(Object entity, Object parent) {
	}
	
}
