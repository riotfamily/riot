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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Acts like a {@link FilterChain}, just for FilterPlugins.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class PluginChain implements FilterChain {

	private FilterChain filterChain;
	
	private FilterPlugin[] plugins;
	
	private int nextPlugin = 0;
	
	public PluginChain(FilterChain filterChain, FilterPlugin[] plugins) {
		this.filterChain = filterChain;
		this.plugins = plugins;
	}

	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response) 
			throws IOException, ServletException {
		
		if (nextPlugin < plugins.length) {
			plugins[nextPlugin++].doFilter(request, response, this);
		}
		else {
			filterChain.doFilter(request, response);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		
		doFilter((HttpServletRequest) request, (HttpServletResponse) response);
	}
}
