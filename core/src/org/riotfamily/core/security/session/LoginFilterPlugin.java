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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.filter.PathMatchingFilterPlugin;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.core.security.auth.RiotUser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * FilterPlugin that sends a redirect to the login URL in case the user is 
 * not logged in.
 */
public final class LoginFilterPlugin extends PathMatchingFilterPlugin 
		implements ApplicationContextAware {

	private static final String INTERCEPTED_URL_ATTR = LoginFilterPlugin.class 
			+ ".interceptedUrl";

	private String loginHandlerName;
	
	private String loginUrl;

	private ApplicationContext applicationContext;
	
	public LoginFilterPlugin() {
		setOrder(1);
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
	protected void filterInternal(HttpServletRequest request,
		HttpServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {
		
		RiotUser user = SecurityContext.getCurrentUser();
		if (user != null) {
			filterChain.doFilter(request, response);	
		}
		else {
			request.getSession().setAttribute(INTERCEPTED_URL_ATTR, 
					request.getRequestURL().toString());
		
			ServletUtils.sendRedirect(request, response, getLoginUrl(request));
		}
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
