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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.core.dao.ConstrainedDao;
import org.riotfamily.core.dao.CopyAndPasteEnabledDao;
import org.riotfamily.core.dao.CutAndPasteEnabledDao;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.ParentChildDao;
import org.riotfamily.core.dao.RootNodeTreeDao;
import org.riotfamily.core.dao.SwappableItemDao;
import org.riotfamily.pages.config.SitemapSchema;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageRiotDao implements ParentChildDao, RootNodeTreeDao, 
		ConstrainedDao, SwappableItemDao, CutAndPasteEnabledDao, 
		CopyAndPasteEnabledDao {

	private SitemapSchema sitemapSchema;
	
	public PageRiotDao(SitemapSchema sitemapSchema) {
		this.sitemapSchema = sitemapSchema;
	}

	public Object getParent(Object entity) {
		Page page = (Page) entity;
		return page.getSite();
	}

	public boolean canAdd(Object parent) {
		return parent instanceof Page 
				&& sitemapSchema.canHaveChildren((Page) parent);
	}
	
	public boolean canDelete(Object entity) {
		Page page = (Page) entity;
		return !sitemapSchema.isSystemPage(page);
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
	
	public Object getRootNode(Object parent) {
		if (parent == null) {
			parent = Site.loadDefaultSite();
		}
		return ((Site) parent).getRootPage();
	}
	
	public Collection<Page> list(Object parent, ListParams params)
			throws DataAccessException {

		Assert.isInstanceOf(Page.class, parent);
		return ((Page) parent).getChildPagesWithFallback();
	}
	
	public Object getParentNode(Object node) {
		Page page = (Page) node;
		return page.getParent();
	}

	public boolean hasChildren(Object node, Object parent, ListParams params) {
		Page page = (Page) node;
		if (parent instanceof Site) {
			if (!((Site) parent).equals(page.getSite())) {
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
		Assert.isInstanceOf(Page.class, parent);
		((Page) parent).addPage(page);
		page.save();
	}

	public Object update(Object entity) throws DataAccessException {
		Page page = (Page) entity;
		return page.merge();
	}

	public boolean canSwap(Object entity, Object parent, ListParams params,
			int swapWith) {
		
		Page page = (Page) entity;
		List<Page> siblings = page.getSiblings();
		int i = siblings.indexOf(page) + swapWith;
		return i >= 0 && i < siblings.size();
	}
	
	public void swapEntity(Object entity, Object parent, ListParams params,
			int swapWith) {

		Page page = (Page) entity;
		List<Page> pages = page.getSiblings();
		int i = pages.indexOf(page);
		Collections.swap(pages, i, i+ swapWith);
		//TODO PageCacheUtils.invalidateNode(cacheService, parent);
	}
	
	public boolean canCut(Object entity) {
		Page page = (Page) entity;
		return !sitemapSchema.isSystemPage(page);
	}
	
	public void cut(Object entity, Object parent) {
	}
	
	public boolean canPasteCut(Object entity, Object target) {
		return target instanceof Page 
				&& sitemapSchema.isValidChild((Page) target, (Page) entity);
	}

	public void pasteCut(Object entity, Object dest) {
		Page page = (Page) entity;
		page.getParent().removePage(page);
		((Page) dest).addPage(page);
		
		//FIXME Invalidate cache items
		//PageCacheUtils.invalidateNode(cacheService, node);
		//PageCacheUtils.invalidateNode(cacheService, parentNode);
		//PageCacheUtils.invalidateNode(cacheService, newParent);
	}
	
	public boolean canCopy(Object entity) {
		return false;
	}
	
	public boolean canPasteCopy(Object entity, Object dest) {
		return false;
	}
	
	public void pasteCopy(Object entity, Object dest) {
	}

}
