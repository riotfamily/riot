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
package org.riotfamily.common.web.mvc.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

/**
 * HandlerInterceptor that sends a 404 error if the request is not an 
 * include request, i.e. was not dispatched by a {@link RequestDispatcher}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class IncludeOnlyInterceptor extends PathMatchingInterceptor {

	private Logger log = LoggerFactory.getLogger(IncludeOnlyInterceptor.class);
	
	protected boolean doPreHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		if (!WebUtils.isIncludeRequest(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			log.warn("Direct access prevented by IncludeOnlyInterceptor");
			return false;
		}
		return true;
	}

}
