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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FilterPlugin that delegates to a plain {@link Filter}. Can be used to 
 * register legacy filters without having to modify the web.xml deployment
 * descriptor.
 */
public class DelegatingFilterPlugin extends FilterPlugin  {

	Filter filter;
	
	public DelegatingFilterPlugin(Filter filter) {
		this.filter = filter;
	}

	@Override
	public void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		filter.doFilter(request, response, filterChain);
	}

}
