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
package org.riotfamily.pages.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.component.PageComponentListLocator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Abstract base class for {@link PageDao} implementations.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public abstract class AbstractPageDao implements PageDao, InitializingBean {

	private static final String DEFAULT_SITE_NAME = "default";

	private static final Log log = LogFactory.getLog(AbstractPageDao.class);

	private ComponentDao componentDao;

	private List locales;

	public AbstractPageDao() {
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public void setLocales(List locales) {
		this.locales = locales;
	}

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(componentDao, "A ComponentDao must be set.");
		Assert.notEmpty(locales, "At least one Locale must be configured.");
		initDao();
	}

	protected void initDao() {
	}

	protected abstract Object loadObject(Class clazz, Serializable id);

	protected abstract void saveObject(Object object);

	protected abstract void updateObject(Object object);

	protected abstract void deleteObject(Object object);


	public Page loadPage(Long id) {
		return (Page) loadObject(Page.class, id);
	}

	public Site loadSite(Long id) {
		return (Site) loadObject(Site.class, id);
	}

	public void savePage(Page parent, Page page) {
		page.setLocale(parent.getLocale());
		savePage(parent.getNode(), page);
	}

	public void savePage(Site site, Page page) {
		PageNode rootNode = findRootNode(site);
		savePage(rootNode, page);
	}

	private void savePage(PageNode parentNode, Page page) {
		PageNode node = page.getNode();
		if (node == null) {
			node = new PageNode();
		}
		node.addPage(page);
		parentNode.addChildNode(node);
		page.setCreationDate(new Date());
		updateNode(parentNode);
		deleteAlias(new PageLocation(page));
		log.debug("Page saved: " + page);
	}

	public Page addTranslation(Page page, Locale locale) {
		log.info("Adding translation " + page + " --> " + locale);
		Page translation = new Page();
		translation.setLocale(locale);
		translation.setPathComponent(page.getPathComponent());
		page.getNode().addPage(translation);
		updateNode(page.getNode());

		componentDao.copyComponentLists(PageComponentListLocator.TYPE_PAGE,
				page.getId().toString(), translation.getId().toString());

		return translation;
	}

	public void updatePage(Page page) {
		updateObject(page);
		String oldPath = page.getPath();
		String newPath = page.buildPath();
		if (!ObjectUtils.nullSafeEquals(oldPath, newPath)) {
			log.info("Path modified: " + page);
			PageLocation oldLocation = new PageLocation(page);
			page.setPath(newPath);
			createAlias(page, oldLocation);
			updatePaths(page.getChildPages());
		}
	}

	public void updateNode(PageNode node) {
			updateObject(node);
	}

	public void moveNode(PageNode node, PageNode newParent) {
		PageNode parentNode = node.getParent();
		parentNode.getChildNodes().remove(node);
		newParent.addChildNode(node);
		//updateNode(newParent);
		//updateNode(parentNode);
		updatePaths(node.getPages());
	}

	private void updatePaths(Collection pages) {
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			PageLocation oldLocation = new PageLocation(page);
			page.setPath(page.buildPath());
			createAlias(page, oldLocation);
			updateObject(page);
			updatePaths(page.getChildPages());
		}
	}

	protected abstract void clearAliases(Page page);

	protected void deleteAlias(PageLocation location) {
		PageAlias alias = findPageAlias(location);
		if (alias != null) {
			log.info("Deleting " + alias);
			deleteObject(alias);
		}
	}

	protected void createAlias(Page page, PageLocation location) {
		if (page != null) {
			deleteAlias(new PageLocation(page));
		}
		PageAlias alias = new PageAlias(page, location);
		log.info("Creating " + alias);
		saveObject(alias);
	}


	public void deletePage(Page page) {
		log.info("Deleting page " + page);
		Collection childPages = page.getChildPages();
		if (childPages != null) {
			Iterator it = childPages.iterator();
			while (it.hasNext()) {
				Page child = (Page) it.next();
				deletePage(child);
			}
		}
		componentDao.deleteComponentLists(PageComponentListLocator.TYPE_PAGE,
				page.getId().toString());

		clearAliases(page);

		PageNode node = page.getNode();
		node.removePage(page);
		if (node.hasPages()) {
			updateNode(node);
		}
		else {
			log.debug("Node has no more pages - deleting it ...");
			PageNode parentNode = node.getParent();
			if (parentNode != null) {
				parentNode.getChildNodes().remove(node);
				updateNode(parentNode);
			}
			deleteObject(node);
		}
	}

	public Site getDefaultSite() {
		List sites = listSites();
		if (sites.size() > 0) {
			return (Site) sites.get(0);
		}
		Site site = new Site();
		site.setName(DEFAULT_SITE_NAME);
		site.setEnabled(true);
		site.setLocales(new ArrayList(locales));
		saveSite(site);
		return site;
	}

	public void saveSite(Site site) {
		saveObject(site);
		PageNode rootNode = new PageNode();
		rootNode.setSite(site);
		saveObject(rootNode);
	}

	public void updateSite(Site site) {
		updateObject(site);
	}

	public void deleteSite(Site site) {
		PageNode rootNode = findRootNode(site);
		Iterator it = rootNode.getPages().iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			deletePage(page);
		}
		deleteObject(site);
	}

	public List getLocales() {
		return locales;
	}

}
