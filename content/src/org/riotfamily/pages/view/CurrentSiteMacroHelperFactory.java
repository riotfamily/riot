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

import org.riotfamily.common.view.MacroHelperFactory;
import org.riotfamily.pages.mapping.PageResolver;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CurrentSiteMacroHelperFactory implements MacroHelperFactory {

	private PageResolver pageResolver;
	
	public CurrentSiteMacroHelperFactory(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {

		return pageResolver.getSite(request);
	}
}