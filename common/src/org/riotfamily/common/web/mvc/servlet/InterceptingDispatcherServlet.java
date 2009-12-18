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
package org.riotfamily.common.web.mvc.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.mvc.interceptor.RequestInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * DispacherServlet that supports global request interception.
 * <p>
 * Works like Spring's {@link HandlerInterceptor} with the difference that the 
 * interceptors don't need to be registered with each HandlerMapping.
 * </p>
 */
public class InterceptingDispatcherServlet extends HeadDispatcherServlet {

	private RequestInterceptor[] interceptors;
	
	@Override
	protected void initStrategies(ApplicationContext context) {
		super.initStrategies(context);
		initInterceptors(context);
	}
	
	/**
	 * Looks up all {@link RequestInterceptor} beans in the given ApplicationContext.
	 */
	protected void initInterceptors(ApplicationContext context) {
		List<RequestInterceptor> list = SpringUtils.orderedBeans(context, RequestInterceptor.class);
		this.interceptors = list.toArray(new RequestInterceptor[list.size()]);
	}
	
	@Override
	protected void doDispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		int interceptorIndex = -1;
		try {
			for (int i = 0; i < interceptors.length; i++) {
				if (!interceptors[i].preHandle(request, response)) {
					triggerAfterCompletion(interceptorIndex, request, response, null);
					return;
				}
				interceptorIndex = i;
			}
			
			super.doDispatch(request, response);
			
			for (int i = interceptors.length - 1; i >= 0; i--) {
				interceptors[i].postHandle(request, response);
			}

			triggerAfterCompletion(interceptorIndex, request, response, null);
		}
		catch (Exception ex) {
			triggerAfterCompletion(interceptorIndex, request, response, ex);
			throw ex;
		}
	}
	
	private void triggerAfterCompletion(
			int interceptorIndex,
			HttpServletRequest request,
			HttpServletResponse response,
			Exception ex) throws Exception {

		for (int i = interceptorIndex; i >= 0; i--) {
			try {
				interceptors[i].afterCompletion(request, response, ex);
			}
			catch (Throwable e) {
				logger.error("RequestInterceptor.afterCompletion threw exception", e);
			}
		}
	}
	
}
