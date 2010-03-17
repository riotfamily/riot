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

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.mvc.interceptor.Intercept;
import org.riotfamily.common.web.mvc.interceptor.RequestInterceptor;
import org.riotfamily.common.web.support.ServletUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * DispacherServlet that supports global request interception.
 * <p>
 * Works like Spring's {@link HandlerInterceptor} with the difference that the 
 * interceptors don't need to be registered with each HandlerMapping.
 * </p>
 */
public class InterceptingDispatcherServlet extends HeadDispatcherServlet {

	private static final String APPLIED_INTERCEPTORS = InterceptingDispatcherServlet.class.getName() + ".appliedInterceptors";
	
	private List<RequestInterceptor> interceptors;
	
	@Override
	protected void initStrategies(ApplicationContext context) {
		super.initStrategies(context);
		initInterceptors(context);
	}
	
	/**
	 * Looks up all {@link RequestInterceptor} beans in the given ApplicationContext.
	 */
	protected void initInterceptors(ApplicationContext context) {
		this.interceptors = SpringUtils.orderedBeans(context, RequestInterceptor.class);
	}
	
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		InterceptorChain chain = new InterceptorChain(request, response);
		try {
			if (chain.preHandle()) {
				super.service(request, response);
				chain.postHandle();
			}
		}
		catch (ServletException ex) {
			chain.handleException(ex);
			throw ex;
		}
		catch (IOException ex) {
			chain.handleException(ex);
			throw ex;
		}
		catch (Exception ex) {
			chain.handleException(ex);
			throw new ServletException(ex);
		}
	}
	
	private class InterceptorChain {
		
		private HttpServletRequest request;
		
		private HttpServletResponse response;
		
		private Set<RequestInterceptor> appliedInterceptors;
		
		private LinkedList<RequestInterceptor> preHandled = Generics.newLinkedList();
		
		@SuppressWarnings("unchecked")
		public InterceptorChain(HttpServletRequest request,
				HttpServletResponse response) {
			
			this.request = request;
			this.response = response;
			
			appliedInterceptors = (Set<RequestInterceptor>) request.getAttribute(APPLIED_INTERCEPTORS);
			if (appliedInterceptors == null) {
				appliedInterceptors = Generics.newHashSet();
				request.setAttribute(APPLIED_INTERCEPTORS, appliedInterceptors);
			}
		}

		public boolean preHandle() throws Exception {
			for (RequestInterceptor interceptor : interceptors) {
				if (isEligible(interceptor)) {
					if (!interceptor.preHandle(request, response)) {
						afterCompletion(null);
						return false;
					}
					preHandled.addFirst(interceptor);
					appliedInterceptors.add(interceptor);
				}
			}
			return true;
		}
		
		private boolean isEligible(RequestInterceptor interceptor) {
			Intercept intercept = AnnotationUtils.findAnnotation(interceptor.getClass(), Intercept.class);
			if (intercept == null) {
				// No annotation present - default is to apply the interceptor once
				return !alreadyApplied(interceptor);
			}
			
			if (intercept.once() && alreadyApplied(interceptor)) {
				return false;
			}
			
			boolean include = ServletUtils.isInclude(request);
			boolean forward = ServletUtils.isForward(request);
			boolean direct = !include && !forward;
			
			return (forward && intercept.forward())
					|| (include && intercept.include())
					|| (direct && intercept.request());
		}

		private boolean alreadyApplied(RequestInterceptor interceptor) {
			return appliedInterceptors.contains(interceptor);
		}
		
		public void postHandle() throws Exception {
			for (RequestInterceptor interceptor : preHandled) {
				interceptor.postHandle(request, response);
			}
			afterCompletion(null);
		}
		
		public void handleException(Exception ex) {
			afterCompletion(ex);
		}
		
		private void afterCompletion(Exception ex) {
			Iterator<RequestInterceptor> it = preHandled.iterator();
			while(it.hasNext()) {
				try {
					RequestInterceptor interceptor = it.next();
					it.remove();
					interceptor.afterCompletion(request, response, ex);
				}
				catch (Throwable e) {
					logger.error("RequestInterceptor.afterCompletion threw exception", e);
				}				
			}
		}
	}
	

}
