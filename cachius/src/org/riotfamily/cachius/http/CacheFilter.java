package org.riotfamily.cachius.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.persistence.SimpleDiskStore;

public class CacheFilter implements Filter {

	private CacheService cacheService = new CacheService(new SimpleDiskStore());
	
	public void init(FilterConfig config) throws ServletException {
	}
	
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		try {
			cacheService.handle(new FilterHandler(
					(HttpServletRequest) request, 
					(HttpServletResponse) response,
					filterChain));
		}
		catch (IOException e) {
			throw e;
		}
		catch (ServletException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private static class FilterHandler extends AbstractHttpHandler {

		private FilterChain filterChain;

		public FilterHandler(HttpServletRequest request, HttpServletResponse response, 
				FilterChain filterChain) {
			
			super(request, response);
			this.filterChain = filterChain;
		}
		
		@Override
		protected void handleRequest(HttpServletRequest request, 
				HttpServletResponse response) 
				throws IOException, ServletException {
			
			filterChain.doFilter(request, response);
		}
		
	}
}
