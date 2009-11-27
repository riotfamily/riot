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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.web.servlet.view.RedirectView;

public class PageView extends RedirectView {

	private String pageType;
	
	private boolean addRequestParameters;

	public PageView(String pageType) {
		this.pageType = pageType;
		setExposeModelAttributes(false);
	}

	public PageView addRequestParameters() {
		addRequestParameters = true;
		return this;
	}
	
	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		Site site = PageResolver.getSite(request);
		Page page = ContentPage.loadByTypeAndSite(pageType, site);
		String url = new PageFacade(page, request, response).getUrl();
		if (addRequestParameters) {
			url = ServletUtils.addRequestParameters(url, request);
		}
		setUrl(url);
		super.render(model, request, response);
	}
}
