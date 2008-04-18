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
package org.riotfamily.pages.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.beans.MapWrapper;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.riot.security.AccessController;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class Page {

	public static final String TITLE_PROPERTY = "title";
	
	private Long id;

	private PageNode node;

	private Site site;

	private String pathComponent;

	private boolean hidden;

	private boolean folder;

	private String path;
	
	private boolean wildcardInPath;

	private boolean published;

	private Date creationDate;

	private PageProperties contentContainer;
	
	public Page() {
	}

	public Page(String pathComponent, Site site) {
		this.pathComponent = pathComponent;
		this.site = site;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PageNode getNode() {
		return this.node;
	}

	public void setNode(PageNode node) {
		this.node = node;
	}

	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		Assert.state(this.site == null, 
				"The page is already associated with a site");
		
		this.site = site;
	}

	public Locale getLocale() {
		return site.getLocale();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getPathComponent() {
		return pathComponent;
	}

	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}

	public boolean isHidden() {
		return this.hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	/**
	 * Returns whether the page only acts as container for other pages and
	 * has no own content.
	 */
	public boolean isFolder() {
		return this.folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public String getPath() {
		if (path == null) {
			path = buildPath();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	private String getFullPath() {
		if (site.getPathPrefix() != null) {
			return site.getPathPrefix() + getPath();
		}
		return getPath();
	}

	public String getUrl(PathCompleter pathCompleter) {
		return getUrl(pathCompleter, null);
	}
	
	public String getUrl(PathCompleter pathCompleter, Object attributes) {
		String pagePath = pathCompleter.addServletMapping(getFullPath());
		if (isWildcardInPath()) {
			pagePath = fillInWildcards(pagePath, attributes);
		}
		return pagePath;
	}	
	
	public String getAbsoluteUrl(PathCompleter pathCompleter, boolean secure,
			String defaultHost, String contextPath, Object attributes) {
		
		String pagePath =  pathCompleter.addServletMapping(getPath());
		if (isWildcardInPath()) {
			pagePath = fillInWildcards(pagePath, attributes);
		}
		return site.makeAbsolute(secure, defaultHost, contextPath, pagePath);
	}
	
	private String fillInWildcards(String pattern, Object attributes) {
		if (attributes == null) {
			return pattern;
		}
		AttributePattern p = new AttributePattern(pattern);
		if (attributes instanceof Map) {
			Map map = (Map) attributes;
			if (p.canFillIn(map.keySet(), 0)) {
				return p.fillInAttributes(new MapWrapper(map));
			}
			return null;
		}
		if (ClassUtils.isAssignable(String.class, attributes.getClass()) ||
				ClassUtils.isPrimitiveOrWrapper(attributes.getClass())) {
			
			return p.fillInAttribute(attributes);
		}
		return p.fillInAttributes(new BeanWrapperImpl(attributes));
	}
	
	public boolean isWildcard() {
		return pathComponent.indexOf("@{") != -1;
	}
	
	public boolean isWildcardInPath() {
		return this.wildcardInPath;
	}

	public void setWildcardInPath(boolean wildcardInPath) {
		this.wildcardInPath = wildcardInPath;
	}

	public String buildPath() {
		StringBuffer path = new StringBuffer();
		wildcardInPath = false;
		Page page = this;
		while (page != null) {
			path.insert(0, page.getPathComponent());
			path.insert(0, '/');
			wildcardInPath |= page.isWildcard();
			page = page.getParentPage();
		}
		return path.toString();
	}

	public Page getParentPage() {
		PageNode parentNode = node.getParent();
		if (parentNode == null) {
			return null;
		}
		return parentNode.getPage(site);
	}

	public List getChildPages() {
		return node.getChildPages(site);
	}

	public Collection getChildPagesWithFallback() {
		return node.getChildPagesWithFallback(site);
	}

	public Collection getAncestors() {
		LinkedList pages = new LinkedList();
		Page page = this;
		while (page != null) {
			pages.addFirst(page);
			page = page.getParentPage();
		}
		return pages;
	}

	public void addChildPage(Page child) {
		child.setSite(site);
		node.addChildNode(new PageNode(child));
	}

	public String getHandlerName() {
		return node.getHandlerName();
	}

	public ContentContainer getContentContainer() {
		if (contentContainer == null) {
			contentContainer = new PageProperties();
			Content version = new Content();
			contentContainer.setLiveVersion(version);
		}
		return contentContainer;
	}

	public void setContentContainer(PageProperties contentContainer) {
		this.contentContainer = contentContainer;
	}

	public Content getContent(boolean preview) {
		return preview ? getContentContainer().getLatestVersion()
				: getContentContainer().getLiveVersion();
	}
	
	public Page getMasterPage() {
		Page masterPage = null;
		Site masterSite = site.getMasterSite();
		while (masterPage == null && masterSite != null) {
			masterPage = node.getPage(masterSite);
			masterSite = masterSite.getMasterSite();
		}
		return masterPage;
	}
	
	public Map getPropertiesMap(boolean preview) {
		Map mergedProperties;
		Page masterPage = getMasterPage();
		if (masterPage != null) {
			mergedProperties = masterPage.getPropertiesMap(preview);
		}
		else {
			mergedProperties = new HashMap();
		}
		mergedProperties.putAll(getLocalPropertiesMap(preview));
		return mergedProperties;
	}
	
	public Map getLocalPropertiesMap(boolean preview) {
		Content content = getContent(preview);
		return content != null 
				? content.unwrapValues() 
				: Collections.EMPTY_MAP;
	}

	public Object getProperty(String key, boolean preview) {
		Content version = getContent(preview);
		Object value = null;
		ValueWrapper wrapper = version.getWrapper(key);
		if (wrapper != null) {
			value = wrapper.unwrap();
		}
		if (value == null && getMasterPage() != null) {
			value = getMasterPage().getProperty(key, preview);
		}
		return value;
	}

	public String getTitle() {
		return getTitle(true);
	}
	
	public String getTitle(boolean preview) {
		Object title = getProperty(TITLE_PROPERTY, preview);
		if (title != null) {
			return title.toString();
		}
		return FormatUtils.xmlToTitleCase(pathComponent);
	}
	
	public boolean isDirty() {
		return getContentContainer().isDirty();
	}

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public boolean isRequestable() {
		return (published && site.isEnabled())
			|| AccessController.isAuthenticatedUser();
	}

	public boolean isVisible(boolean preview) {
		return !isHidden() 
				&& !node.isHidden() 
				&& !isWildcard() 
				&& (published || preview)
				&& (!folder || hasVisibleChildPage(preview));
	}
	
	private boolean hasVisibleChildPage(boolean preview) {
		Iterator it = getChildPages().iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (page.isVisible(preview)) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return site + ":" + path;
	}

	public boolean equals(Object o) {
		if (o instanceof Page) {
			Page page = (Page) o;
			if (id == null) {
				return this == o;
			}
			return id.equals(page.getId());
		}
		return false;
	}

	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

}
