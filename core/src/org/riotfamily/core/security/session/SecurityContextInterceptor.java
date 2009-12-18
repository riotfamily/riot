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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mvc.interceptor.RequestInterceptorAdapter;
import org.riotfamily.core.security.auth.RiotUser;
import org.springframework.core.Ordered;
import org.springframework.web.context.ServletContextAware;

/**
 * RequestInterceptor that binds the authenticated user (if present) to the
 * current thread. 
 * 
 * @see LoginManager#getUser(HttpServletRequest)
 * @see SecurityContext#bindUserToCurrentThread(RiotUser)
 */
public final class SecurityContextInterceptor extends RequestInterceptorAdapter 
		implements Ordered, ServletContextAware {

	private int order = 0;
	
	private ServletContext servletContext;

	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response) {
		LoginManager loginManager = LoginManager.getInstance(servletContext);
		RiotUser user = loginManager.getUser(request);
		SecurityContext.bindUserToCurrentThread(user);
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		SecurityContext.resetUser();
	}
}
