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

import org.riotfamily.core.security.session.LoginManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class LogoutController implements Controller {

	private String servletPrefix = "";
	
	private String goodbyUrl = "/";
	
	public void setServletPrefix(String servletPrefix) {
		this.servletPrefix = servletPrefix;
	}

	public void setGoodbyUrl(String goodbyUrl) {
		this.goodbyUrl = goodbyUrl;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		LoginManager.logout(request, response);
		return new ModelAndView(new RedirectView(servletPrefix + goodbyUrl, true));
	}

}
