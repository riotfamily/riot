/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.pages.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.website.cache.CacheTagUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageFacade {
	
	public static final String TITLE_PROPERTY = "title";

	private Page page;

	private HttpServletRequest request;
	
	private boolean preview;
	
	public PageFacade(Page page, HttpServletRequest request) {
		this.page = page;
		this.request = request;
		this.preview = isPreview(page);
		CacheTagUtils.tag(page);
	}
		
	private boolean isPreview(Page page) {
		return EditModeUtils.isPreview(request, page.getContentContainer());
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
		CacheTagUtils.tag(page);
		return getPublishedPages(page.getChildPages());
	}

	public List<Page> getSiblings() {
		Page parent = page.getParent();
		if (parent == null) {
			return Collections.singletonList(page);
		}
		CacheTagUtils.tag(parent);
		return getPublishedPages(parent.getChildPages());
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
	
	public String getPageType() {
		return page.getPageType();
	}

	public Long getContentId() {
		return getContent().getId();
	}
		
	public ContentContainer getContentContainer() {
		ContentContainer container = page.getContentContainer();
		CacheTagUtils.tag(container);
		/*
		ComponentCacheUtils.addContainerTags(container, preview);
		Page master = page.getMasterPage();
		if (master != null) {
			ComponentCacheUtils.addContainerTags(master.getContentContainer(), preview);
		}
		*/
		return container;
	}

	public Content getContent() {
		Content content = getContentContainer().getContent(preview);
		CacheTagUtils.tag(content);
		return content;
	}
	
	/**
	 * @see http://freemarker.org/docs/api/freemarker/ext/beans/BeanModel.html#get(java.lang.String)
	 */
	public Object get(String key) {
		return getContent().get(key);
	}

	public String getTitle() {
		Object title = get(TITLE_PROPERTY);
		if (title != null) {
			return title.toString();
		}
		return FormatUtils.xmlToTitleCase(page.getPathComponent());
	}

	
	public boolean isPublished() {
		return page.isPublished();
	}

	private List<Page> getPublishedPages(Collection<Page> pages) {
		ArrayList<Page> result = new ArrayList<Page>();
		for (Page page : pages) {
			if (page.isPublished() || isPreview(page)) {
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
