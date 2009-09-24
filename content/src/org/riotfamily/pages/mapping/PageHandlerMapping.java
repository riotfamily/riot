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
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.controller.HttpErrorController;
import org.riotfamily.common.controller.RedirectController;
import org.riotfamily.pages.config.SitemapSchema;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.view.PageFacade;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.util.WebUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageHandlerMapping extends AbstractHandlerMapping {

	private PageResolver pageResolver;
	
	public PageHandlerMapping(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		if (WebUtils.isIncludeRequest(request)) {
			return null;
		}
		Page page = pageResolver.getPage(request);
		String path = pageResolver.getLookupPath(request);
		if (page == null) {
			Site site = pageResolver.getSite(request);
			if (site == null) {
				return null;
			}
			return getPageNotFoundHandler(site, path, request);
		}
		
		exposePathWithinMapping(path, request);
		return SitemapSchema.getDefault().getPageType(page.getPageType()).getHandler();
	}
		
	/**
	 * Checks if an alias is registered for the given site and path and returns 
	 * a RedirectController, or <code>null</code> in case no alias can be found.
	 * @param request 
	 */
	protected Object getPageNotFoundHandler(Site site, String path,
			HttpServletRequest request) {
		
		try {
			PageAlias alias = PageAlias.loadBySiteAndPath(site, path);
			if (alias != null) {
				ContentPage page = alias.getPage();
				if (page != null) {
					String url = new PageFacade(page, request).getUrl();
					return new RedirectController(url, true, false);
				}
				else {
					return new HttpErrorController(HttpServletResponse.SC_GONE);
				}
			}
			return null;
		}
		catch (HibernateSystemException e) {
			// No Hibernate session bound to thread
		}
		return null;
	}
	
	/**
	 * <strong>Copied from AbstractUrlHandlerMapping</strong>
	 */
	protected void exposePathWithinMapping(String pathWithinMapping, HttpServletRequest request) {
		request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping);
	}
	
}
