package org.riotfamily.common.filter;

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
