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
package org.riotfamily.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * Generic base-class for filters hat work on <em>Http</em>ServletRequest 
 * and <em>Http</em>ServletResponse objects.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class HttpFilterBean extends GenericFilterBean {

	public final void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}
	
	protected abstract void filter(HttpServletRequest request, 
			HttpServletResponse response, FilterChain chain) 
			throws IOException, ServletException;
}
