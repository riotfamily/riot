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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.pages.component.PageComponentListLocator;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;
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

	private static final Log log = LogFactory.getLog(AbstractPageDao.class);

	private ComponentDao componentDao;
	
	private Map childHandlerNames;

	private Map autoCreatePages;
	
	public AbstractPageDao() {
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public void setChildHandlerNames(Map childHandlerNames) {
		this.childHandlerNames = childHandlerNames;
	}
	
	public String getChildHandlerName(String parentHandlerName) {
		if (childHandlerNames != null) {
			return (String) childHandlerNames.get(parentHandlerName);
		}
		return null;
	}
	
	public void setAutoCreatePages(Map autoCreatePages) {
		this.autoCreatePages = autoCreatePages;
	}
	
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(componentDao, "A ComponentDao must be set.");
		initDao();
	}

	protected void initDao() {
	}

	protected abstract Object loadObject(Class clazz, Serializable id);

	protected abstract void saveObject(Object object);

	protected abstract void updateObject(Object object);

	protected abstract void deleteObject(Object object);

	protected abstract void flush();
	

	public Page loadPage(Long id) {
		return (Page) loadObject(Page.class, id);
	}

	public Site loadSite(Long id) {
		return (Site) loadObject(Site.class, id);
	}

	public Site findSite(String hostName, String path) {
		Iterator it = listSites().iterator();
		while (it.hasNext()) {
			Site site = (Site) it.next();
			if (site.matches(hostName, path)) {
				return site;
			}
		}
		return null;
	}
	
	public void saveNode(PageNode node) {
		if (node.getHandlerName() == null && node.getParent() != null) {
			node.setHandlerName(getChildHandlerName(node.getParent().getHandlerName()));
		}
		
		String handlerName = node.getHandlerName();
		if (autoCreatePages != null && handlerName != null) {
			PageDefinition child = (PageDefinition) autoCreatePages.get(handlerName);
			if (child != null) {
				ArrayList sites = new ArrayList();
				Iterator it = node.getPages().iterator();
				while (it.hasNext()) {
					Page page = (Page) it.next();
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
		node.addPage(page); // It could be that the node does not yet contain
							// the page itself, for example when edited nested...

		if (!PageValidationUtils.isValidChild(parentNode, page)) {
			log.warn("Page not saved because not valid: " + page);
			throw new DuplicatePathComponentException("Page '{0}' did not validate", page.toString());
		}

		parentNode.addChildNode(node);
		page.setCreationDate(new Date());
		
		saveNode(node);
		deleteAlias(page);
		
		log.debug("Page saved: " + page);
	}

	public Page addTranslation(Page page, Site site) {
		log.info("Adding translation " + page + " --> " + site);
		Page translation = new Page();
		translation.setSite(site);
		translation.setCreationDate(new Date());
		translation.setPathComponent(page.getPathComponent());
		translation.setFolder(page.isFolder());
		translation.setHidden(page.isHidden());
		PageNode node = page.getNode();
		if (node.isSystemNode()) {
			translation.setPublished(page.isPublished());
		}
		
		translation.setVersionContainer(page.getVersionContainer().createCopy());
		
		node.addPage(translation);
		updateNode(node);
		deleteAlias(translation);
		saveObject(translation);
		
		componentDao.copyComponentLists(PageComponentListLocator.TYPE_PAGE,
				page.getId().toString(), translation.getId().toString());

		return translation;
	}

	public void updatePage(Page page) {
		updateNode(page.getNode());
		updateObject(page);

		if (!PageValidationUtils.isValidChild(page.getNode().getParent(), page)) {
			log.warn("Page not saved because not valid: " + page);
			throw new DuplicatePathComponentException("Page '{0}' did not validate", page.toString());
		}

		String oldPath = page.getPath();
		String newPath = page.buildPath();
		if (!ObjectUtils.nullSafeEquals(oldPath, newPath)) {
			log.info("Path modified: " + page);
			page.setPath(newPath);
			createAlias(page, oldPath);
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
		updatePaths(node.getPages());
	}

	private void updatePaths(Collection pages) {
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			String oldPath = page.getPath();
			page.setPath(page.buildPath());
			createAlias(page, oldPath);
			updateObject(page);
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
		deleteObject(page);
		
		//Deleted by cascade now ...
		//VersionContainer vc = page.getVersionContainer();
		//page.setVersionContainer(null);
		//componentService.deleteVersionContainer(vc);

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

	public void saveSite(Site site) {
		saveObject(site);
		Site masterSite = site.getMasterSite();
		if (masterSite == null) {
			masterSite = getDefaultSite();
		}
		translateSystemPages(getRootNode(), masterSite, site);
	}
	
	private void translateSystemPages(PageNode node, Site masterSite, Site site) {
		List childNodes = node.getChildNodes();
		if (childNodes != null) {
			Iterator it = childNodes.iterator();
			while (it.hasNext()) {
				PageNode childNode = (PageNode) it.next();
				if (childNode.isSystemNode()) {
					addTranslation(childNode.getPage(masterSite), site);
					translateSystemPages(childNode, masterSite, site);
				}
			}
		}
	}

	public void updateSite(Site site) {
		updateObject(site);
	}

	public void deleteSite(Site site) {
		Iterator it = getRootNode().getChildPages(site).iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			deletePage(page);
		}
		deleteAliases(site);
		deleteObject(site);
	}

}
