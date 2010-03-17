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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.orm.hibernate3.support.OpenSessionInViewInterceptor;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * RequestInterceper that adapts Spring's {@link OpenSessionInViewInterceptor}.
 */
@Intercept(once=false)
public class OpenSessionInViewRequestInterceptor extends OpenSessionInViewInterceptor
		implements RequestInterceptor, Ordered {
	
	private int order = Ordered.HIGHEST_PRECEDENCE;
	
	private ModelMap emptyModelMap = new ModelMap();
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		preHandle(new ServletWebRequest(request, response));
		return true;
	}
	
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		postHandle(new ServletWebRequest(request, response), emptyModelMap);
	}
	
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Exception ex) throws Exception {

		afterCompletion(new ServletWebRequest(request, response), ex);
	}
}
