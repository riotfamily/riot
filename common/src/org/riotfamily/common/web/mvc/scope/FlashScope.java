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
package org.riotfamily.common.web.mvc.scope;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.mvc.view.FlashScopeView;
import org.riotfamily.common.web.support.ServletUtils;

/**
 * Class that temporarily stores data in the HTTP session to pass it from one 
 * request to another. Used by the {@link FlashScopeView} and its subclasses
 * to pass on data during redirects.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class FlashScope {

	private static final String SESSION_ATTRIBUTE = FlashScope.class.getName();

	private Map<String, Map<String, ?>> models = Generics.newHashMap();
		
	/**
	 * Stores a model map in the HTTP session for later retrieval by the next 
	 * request. 
	 * @param request The current request
	 * @param model The model to store
	 * @param url The URL of the next request  
	 */
	public static void store(HttpServletRequest request, Map<?, ?> model, String url) {
		FlashScope flashScope = getFlashScope(request, true);
		flashScope.models.put(url, (Map<String, ?>) model);
	}
	
	static void expose(HttpServletRequest request) {
		FlashScope flashScope = getFlashScope(request, false);
		if (flashScope != null) {
			String url = ServletUtils.getRequestUrlWithQueryString(request);
			Map<String, ?> model = flashScope.models.remove(url);
			if (model != null) {
				for (Map.Entry<String, ?> entry : model.entrySet()) {
					request.setAttribute(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private static FlashScope getFlashScope(HttpServletRequest request, boolean create) {
		FlashScope flashScope = null;
		HttpSession session = request.getSession(create);
		if (session != null) {
			flashScope = (FlashScope) session.getAttribute(SESSION_ATTRIBUTE);
			if (flashScope == null && create) {
				flashScope = new FlashScope();
				session.setAttribute(SESSION_ATTRIBUTE, flashScope);
			}
		}
		return flashScope;
	}

	
	
}
