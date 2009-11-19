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

import org.riotfamily.pages.config.PageType;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.springframework.web.servlet.RequestToViewNameTranslator;

/**
 * RequestToViewNameTranslator that uses the pageType of the resolved Page
 * to construct a viewName.
 * 
 * @see PageResolver
 * @since 8.0
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PageRequestToViewNameTranslator 
		implements RequestToViewNameTranslator {

	private String prefix = "";
	
	private String suffix = "";
	
	private RequestToViewNameTranslator noPageTranslator;
	
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setNoPageTranslator(RequestToViewNameTranslator noPageTranslator) {
		this.noPageTranslator = noPageTranslator;
	}

	public String getViewName(HttpServletRequest request) throws Exception {
		Page page = PageResolver.getPage(request);
		if (page != null) {
			PageType pageType = page.getPageType();
			return prefix + pageType.getName() + suffix;
		}
		else if (noPageTranslator != null) {
			return noPageTranslator.getViewName(request);
		}
		return null;
	}

}
