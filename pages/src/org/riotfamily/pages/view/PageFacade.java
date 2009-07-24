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
package org.riotfamily.pages.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageProperties;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageFacade {
	
	public static final String TITLE_PROPERTY = "title";

	private Page page;

	private HttpServletRequest request;
	
	private boolean preview;
	
	private Map<String, Object> properties = null;

	public PageFacade(Page page, HttpServletRequest request) {
		this.page = page;
		this.request = request;
		this.preview = isPreview(page);
		TaggingContext.tag(page.getCacheTag());
	}
		
	private boolean isPreview(Page page) {
		return EditModeUtils.isPreview(request, page.getPageProperties());
	}
	public Long getId() {
		return page.getId();
	}
	
	public Site getSite() {
		return page.getSite();
	}
	
	public Locale getLocale() {
		return page.getLocale();
	}
	
	public Date getCreationDate() {
		return page.getCreationDate();
	}
	
	public String getPathComponent() {
		return page.getPathComponent();
	}

	public boolean isHidden() {
		return page.isHidden();
	}
	
	public String getPath() {
		return page.getPath();
	}

	public String getUrl() {
		if (!page.getSite().hostNameMatches(request.getServerName())) {
			return getAbsoluteUrl();
		}
		return page.getUrl();
	}
	
	public String getAbsoluteUrl() {
		return page.getAbsoluteUrl(request.isSecure(), 
				ServletUtils.getServerNameAndPort(request),
				request.getContextPath());
	}

	public String getSecureUrl() {
		if (request.isSecure() && request.getServerName().equals(
				page.getSite().getHostName())) {
			
			return getUrl();
		}
		return page.getAbsoluteUrl(true, 
				ServletUtils.getServerNameAndPort(request),
				request.getContextPath());
	}
		
	public Page getMasterPage() {
		return page.getMasterPage();
	}

	public Page getParent() {
		return page.getParent();
	}

	public Collection<Page> getChildPages() {
		TaggingContext.tag(page.getCacheTag());
		return getVisiblePages(page.getChildPages());
	}

	public List<Page> getSiblings() {
		Page parent = page.getParent();
		if (parent == null) {
			return Collections.singletonList(page);
		}
		TaggingContext.tag(parent.getCacheTag());
		return getVisiblePages(parent.getChildPages());
	}
	
	public Page getPreviousSibling() {
		List<Page> siblings = getSiblings();
		int i = siblings.indexOf(page);
		if (i > 0) {
			return siblings.get(i - 1);
		}
		return null;
	}
	
	public Page getNextSibling() {
		List<Page> siblings = getSiblings();
		int i = siblings.indexOf(page);
		if (i < siblings.size() - 1) {
			return siblings.get(i + 1);
		}
		return null;
	}
	
	public Collection<Page> getAncestors() {
		return page.getAncestors();
	}

	public Object findPropertyInAncestors(String key) {
		Page p = page;
		while (p != null) {
			Map<String, Object> props = p.getPageProperties().unwrap(preview);
			Object value = props.get(key);
			if (value != null) {
				return value;
			}
			p = p.getParent();
		}
		return null;
	}
	
	public String getPageType() {
		return page.getPageType();
	}

	public Long getContentId() {
		Content content = getPageProperties().getContent(preview);
		return content != null ? content.getId() : null;
	}
		
	public PageProperties getPageProperties() {
		PageProperties pageProperties = page.getPageProperties(); 
		ComponentCacheUtils.addContainerTags(pageProperties, preview);
		Page master = page.getMasterPage();
		if (master != null) {
			ComponentCacheUtils.addContainerTags(master.getPageProperties(), preview);
		}
		return pageProperties;
	}

	public Map<String, Object> getProperties() {
		if (properties == null) {
			properties = getPageProperties().unwrap(preview);
		}
		return properties;
	}
	
	/**
	 * @see http://freemarker.org/docs/api/freemarker/ext/beans/BeanModel.html#get(java.lang.String)
	 */
	public Object get(String key) {
		return getProperties().get(key);
	}
	
	public Map<String, Object> getLocal() {
		return getPageProperties().unwrapLocal(preview);
	}

	public String getTitle() {
		Object title = getProperties().get(TITLE_PROPERTY);
		if (title != null) {
			return title.toString();
		}
		return FormatUtils.xmlToTitleCase(page.getPathComponent());
	}

	
	public boolean isPublished() {
		return page.isPublished();
	}

	public boolean isVisible() {
		return page.isVisible(preview);
	}

	private List<Page> getVisiblePages(Collection<Page> pages) {
		ArrayList<Page> result = new ArrayList<Page>();
		for (Page page : pages) {
			if (page.isVisible(isPreview(page))) {
				result.add(page);
			}
		}
		return result;
	}
	
	public String toString() {
		return page.toString();
	}

	public boolean equals(Object o) {
		if (o instanceof PageFacade) {
			PageFacade other = (PageFacade) o; 
			return page.equals(other.page) && preview == other.preview;
		}
		return false;
	}

	public int hashCode() {
		return page.hashCode() + (preview ? 1 : 0);
	}

}
