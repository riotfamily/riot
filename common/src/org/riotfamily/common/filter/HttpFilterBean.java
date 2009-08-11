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
