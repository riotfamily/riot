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
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.cache.tags.CacheTagUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.components.view.ContentContainerOwnerFacade;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.config.VirtualPageType;
import org.riotfamily.pages.model.Page;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageFacade extends ContentContainerOwnerFacade {
	
	private Page page;

	public PageFacade(Page page, HttpServletRequest request) {
		this(page, request, null);
	}
	
	public PageFacade(Page page, HttpServletRequest request,
			HttpServletResponse response) {
		
		super(page, request, response);
		this.page = page;
	}
	
	public Page getPage() {
		return page;
	}

	public boolean isRequestable() {
		return (page.getContentContainer().getLiveVersion() != null 
				&& page.getSite().isEnabled())
				|| AccessController.isAuthenticatedUser();
	}
	
	public String getRelativeUrl() {
		return getRelativeUrl(null, true);
	}

	public String getRelativeUrl(String suffix) {
		return getRelativeUrl(suffix, true);
	}
	
	public String getRelativeUrl(String suffix, boolean encode) {
		StringBuilder url = new StringBuilder();
		url.append(page.getPath());
		if (suffix == null) {
			suffix = page.getSite().getDefaultSuffix(page);
		}
		url.append(suffix);
		if (encode && response != null) {
			return ServletUtils.resolveAndEncodeUrl(url.toString(), request, response);
		}
		return ServletUtils.resolveUrl(url.toString(), request);
	}
	
	public String getUrl() {
		return getUrl(null);
	}

	public String getUrl(String suffix) {
		return getUrl(suffix, true);
	}
	
	public String getUrl(String suffix, boolean encode) {
		if (!page.getSite().hostNameMatches(request.getServerName())) {
			return getAbsoluteUrl(suffix);
		}
		return getRelativeUrl(suffix, encode);
	}
	
	public String getAbsoluteUrl() {
		return getAbsoluteUrl(null);
	}

	public String getAbsoluteUrl(String suffix) {
		return getAbsoluteUrl(suffix, true);
	}

	public String getAbsoluteUrl(String suffix, boolean encode) {
		return page.getSite().makeAbsolute(request.isSecure(), 
				ServletUtils.getServerNameAndPort(request),
				request.getContextPath(), getRelativeUrl(suffix, encode));
	}

	public String getSecureUrl() {
		return getSecureUrl(null);
	}

	public String getSecureUrl(String suffix) {
		return getSecureUrl(suffix, true);
	}

	public String getSecureUrl(String suffix, boolean encode) {
		if (request.isSecure() && request.getServerName().equals(
				page.getSite().getHostName())) {
			
			return getUrl(suffix);
		}
		return page.getSite().makeAbsolute(true, ServletUtils.getServerNameAndPort(request), 
				request.getContextPath(), getRelativeUrl(suffix, encode));
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
		
	public List<Page> getChildren() {
		ArrayList<Page> result = Generics.newArrayList();
		CacheTagUtils.tagIfSupported(page);
		VirtualPageType type = page.getSite().getSchema().getVirtualChildType(page);
		if (type != null) {
			Collection<Page> children = type.listChildren(page);
			CacheTagUtils.tagIfSupported(children);
			result.addAll(filterPages(children)) ;
		}
		result.addAll(filterPages(page.getChildren()));
		return result;
	}

	public List<Page> getSiblings() {
		Page parent = page.getParent();
		if (parent == null) {
			return Collections.singletonList(page);
		}
		return new PageFacade(parent, request, response).getChildren();
	}
	
	public Page getPreviousSibling() {
		List<Page> siblings = getSiblings();
		int i = siblings.indexOf(this);
		if (i > 0) {
			return siblings.get(i - 1);
		}
		return null;
	}
	
	public Page getNextSibling() {
		List<Page> siblings = getSiblings();
		int i = siblings.indexOf(this);
		if (i < siblings.size() - 1) {
			return siblings.get(i + 1);
		}
		return null;
	}
	
	protected List<Page> filterPages(Collection<? extends Page> pages) {
		ArrayList<Page> result = Generics.newArrayList();
		for (Page page : pages) {
			if (page.getContentContainer().getLiveVersion() != null || isPreview(page)) {
				result.add(page);
			}
		}
		return result;
	}
	
}
