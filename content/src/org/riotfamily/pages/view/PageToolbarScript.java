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

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.view.DynamicToolbarScript;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageToolbarScript implements DynamicToolbarScript {

	public String generateJavaScript(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		Site site = PageResolver.getSite(request);
		if (site != null) {
			sb.append("riotComponentFormParams.siteId = ").
				append(site.getId()).append(";\n");
		}
		Page page = PageResolver.getPage(request);
		if (page instanceof ContentPage) {
			sb.append("riotComponentFormParams.pageId = ").
				append(((ContentPage) page).getId()).append(";\n");
		}
		return sb.toString();
	}
	
}
