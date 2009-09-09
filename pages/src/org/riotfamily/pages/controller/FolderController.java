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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.website.cache.CacheTagUtils;
import org.riotfamily.website.cache.CacheableController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class FolderController implements CacheableController {

	public String getCacheKey(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}

	public long getLastModified(HttpServletRequest request) {
		return System.currentTimeMillis();
	}

	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Page page = PageResolver.getResolvedPage(request);
		CacheTagUtils.tag(page);
		for (Page child : page.getChildPages()) {
			if (child.isRequestable()) {
				return new ModelAndView(new RedirectView(child.getUrl(), true));
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

	
}
