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

import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.SwappableItemDao;
import org.riotfamily.riot.dao.TreeHintDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageRiotDao implements ParentChildDao, TreeHintDao, 
		SwappableItemDao, CutAndPasteEnabledDao, InitializingBean {

	private PageDao pageDao;

	public PageRiotDao() {
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(pageDao, "A PageDao must be set.");
	}

	public Object getParent(Object entity) {
		Page page = (Page) entity;
		Page parentPage = page.getParentPage();
		if (parentPage != null) {
			return parentPage;
		}
		return page.getSite();
	}

	public void delete(Object entity, Object parent) throws DataAccessException {
		Page page = (Page) entity;
		pageDao.deletePage(page);
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

		if (parent instanceof Page) {
			Page parentPage = (Page) parent;
			return parentPage.getChildPagesWithFallback();
		}
		else {
			Site site;
			if (parent instanceof Site) {
				site = (Site) parent;
			}
			else {
				site = pageDao.getDefaultSite();
			}
			return pageDao.getRootNode().getChildPagesWithFallback(site);
		}
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
		return pageDao.loadPage(new Long(id));
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		Page page = (Page) entity;
		if (parent instanceof Page) {
			Page parentPage = (Page) parent;
			pageDao.savePage(parentPage, page);
		}
		else {
			Site site;
			if (parent instanceof Site) {
				site = (Site) parent;
			}
			else {
				site = pageDao.getDefaultSite();
			}
			page.setSite(site);
			pageDao.savePage(site, page);
		}
	}

	public Object merge(Object entity) throws DataAccessException {
		Page page = (Page) entity;
		return pageDao.mergePage(page);
	}
	
	public void update(Object entity) throws DataAccessException {
		Page page = (Page) entity;
		pageDao.updatePage(page);
	}

	public void swapEntity(Object entity, Object parent, ListParams params,
			int swapWith) {

		Page page = (Page) entity;
		PageNode node = page.getNode();
		
		List<Page> pages = new ArrayList<Page>(list(parent, params));
		int i = pages.indexOf(page);
		PageNode otherNode = pages.get(i + swapWith).getNode();
		
		PageNode parentNode = node.getParent();
		List<PageNode> nodes = parentNode.getChildNodes();

		int pos = nodes.indexOf(node);
		int otherPos = nodes.indexOf(otherNode);
		
		Collections.swap(nodes, pos, otherPos);
		pageDao.updateNode(parentNode);
	}

	public void addChild(Object entity, Object parent) {
		Page page = (Page) entity;
		PageNode node = page.getNode();
		PageNode parentNode = null;
		if (parent instanceof Page) {
			Page parentPage = (Page) parent;
			parentNode = parentPage.getNode();
		}
		else {
			parentNode = pageDao.getRootNode();
		}
		pageDao.validate(parentNode, page);
		pageDao.moveNode(node, parentNode);
	}

	public void removeChild(Object entity, Object parent) {
	}
	
}
