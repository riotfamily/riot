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

import org.riotfamily.common.web.mvc.interceptor.PathMatchingRequestInterceptor;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.core.security.auth.RiotUser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

/**
 * RequestInterceptor that sends a redirect to the login URL in case the user is 
 * not logged in.
 */
public final class LoginRequestInterceptor extends PathMatchingRequestInterceptor 
		implements ApplicationContextAware, Ordered {

	private static final String INTERCEPTED_URL_ATTR = LoginRequestInterceptor.class 
			+ ".interceptedUrl";

	private String loginHandlerName;
	
	private String loginUrl;

	private ApplicationContext applicationContext;
	
	private int order = 1;
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setLoginHandlerName(String loginHandlerName) {
		this.loginHandlerName = loginHandlerName;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	protected boolean preHandleMatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		RiotUser user = SecurityContext.getCurrentUser();
		if (user != null) {
			return true;	
		}
		request.getSession().setAttribute(INTERCEPTED_URL_ATTR, 
				request.getRequestURL().toString());
	
		ServletUtils.sendRedirect(request, response, getLoginUrl(request));
		return false;
	}

	private String getLoginUrl(HttpServletRequest request) {
		if (loginUrl == null) {
			loginUrl = request.getContextPath() +
					HandlerUrlUtils.getUrlResolver(applicationContext)
					.getUrlForHandler(loginHandlerName); 
		}
		return loginUrl;
	}

	public static String getInterceptedUrl(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(INTERCEPTED_URL_ATTR);
	}

}
