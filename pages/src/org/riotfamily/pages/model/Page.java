package org.riotfamily.pages.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.IndexColumn;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.hibernate.Lifecycle;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.config.SitemapSchema;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_pages", uniqueConstraints = {@UniqueConstraint(columnNames={"site_id", "path"})})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
public class Page extends ActiveRecordBeanSupport implements Lifecycle {

	public static final String TITLE_PROPERTY = "title";
	
	private String pageType;
	
	private Page parent;
	
	private List<Page> childPages;
	
	private Page masterPage;
	
	private Set<Page> translations;
	
	private Site site;

	private String pathComponent;

	private String path;
	
	private boolean published;

	private Date creationDate;

	private PageProperties pageProperties;
	
	private CacheService cacheService;
	
	private SitemapSchema schema;
	
	public Page() {
	}

	public Page(String pathComponent, Site site) {
		this.pathComponent = pathComponent;
		this.site = site;
	}

	public Page(Page master) {
		this.masterPage = master;
		this.pageType = master.getPageType();
		this.pathComponent = master.getPathComponent();
	}
	
	@Required	
	@Transient
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	
	@Required	
	@Transient
	public void setSchema(SitemapSchema schema) {
		this.schema = schema;
	}
	
	@Transient
	public String getCacheTag() {
		return Page.class.getName() + "#" + getId();
	}
	
	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@OneToMany
	@JoinColumn(name="parent_id")
	@IndexColumn(name="pos")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public List<Page> getChildPages() {
		return childPages;
	}

	public void setChildPages(List<Page> childPages) {
		this.childPages = childPages;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
	@JoinColumn(name = "parent_id", updatable = false, insertable = false)
	public Page getParent() {
		return parent;
	}

	public void setParent(Page parent) {
		this.parent = parent;
	}
	
	@ManyToOne(cascade=CascadeType.MERGE)
	public Page getMasterPage() {
		return masterPage;
	}

	public void setMasterPage(Page masterPage) {
		this.masterPage = masterPage;
	}
		
	@OneToMany(mappedBy="masterPage", cascade={CascadeType.MERGE, CascadeType.PERSIST})
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
	
	@Transient
	public String getUrl() {
		StringBuilder url = new StringBuilder();
		if (site.getPathPrefix() != null) {
			url.append(site.getPathPrefix());
		}
		url.append(getPath());
		String suffix = schema.getDefaultSuffix(pageType);
		if (suffix != null) {
			url.append(suffix);
		}
		return url.toString();
	}	
	
	public String getAbsoluteUrl(boolean secure, String defaultHost, String contextPath) {
		return site.makeAbsolute(secure, defaultHost, contextPath, getUrl());
	}
	
	@Transient
	public Collection<Page> getAncestors() {
		LinkedList<Page> pages = new LinkedList<Page>();
		Page page = this;
		while (page != null) {
			pages.addFirst(page);
			page = page.getParent();
		}
		return pages;
	}

		

	@ManyToOne(cascade=CascadeType.ALL)
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
		Object title = getPageProperties().getContent(preview).get(TITLE_PROPERTY);
		if (title != null) {
			return title.toString();
		}
		if (!StringUtils.hasText(pathComponent)) {
			return "/";
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
		
	public String toString() {
		return site + ":" + path;
	}
	
	@Transient
	public List<Page> getSiblings() {
		if (parent == null) {
			return Collections.singletonList(this);
		}
		return parent.getChildPages();
	}
	
	@Transient
	public Collection<Page> getChildPagesWithFallback() {
		List<Page> pages = Generics.newArrayList();
		pages.addAll(getChildPages());
		pages.addAll(getTranslationCandidates());
		return pages;
	}
	
	@Transient
	public Collection<Page> getTranslationCandidates() {
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
	
	@Transient
	public Object getHandler() {
		return schema.getPageType(pageType).getHandler();
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
		child.setParent(this);
		child.setSite(getSite());
		if (childPages == null) {
			childPages = Generics.newArrayList();
		}
		childPages.add(child);
		//deleteAlias(page);
		if (parent != null) {
			parent.invalidateCacheItems();
		}
	}
	
	public void removePage(Page child) {
		childPages.remove(child);
		if (this.equals(child.getParent())) {
			child.setParent(null);
		}
	}
	
	public void publish() {
		setPublished(true);
		if (getPageProperties().isDirty()) {
			getPageProperties().publish();
		}
		invalidateCacheItems();
		if (getParent() != null) {
			getParent().invalidateCacheItems();
		}
	}
	
	public void unpublish() {
		setPublished(false);
		invalidateCacheItems();
		if (getParent() != null) {
			getParent().invalidateCacheItems();
		}
	}
		
	private void invalidateCacheItems() {
		if (cacheService != null) {
			cacheService.invalidateTaggedItems(getCacheTag());
		}
	}

	// ----------------------------------------------------------------------
	// Materialized path methods
	// ----------------------------------------------------------------------
	
	public String getPath() {
		if (path == null) {
			materializePath();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	private void updatePath() {
		if (materializePath()) {
			updateChildPaths();
		}
	}
	
	private void updateChildPaths() {
		if (childPages != null) {
			for (Page child : childPages) {
				child.updatePath();
			}
		}
	}
	
	private boolean materializePath() {
		String path = buildPath();
		if (!path.equals(this.path)) {
			this.path = path;
			return true;
		}
		return false;
	}
	
	private String buildPath() {
		if (parent != null) {
			return parent.getPath() + "/" + pathComponent;
		}
		return "";
	}
	
	// ----------------------------------------------------------------------
	// Implementation of the Lifecycle interface
	// ----------------------------------------------------------------------
	
	public void onSave() {
		setCreationDate(new Date());
		updatePath();
	}
	
	public void onUpdate(Object oldState) {
		materializePath();
		updateChildPaths();
	}
	
	public void onDelete() {
	}
	
	// ----------------------------------------------------------------------
	// Persistence methods
	// ----------------------------------------------------------------------
	
	public static Page load(Long id) {
		return load(Page.class, id);
	}

	public void refreshIfDetached() {
		Session session = getSession();
		if (!session.contains(this)) {
			session.refresh(this);
		}
	}
	
	public static Page loadBySiteAndPath(Site site, String path) {
		return load("from Page where site = ? and path = ?", site, path);
	}
	
	public static Page loadByTypeAndSite(String pageType, Site site) {
		return load("from Page where pageType = ? and site = ?", pageType, site);
	}

}
