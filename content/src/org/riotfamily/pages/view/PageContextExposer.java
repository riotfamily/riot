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

import org.riotfamily.common.web.mvc.view.ModelPostProcessor;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PageContextExposer implements ModelPostProcessor {

	private PageResolver pageResolver;
	
	public PageContextExposer(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	public void postProcess(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Site site = pageResolver.getSite(request);
		if (site != null) {
			model.put("currentSite", site);
		}
		
		Page page = pageResolver.getPage(request);
		if (page != null) {
			model.put("currentPage", page);
			if (!model.containsKey("contentMap")) {
				ContentContainer container = page.getContentContainer();
				boolean preview = EditModeUtils.isPreview(request, container);
				ContentMap contentMap = container.getContent(preview);
				model.put("contentMap", contentMap);
			}
		}
	}
}
