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
package org.riotfamily.common.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mvc.view.JsonView;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that invokes <code>request.getSession(false)</code> to keep an
 * existing HTTP session alive.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class SessionKeepAliveController implements Controller {

	private String identity = ObjectUtils.getIdentityHexString(this);
	
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		request.getSession(false);
		return new ModelAndView(new JsonView("{ identity: \"" + identity + "\" }"));
	}

}
