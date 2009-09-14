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
package org.riotfamily.common.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public final class RequestHolder {

	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private int level;

	private static ThreadLocal<RequestHolder> threadLocal = 
			new ThreadLocal<RequestHolder>();
	
	private RequestHolder(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public static void set(HttpServletRequest request,
			HttpServletResponse response) {
		
		RequestHolder holder = threadLocal.get();
		if (holder == null) {
			holder = new RequestHolder(request, response);
			threadLocal.set(holder);
		}
		holder.level++;
	}
	
	public static HttpServletRequest getRequest() {
		RequestHolder holder = threadLocal.get(); 
		return holder != null ? holder.request : null;
	}
	
	public static HttpServletResponse getResponse() {
		RequestHolder holder = threadLocal.get(); 
		return holder != null ? holder.response: null;
	}
	
	public static void unset() {
		RequestHolder holder = threadLocal.get();
		Assert.notNull(holder, "Expected to find a thread-local RequestHolder");
		if (--holder.level == 0) {
			threadLocal.set(null);
		}
	}
}
