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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.cache.CacheTagUtils;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.config.SitemapSchema;
import org.riotfamily.pages.config.VirtualPageType;
import org.riotfamily.pages.model.Page;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageFacade {
	
	private Page page;

	private HttpServletRequest request;
	
	private boolean preview;
	
	public PageFacade(Page page, HttpServletRequest request) {
		this.page = page;
		this.request = request;
		this.preview = isPreview(page);
		CacheTagUtils.tagIfSupported(page);
	}
	
	public Page getPage() {
		return page;
	}
		
	private boolean isPreview(Page page) {
		return EditModeUtils.isPreview(request, page.getContentContainer());
	}
	
	public boolean isRequestable() {
		return (page.isPublished() && page.getSite().isEnabled()) 
				|| AccessController.isAuthenticatedUser();
	}
	
	public String getRelativeUrl() {
		StringBuilder url = new StringBuilder();
		url.append(page.getPath());
		String suffix = SitemapSchema.getDefault().getDefaultSuffix(page.getPageType());
		if (suffix != null) {
			url.append(suffix);
		}
		return url.toString();
	}	
	
	public String getUrl() {
		if (!page.getSite().hostNameMatches(request.getServerName())) {
			return getAbsoluteUrl();
		}
		return getRelativeUrl();
	}
	
	public String getAbsoluteUrl() {
		return page.getSite().makeAbsolute(request.isSecure(), 
				ServletUtils.getServerNameAndPort(request),
				request.getContextPath(), getRelativeUrl());
	}

	public String getSecureUrl() {
		if (request.isSecure() && request.getServerName().equals(
				page.getSite().getHostName())) {
			
			return getUrl();
		}
		return page.getSite().makeAbsolute(true, ServletUtils.getServerNameAndPort(request), 
				request.getContextPath(), getRelativeUrl());
	}
	
	public Collection<Page> getAncestors() {
		LinkedList<Page> pages = Generics.newLinkedList();
		Page page = this.page;
		while (page != null) {
			pages.addFirst(page);
			page = page.getParent();
		}
		return pages;
	}
		
	public List<PageFacade> getChildren() {
		CacheTagUtils.tagIfSupported(page);
		VirtualPageType type = SitemapSchema.getDefault().getVirtualChildType(page);
		if (type != null) {
			Collection<Page> children = type.listChildren(page);
			CacheTagUtils.tagIfSupported(children);
			return createFacades(children);
		}
		return createFacades(page.getChildren());
	}

	public List<PageFacade> getSiblings() {
		Page parent = page.getParent();
		if (parent == null) {
			return Collections.singletonList(this);
		}
		return new PageFacade(parent, request).getChildren();
	}
	
	public PageFacade getPreviousSibling() {
		List<PageFacade> siblings = getSiblings();
		int i = siblings.indexOf(this);
		if (i > 0) {
			return siblings.get(i - 1);
		}
		return null;
	}
	
	public PageFacade getNextSibling() {
		List<PageFacade> siblings = getSiblings();
		int i = siblings.indexOf(this);
		if (i < siblings.size() - 1) {
			return siblings.get(i + 1);
		}
		return null;
	}
	
	public ContentContainer getContentContainer() {
		ContentContainer container = page.getContentContainer();
		CacheTagUtils.tag(container);
		/*
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

	private List<PageFacade> createFacades(Collection<? extends Page> pages) {
		ArrayList<PageFacade> result = Generics.newArrayList();
		for (Page page : pages) {
			if (page.isPublished() || isPreview(page)) {
				result.add(new PageFacade(page, request));
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return page.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PageFacade) {
			PageFacade other = (PageFacade) o; 
			return page.equals(other.page) && preview == other.preview;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return page.hashCode() + (preview ? 1 : 0);
	}

}
