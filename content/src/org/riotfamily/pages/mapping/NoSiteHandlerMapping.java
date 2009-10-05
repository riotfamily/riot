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

import org.riotfamily.pages.model.Site;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class NoSiteHandlerMapping extends AbstractHandlerMapping {

	private PageResolver pageResolver;
	
	private Object siteNotFoundHandler;

	public NoSiteHandlerMapping(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	public void setSiteNotFoundHandler(Object siteNotFoundHandler) {
		this.siteNotFoundHandler = siteNotFoundHandler;
	}
	
	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		
		Site site = pageResolver.getSite(request);
		if (site == null) {
			return siteNotFoundHandler;
		}
		return null;
	}
	
}
