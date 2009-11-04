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
package org.riotfamily.core.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.core.security.AccessController;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Sets cache control headers to prevent client side caching if a Riot user 
 * is logged in. This is especially useful if the user modifies a page via
 * AJAX, leaves the page and hits the back button.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class NoCacheHeaderInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		if (!response.isCommitted() && AccessController.isAuthenticatedUser()) {
			ServletUtils.setNoCacheHeaders(response);
		}
		return true;
	}
}
