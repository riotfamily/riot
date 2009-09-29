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
package org.riotfamily.core.security.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mvc.interceptor.OncePerRequestInterceptor;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.security.auth.RiotUser;

/**
 * HandlerInterceptor that sends a redirect to the login URL in case the
 * user is not logged in.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class LoginInterceptor extends OncePerRequestInterceptor {
	
	private static final String INTERCEPTED_URL_ATTR = LoginInterceptor.class 
			+ ".interceptedUrl";

	private String loginHandlerName;
	
	public void setLoginHandlerName(String loginHandlerName) {
		this.loginHandlerName = loginHandlerName;
	}
	
	@Override
	protected boolean preHandleOnce(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		RiotUser user = SecurityContext.getCurrentUser();
		return isAuthorized(request, response, user);
	}
	
	/**
	 * Returns <code>true</code> if a principal is set, otherwise 
	 * <code>false</code> is returned and a redirect to the login form is sent.
	 */
	protected boolean isAuthorized(HttpServletRequest request,
			HttpServletResponse response, RiotUser user) throws Exception {
		
		if (user != null) {
			return true;
		}
		else {
			request.getSession().setAttribute(INTERCEPTED_URL_ATTR, 
						request.getRequestURL().toString());
			
			HandlerUrlUtils.sendRedirect(request, response, loginHandlerName);
			return false;
		}
	}

	public static String getInterceptedUrl(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(INTERCEPTED_URL_ATTR);
	}
	
}
