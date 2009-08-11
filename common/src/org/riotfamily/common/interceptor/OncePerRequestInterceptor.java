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
package org.riotfamily.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class OncePerRequestInterceptor extends PathMatchingInterceptor {

	private String counterAttribute = getClass().getName() + ".interceptions";

	protected final boolean doPreHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		int interceptions = 0;
		Integer counter = (Integer) request.getAttribute(counterAttribute);
		if (counter != null) {
			interceptions = counter.intValue();
		}
		interceptions++;
		request.setAttribute(counterAttribute, new Integer(interceptions));

		if (interceptions == 1) {
			return preHandleOnce(request, response, handler);
		}
		
		return true;
	}

	protected boolean preHandleOnce(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		return true;
	}
	
	public final void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception)
			throws Exception {
		
		Integer counter = (Integer) request.getAttribute(counterAttribute);
		if (counter != null) {
			int interceptions = counter.intValue() - 1;
			request.setAttribute(counterAttribute, new Integer(interceptions));
			if (interceptions == 0) {
				afterLastCompletion(request, response, handler, exception);
			}
		}
	}

	protected void afterLastCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception)
			throws Exception {
	}

}
