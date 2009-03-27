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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.riotfamily.common.beans.MapWrapper;
import org.riotfamily.common.hibernate.ActiveRecordSupport;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.mapping.PathConverter;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_pages", uniqueConstraints = {@UniqueConstraint(columnNames={"site_id", "path"})})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
public class Page extends ActiveRecordSupport implements SiteMapItem {

	@Deprecated
	public static final String TITLE_PROPERTY = "title";
	
	private String pageType;
	
	private Page parentPage;
	
	private long position;
	
	private Set<Page> childPages;
	
	private Page masterPage;
	
	private Set<Page> translations;
	
	private Site site;

	private String pathComponent;

	private boolean systemPage;
	
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

	public Page(Page master, Site site) {
		this.masterPage = master;
		this.site = site;
		this.creationDate = new Date();
		this.pathComponent = master.getPathComponent();
		this.folder = master.isFolder();
		this.hidden = master.isHidden();
		if (master.isSystemPage()) {
			published = master.isPublished();
		}
	}
	
	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	@ManyToOne
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		Assert.state(this.site == null || this.site.equals(site), 
				"The page is already associated with a site");
		
		this.site = site;
	}

	@OneToMany
    @JoinColumn(name="parent_id")
    @OrderBy("position")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public Set<Page> getChildPages() {
		return childPages;
	}

	public void setChildPages(Set<Page> childPages) {
		this.childPages = childPages;
	}
	
	@Column(name="pos")
	public long getPosition() {
		if (position == 0) {
			position = System.currentTimeMillis();
		}
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id", insertable=false, updatable=false)
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public Page getParentPage() {
		return parentPage;
	}

	public void setParentPage(Page parentPage) {
		this.parentPage = parentPage;
	}
			
	@ManyToOne
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public Page getMasterPage() {
		return masterPage;
	}

	public void setMasterPage(Page masterPage) {
		this.masterPage = masterPage;
	}
		
	@OneToMany(mappedBy="masterPage")
	@Cascade({CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public Set<Page> getTranslations() {
		return translations;
	}

	public void setTranslations(Set<Page> translations) {
		this.translations = translations;
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

	public boolean isSystemPage() {
		return systemPage;
	}

	public void setSystemPage(boolean systemPage) {
		this.systemPage = systemPage;
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

	public String getUrl(PathConverter pathCompleter) {
		return getUrl(pathCompleter, null);
	}
	
	public String getUrl(PathConverter converter, Object attributes) {
		String pagePath = getFullPath();
		if (converter != null) {
			pagePath = converter.addSuffix(pagePath);
		}
		if (isWildcardInPath()) {
			pagePath = fillInWildcards(pagePath, attributes);
		}
		return pagePath;
	}	
	
	public String getAbsoluteUrl(PathConverter converter, boolean secure,
			String defaultHost, String contextPath, Object attributes) {
		
		String relativeUrl = getUrl(converter, attributes);
		return site.makeAbsolute(secure, defaultHost, contextPath, relativeUrl);
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
	public Collection<Page> getAncestors() {
		LinkedList<Page> pages = new LinkedList<Page>();
		Page page = this;
		while (page != null) {
			pages.addFirst(page);
			page = page.getParentPage();
		}
		return pages;
	}

		

	@ManyToOne
	@Cascade(CascadeType.ALL)
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
	
	@Transient
	public Set<Page> getSiblings() {
		return getParentPage().getChildPages();
	}
	
	
	@Transient
	public List<Page> getChildPagesWithFallback() {
		List<Page> pages = Generics.newArrayList();
		pages.addAll(getChildPages());
		pages.addAll(getTranslationCandidates());
		return pages;
	}
	
	@Transient
	public List<Page> getTranslationCandidates() {
		List<Page> candidates = Generics.newArrayList();
		Page master = getMasterPage();
		if (master != null) {
			for (Page page : master.getChildPages()) {
				boolean translated = false;
				for (Page child : getChildPages()) {
					if (page.equals(child.getMasterPage())) {
						translated = true;
						break;
					}
				}
				if (!translated) {
					candidates.add(page);
				}
			}			
		}
		return candidates;
	}

	
	// ----------------------------------------------------------------------
	// 
	// ----------------------------------------------------------------------
	
	public void addPage(Page child) {
		/*
		if (!PageValidationUtils.isValidChild(this, child)) {
			throw new DuplicatePathComponentException(
					"Page '{0}' did not validate", child.toString());
		}
		*/
		getChildPages().add(child);
		child.setSite(getSite());
		//child.setCreationDate(new Date()); // -> EntityListener
		//deleteAlias(page); // -> EntityListener
		//PageCacheUtils.invalidateNode(cacheService, parentNode); // -> EntityListener
	}
	
	public void removePage(Page child) {
		getChildPages().remove(child);
	}
	
	public void publish() {
		setPublished(true);
		//PageCacheUtils.invalidateNode(cacheService, this);
		//PageCacheUtils.invalidateNode(cacheService, getParent());
		//FIXME componentDao.publishContainer(getPageProperties());	
	}
	
	public void unpublish() {
		setPublished(false);
		//PageCacheUtils.invalidateNode(cacheService, this);
		//PageCacheUtils.invalidateNode(cacheService, getParent());
	}
	
	public void discardPageProperties() {
		//FIXME componentDao.discardContainer(getPageProperties());
	}
	
	public void refreshIfDetached() {
		Session session = getSession();
		if (!session.contains(this)) {
			session.refresh(this);
		}
	}
	
	public static Page load(Long id) {
		return load(Page.class, id);
	}
	
	
	public static Page loadBySiteAndPath(Site site, String path) {
		return load("from Page where site = ? and path = ?", site, path);
	}
	
	public static List<Page> findByTypeAndSite(String type, Site site) {
		return find("from Page where type = ? and site = ?", type, site);
	}
	
	public static List<Page> findRootPagesBySite(Site site) {
		return find("from Page where parentPage is null and site = ?", site);
	}
	
}
