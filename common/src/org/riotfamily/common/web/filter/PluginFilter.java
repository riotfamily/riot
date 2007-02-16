/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.OrderComparator;
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
	
	private OrderComparator orderComparator = new OrderComparator();
	
	private FilterPlugin[] plugins = new FilterPlugin[0];
	
	/**
	 * Sets Ant-style path patterns that should not be filtered.
	 */
	public void setExclude(String[] exclude) {
		this.exclude = exclude;
	}
	
	protected void initFilterBean() throws ServletException {
		getServletContext().setAttribute(
				ATTRIBUTE_PREFIX + getFilterName(), this);
	}
	
	synchronized void addPlugin(FilterPlugin plugin) {
		int n = plugins.length;
        FilterPlugin[] newPlugins = new FilterPlugin[n + 1];
        System.arraycopy(plugins, 0, newPlugins, 0, n);
        newPlugins[n++] = plugin;
        Arrays.sort(newPlugins, orderComparator);
		plugins = newPlugins;
	}
	
	synchronized void removePlugin(FilterPlugin plugin) {
		int n = plugins.length;
		FilterPlugin[] newPlugins = new FilterPlugin[n - 1];
		int j = 0;
        for (int i = 0; i < n; i++) {
        	if (plugins[i] != plugin) {
        		newPlugins[j++] = plugins[i];
        	}
        }
		plugins = newPlugins;
	}

	/**
	 * Skips the filter if the requested path matches one of the Ant-style
	 * pattern set via {@link #setExclude}.
	 */
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
	
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) 
			throws ServletException, IOException {
		
		new PluginChain(filterChain, plugins).doFilter(request, response);
	}
	
	static PluginFilter getInstance(ServletContext servletContext, 
			String filterName) {
		
		return (PluginFilter) servletContext.getAttribute(
				ATTRIBUTE_PREFIX + filterName);
	}
	
}
