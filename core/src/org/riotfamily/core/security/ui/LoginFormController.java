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
package org.riotfamily.core.security.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.core.runtime.RiotRuntime;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.session.LoginManager;
import org.riotfamily.core.security.session.LoginRequestInterceptor;
import org.riotfamily.core.security.session.SecurityContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class LoginFormController implements Controller, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	private LoginManager loginManager;
	
	private String viewName = ResourceUtils.getPath(
			LoginFormController.class, "LoginForm.ftl");
	
	private String successViewName;
	

	public LoginFormController(LoginManager loginManager) {
		this.loginManager = loginManager;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		// Check users already logged in
		RiotUser user = SecurityContext.getCurrentUser();
		if (user != null) {
			return new ModelAndView(new RedirectView(getHomeUrl(request)));
		}
		
		String username = request.getParameter("riot-username");
		String password = request.getParameter("riot-password");
		
		if (StringUtils.hasText(username)) {
			if (loginManager.login(request, username, password)) {
				if (successViewName != null) {
					return new ModelAndView(successViewName);
				}
				else {
					String successUrl = LoginRequestInterceptor.getInterceptedUrl(request);
					if (successUrl == null) {
						successUrl = getHomeUrl(request);
					}
					return new ModelAndView(new RedirectView(successUrl));
				}
			}
		}
		
		if (username != null) {
			return new ModelAndView(viewName, "username", username);
		}
		else {
			return new ModelAndView(viewName);
		}
	}

	private String getHomeUrl(HttpServletRequest request) {
		return request.getContextPath() + 
				RiotRuntime.getRuntime(applicationContext).getServletPrefix();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;
	}

}
