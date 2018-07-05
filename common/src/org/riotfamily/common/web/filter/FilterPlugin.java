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
package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface that can be plugged into a {@link PluginFilter} to filter request
 * and response objects like a {@link javax.servlet.Filter servlet filter}.
 * <p>
 * Note that plugins are only invoked once per request, so they won't be 
 * called for included or forwarded requests.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface FilterPlugin {

	/**
	 * Filters the request/response. The contract is the same as for
	 * {@link javax.servlet.FilterChain#doFilter}.
	 */
	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException;
}
