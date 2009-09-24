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
package org.riotfamily.pages.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheContext;
import org.riotfamily.common.cache.AbstractCacheableController;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.view.PageFacade;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class SiteListController extends AbstractCacheableController {

	private String viewName;

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
					HttpServletResponse response) throws Exception {

		List<Site> sites = Site.findAll();
		if (sites.size() == 1) {
			Site site = sites.get(0);
			String url = new PageFacade(site.getRootPage(), request).getUrl();
			return new ModelAndView(new RedirectView(url, true));
		}
		if (!sites.isEmpty()) {
			CacheContext.tag(Site.class.getName());
			return new ModelAndView(viewName, "sites", sites);
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

}
