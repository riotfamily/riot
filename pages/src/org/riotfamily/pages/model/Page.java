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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.riotfamily.common.beans.MapWrapper;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.riot.security.AccessController;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_pages")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
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

	private PageProperties pageProperties;
	
	public Page() {
	}

	public Page(String pathComponent, Site site) {
		this.pathComponent = pathComponent;
		this.site = site;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	public PageNode getNode() {
		return this.node;
	}

	public void setNode(PageNode node) {
		this.node = node;
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		Assert.state(this.site == null || this.site.equals(site), 
				"The page is already associated with a site");
		
		this.site = site;
	}

	@Transient
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
	
	@Transient
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
	
	@SuppressWarnings("unchecked")
	private String fillInWildcards(String pattern, Object attributes) {
		if (attributes == null) {
			return pattern;
		}
		AttributePattern p = new AttributePattern(pattern);
		if (attributes instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) attributes;
			if (p.canFillIn(map, null, 0)) {
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
	
	@Transient
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

	@Transient
	public Page getParentPage() {
		PageNode parentNode = node.getParent();
		if (parentNode == null) {
			return null;
		}
		return parentNode.getPage(site);
	}

	@Transient
	public List<Page> getChildPages() {
		return node.getChildPages(site);
	}

	@Transient
	public Collection<Page> getChildPagesWithFallback() {
		return node.getChildPagesWithFallback(site);
	}

	@Transient
	public Collection<Page> getAncestors() {
		LinkedList<Page> pages = new LinkedList<Page>();
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

	@Transient
	public String getPageType() {
		return node.getPageType();
	}

	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	public PageProperties getPageProperties() {
		if (pageProperties == null) {
			pageProperties = new PageProperties(this);
		}
		return pageProperties;
	}

	public void setPageProperties(PageProperties pageProperties) {
		this.pageProperties = pageProperties;
	}
	
	@Transient
	public Page getMasterPage() {
		Page masterPage = null;
		Site masterSite = site.getMasterSite();
		while (masterPage == null && masterSite != null) {
			masterPage = node.getPage(masterSite);
			masterSite = masterSite.getMasterSite();
		}
		return masterPage;
	}
	
	@Transient
	public String getTitle() {
		return getTitle(true);
	}
	
	public String getTitle(boolean preview) {
		Object title = getPageProperties().unwrap(preview).get(TITLE_PROPERTY);
		if (title != null) {
			return title.toString();
		}
		return FormatUtils.xmlToTitleCase(pathComponent);
	}
	
	@Transient
	public boolean isDirty() {
		return getPageProperties().isDirty();
	}

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	@Transient
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
		for (Page page : getChildPages()) {
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
