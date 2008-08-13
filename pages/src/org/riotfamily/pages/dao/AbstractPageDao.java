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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.pages.cache.PageCacheUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.setup.PageTypeHierarchy;
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

	private Log log = LogFactory.getLog(AbstractPageDao.class);

	private CacheService cacheService;
	
	private ComponentDao componentDao;
	
	private PageTypeHierarchy pageTypeHierarchy;

	private Map<String, PageDefinition> autoCreatePages;
	
	public AbstractPageDao() {
	}

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	
	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public void setPageTypeHierarchy(PageTypeHierarchy pageTypeHierarchy) {
		this.pageTypeHierarchy = pageTypeHierarchy;
	}
	
	protected PageTypeHierarchy getPageTypeHierarchy() {
		return this.pageTypeHierarchy;
	}

	public void setAutoCreatePages(Map<String, PageDefinition> autoCreatePages) {
		this.autoCreatePages = autoCreatePages;
	}
	
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(cacheService, "A Cache must be set.");
		Assert.notNull(componentDao, "A ComponentDao must be set.");
		initDao();
	}

	protected void initDao() {
	}

	protected abstract Object loadObject(Class<?> clazz, Serializable id);

	protected abstract void saveObject(Object object);

	protected abstract void deleteObject(Object object);
	
	public Page loadPage(Long id) {
		return (Page) loadObject(Page.class, id);
	}
	
	public PageNode loadPageNode(Long id) {
		return (PageNode) loadObject(PageNode.class, id);
	}

	public Site loadSite(Long id) {
		return (Site) loadObject(Site.class, id);
	}

	public Site findSite(String hostName, String path) {
		for (Site site : listSites()) {
			if (site.matches(hostName, path)) {
				return site;
			}
		}
		return null;
	}

	public Site findSiteWithProperty(String name, Object value) {
		for (Site site : listSites()) {
			Object currentValue = site.getProperty(name);
			if (value.equals(currentValue)) {
				return site;
			}
		}
		return null;
	}

	public void saveNode(PageNode node) {
		String pageType = pageTypeHierarchy.initPageType(node);
		if (autoCreatePages != null && pageType != null) {
			PageDefinition child = autoCreatePages.get(pageType);
			if (child != null) {
				ArrayList<Site> sites = new ArrayList<Site>();
				for (Page page : node.getPages()) { 
					sites.add(page.getSite());
				}
				child.createNode(node, sites, this);
			}
		}
		saveObject(node);
	}
	
	public void savePage(Page parent, Page page) {
		page.setSite(parent.getSite());
		savePage(parent.getNode(), page);
	}

	public void savePage(Site site, Page page) {
		savePage(getRootNode(), page);
	}

	private void savePage(PageNode parentNode, Page page) {
		PageNode node = page.getNode();
		if (node == null) {
			node = new PageNode();
		}
		
		if (!PageValidationUtils.isValidChild(parentNode, page)) {
			log.warn("Page not saved because not valid: " + page);
			throw new DuplicatePathComponentException("Page '{0}' did not validate", page.toString());
		}
		
		node.addPage(page); // It could be that the node does not yet contain
							// the page itself, for example when edited nested...

		parentNode.addChildNode(node);
		page.setCreationDate(new Date());
		
		saveNode(node);
		deleteAlias(page);
		PageCacheUtils.invalidateNode(cacheService, parentNode);
		log.debug("Page saved: " + page);
	}

	public Page addTranslation(Page page, Site site) {
		log.info("Adding translation " + page + " --> " + site);
		Page translation = new Page(page, site);
		PageNode node = page.getNode();
		node.addPage(translation);
		deleteAlias(translation);
		saveObject(translation);
		PageCacheUtils.invalidateNode(cacheService, node.getParent());
		return translation;
	}
	
	public Page addTranslation(PageNode node, Site site, String pathComponent) {
		log.info("Adding translation " + node + " --> " + site);
		Page translation = new Page();
		translation.setSite(site);
		translation.setCreationDate(new Date());
		translation.setPathComponent(pathComponent);
				
		node.addPage(translation);		
		saveObject(translation);
		PageCacheUtils.invalidateNode(cacheService, node.getParent());
		return translation;
	}

	public void updatePage(Page page) {
		PageNode node = page.getNode();
		
		if (!PageValidationUtils.isValidChild(node.getParent(), page)) {
			log.warn("Page not saved because not valid: " + page);
			throw new DuplicatePathComponentException("Page '{0}' did not validate", page.toString());
		}
		
		PageCacheUtils.invalidateNode(cacheService, node);
		PageCacheUtils.invalidateNode(cacheService, node.getParent());

		String oldPath = page.getPath();
		String newPath = page.buildPath();
		if (!ObjectUtils.nullSafeEquals(oldPath, newPath)) {
			log.info("Path modified: " + page);
			page.setPath(newPath);
			createAlias(page, oldPath);
			updatePaths(page.getChildPages());
		}
	}
	
	public void publishPage(Page page) {
		page.setPublished(true);
		PageNode node = page.getNode();
		PageCacheUtils.invalidateNode(cacheService, node);
		PageCacheUtils.invalidateNode(cacheService, node.getParent());
		publishPageProperties(page);
	}
	
	public void publishPageProperties(Page page) {
		componentDao.publishContainer(page.getPageProperties());
	}
	
	public void discardPageProperties(Page page) {
		componentDao.discardContainer(page.getPageProperties());
	}
	
	public void unpublishPage(Page page) {
		page.setPublished(false);
		PageNode node = page.getNode();
		PageCacheUtils.invalidateNode(cacheService, node);
		PageCacheUtils.invalidateNode(cacheService, node.getParent());
	}

	public void updateNode(PageNode node) {
		PageCacheUtils.invalidateNode(cacheService, node);
	}

	public void moveNode(PageNode node, PageNode newParent) {
		PageNode parentNode = node.getParent();
		parentNode.getChildNodes().remove(node);
		newParent.addChildNode(node);
		updatePaths(node.getPages());
		PageCacheUtils.invalidateNode(cacheService, node);
		PageCacheUtils.invalidateNode(cacheService, parentNode);
		PageCacheUtils.invalidateNode(cacheService, newParent);
	}

	private void updatePaths(Collection<Page> pages) {
		for (Page page : pages) {
			String oldPath = page.getPath();
			page.setPath(page.buildPath());
			createAlias(page, oldPath);
			updatePaths(page.getChildPages());
		}
	}

	protected abstract void clearAliases(Page page);
	
	protected abstract void deleteAliases(Site site);

	public void deleteAlias(Page page) {
		PageAlias alias = findPageAlias(page.getSite(), page.getPath());
		if (alias != null) {
			log.info("Deleting " + alias);
			deleteObject(alias);
		}
	}

	protected void createGoneAlias(Site site, String path) {
		PageAlias alias = new PageAlias(null, site, path);
		log.info("Creating " + alias);
		saveObject(alias);
	}
	
	protected void createAlias(Page page, String path) {
		deleteAlias(page);
		PageAlias alias = new PageAlias(page, page.getSite(), path);
		log.info("Creating " + alias);
		saveObject(alias);
	}


	public void deletePage(Page page) {
		log.info("Deleting page " + page);
		List<Page> childPages = page.getChildPages();
		if (childPages != null) {
			for (Page child : childPages) {
				deletePage(child);
			}
		}

		clearAliases(page);

		PageNode node = page.getNode();
		PageNode parentNode = node.getParent();
		
		node.removePage(page);
		
		PageCacheUtils.invalidateNode(cacheService, node);
		PageCacheUtils.invalidateNode(cacheService, parentNode);
		
		deleteObject(page);
		
		if (node.hasPages()) {
			updateNode(node);
		}
		else {
			log.debug("Node has no more pages - deleting it ...");
			if (parentNode != null) {
				parentNode.getChildNodes().remove(node);
				updateNode(parentNode);
			}
			deleteObject(node);
		}
	}
	
	public void saveSite(Site site) {
		saveObject(site);
		Site masterSite = site.getMasterSite();
		if (masterSite == null) {
			masterSite = getDefaultSite();
		}
		translateSystemPages(getRootNode(), masterSite, site);
	}
	
	public void updateSite(Site site) {
		PageCacheUtils.invalidateSite(cacheService, site);
	}
	
	private void translateSystemPages(PageNode node, Site masterSite, Site site) {
		List<PageNode> childNodes = node.getChildNodes();
		if (childNodes != null) {
			for (PageNode childNode : childNodes) {
				if (childNode.isSystemNode()) {
					Page page = childNode.getPage(masterSite);
					if (page != null) {
						addTranslation(page, site);
					}
					translateSystemPages(childNode, masterSite, site);
				}
			}
		}
	}

	public void deleteSite(Site site) {
		for (Page page : getRootNode().getChildPages(site)) {
			deletePage(page);
		}
		deleteAliases(site);
		if (site.getDerivedSites() != null) {
			for (Site derivedSite : site.getDerivedSites()) {
				derivedSite.setMasterSite(null);
			}
		}
		deleteObject(site);
	}

}
