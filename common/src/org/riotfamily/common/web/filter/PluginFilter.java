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
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

/**
 * Servlet filter delegates the filtering to {@link FilterPlugin} beans.
 * <p>
 * In contrast to Spring's {@link org.springframework.web.filter.DelegatingFilterProxy}
 * multiple targets are supported which are processed as a chain. Additionally
 * the beans can be located in any BeanFactory that is aware of the 
 * ServletContext, not only the root ApplicationContext.
 * <p>
 * Riot uses this mechanism to allow modules to register servlet filters without
 * having to modify the web.xml deployment descriptor.
 * 
 * @see FilterPlugin
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public final class PluginFilter extends OncePerRequestFilter {

	private static final String ATTRIBUTE_PREFIX = 
			PluginFilter.class.getName() + '.';
	
	private String[] exclude;
	
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	private AntPathMatcher pathMatcher = new AntPathMatcher();
	
	private FilterPlugin[] plugins = new FilterPlugin[0];
	
	/**
	 * Sets Ant-style path patterns that should not be filtered.
	 */
	public void setExclude(String[] exclude) {
		this.exclude = exclude;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initFilterBean() throws ServletException {
		List<FilterPlugin> plugins = (List<FilterPlugin>) getServletContext()
				.getAttribute(ATTRIBUTE_PREFIX + getFilterName() + ".plugins");
		
		if (plugins != null) {
			setPlugins(plugins);
		}
		else {
			getServletContext().setAttribute(ATTRIBUTE_PREFIX + getFilterName(), this);
		}
	}
	
	public void setPlugins(List<FilterPlugin> plugins) {
		int size = plugins != null ? plugins.size() : 0;
		this.plugins = new FilterPlugin[size];
		for (int i = 0; i < size; i++) {
			this.plugins[i] = plugins.get(i); 
		}
	}
	
	/**
	 * Skips the filter if the requested path matches one of the Ant-style
	 * pattern set via {@link #setExclude}.
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		if (exclude != null) {
			String path = urlPathHelper.getPathWithinApplication(request);
			for (int i = 0; i < exclude.length; i++) {
				if (pathMatcher.match(exclude[i], path)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) 
			throws ServletException, IOException {
		
		new PluginChain(filterChain, plugins).doFilter(request, response);
	}
		
	static void setPlugins(ServletContext servletContext, String filterName, List<FilterPlugin> plugins) {
		servletContext.setAttribute(ATTRIBUTE_PREFIX + filterName + ".plugins", plugins);
	}
	
	static PluginFilter getInstance(ServletContext servletContext, String filterName) {
		return (PluginFilter) servletContext.getAttribute(ATTRIBUTE_PREFIX + filterName);
	}
	
}
