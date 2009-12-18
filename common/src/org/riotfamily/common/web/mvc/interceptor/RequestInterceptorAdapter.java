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


/**
 * Abstract adapter class for the RequestInterceptor interface, for simplified
 * implementation of pre-only/post-only interceptors.
 */
public class RequestInterceptorAdapter implements RequestInterceptor {

	/**
	 * This implementation returns <code>true</code>.
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return true;
	}
	
	/**
	 * This implementation is empty.
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	}

	/**
	 * This implementation is empty.
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
	}

}
