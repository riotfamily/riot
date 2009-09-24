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
package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.config.SitemapSchema;
import org.riotfamily.pages.config.SystemPageType;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PageResolver {
	
	public static final String SITE_ATTRIBUTE = PageResolver.class.getName() + ".site";

	public static final String PAGE_ATTRIBUTE = PageResolver.class.getName() + ".page";

	private static final Object NOT_FOUND = new Object();
	
	private SitemapSchema sitemapSchema;
	
	public PageResolver() {
		this(SitemapSchema.getDefault());
	}
	
	public PageResolver(SitemapSchema sitemapSchema) {
		this.sitemapSchema = sitemapSchema;
	}

	/**
	 * Returns the first Site that matches the given request. The PathCompleter
	 * is used to strip the servlet mapping from the request URI.
	 * @return The first matching Site, or <code>null</code> if no match is found
	 */
	public Site getSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		if (site == null) {
			site = resolveSite(request);
			exposeSite((Site) site, request);
		}
		if (site == null || site == NOT_FOUND) {
			return null;
		}
		Site result = (Site) site;
		result.refreshIfDetached();
		return result; 
	}

	protected void exposeSite(Site site, HttpServletRequest request) {
		expose(site, request, SITE_ATTRIBUTE);
	}
	
	/**
	 * Returns the Page for the given request.
	 */
	public Page getPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		if (page == null) {
			page = resolvePage(request);
			exposePage((Page) page, request);
		}
		if (page == null || page == NOT_FOUND) {
			return null;
		}
		return (Page) page;
	}
	
	protected void exposePage(Page page, HttpServletRequest request) {
		expose(page, request, PAGE_ATTRIBUTE);
	}
	
	/**
	 * Returns the previously resolved Page for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getPage(HttpServletRequest)} has been invoked before. 
	 */
	public static Page getResolvedPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		return page != NOT_FOUND ? (Page) page : null;
	}
	
	/**
	 * Returns the previously resolved Site for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getSite(HttpServletRequest)} has been invoked before. 
	 */
	public static Site getResolvedSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		return site != NOT_FOUND ? (Site) site : null; 
	}

	private Site resolveSite(HttpServletRequest request) {
		String hostName = request.getServerName();
		return Site.loadByHostName(hostName);
	}

	private Page resolvePage(HttpServletRequest request) {
		Site site = getSite(request);
		if (site == null) {
			return null;
		}
		String path = ServletUtils.getPathWithinApplication(request);
		String lookupPath = getLookupPath(path);
		Page page = ContentPage.loadBySiteAndPath(site, lookupPath);
		if (page == null) {
			page = resolveVirtualChildPage(site, lookupPath);
		}
		if (page == null 
				|| !(page.isPublished() || AccessController.isAuthenticatedUser())
				|| !sitemapSchema.suffixMatches(page, path)) {
			
			return null;
		}
		return page;
	}
	
	private Page resolveVirtualChildPage(Site site, String lookupPath) {
		for (ContentPage parent : ContentPage.findByTypesAndSite(sitemapSchema.getVirtualParents(), site)) {
			if (lookupPath.startsWith(parent.getPath())) {
				SystemPageType parentType = (SystemPageType) sitemapSchema.getPageType(parent);
				String tail = lookupPath.substring(parent.getPath().length());
				return parentType.getVirtualChildType().resolve(parent, tail);
			}
		}
		return null;
	}

	public String getLookupPath(HttpServletRequest request) {
		return getLookupPath(ServletUtils.getPathWithinApplication(request));
	}
	
	public String getLookupPath(String path) {
		return FormatUtils.stripExtension(path);
	}
	
	private void expose(Object object, HttpServletRequest request,
			String attributeName) {
		
		if (object == null) {
			object = NOT_FOUND;
		}
		request.setAttribute(attributeName, object);
	}
	
	/**
	 * Resets all internally used attributes.
	 * @param request
	 */
	public static void resetAttributes(HttpServletRequest request) {
		request.removeAttribute(SITE_ATTRIBUTE);
		request.removeAttribute(PAGE_ATTRIBUTE);
	}
}