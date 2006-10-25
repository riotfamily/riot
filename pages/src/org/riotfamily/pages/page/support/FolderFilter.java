package org.riotfamily.pages.page.support;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

public class FolderFilter extends OncePerRequestFilter {

	private String servletSuffix;
	
	private String[] exclude;
	
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	private AntPathMatcher pathMatcher = new AntPathMatcher();
	
	
	public void setExclude(String[] exclude) {
		this.exclude = exclude;
	}

	public void setServletSuffix(String servletSuffix) {
		this.servletSuffix = servletSuffix;
	}

	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain chain) 
			throws ServletException, IOException {
	
		String uri = request.getRequestURI();
		if (uri.lastIndexOf('.') < uri.lastIndexOf('/')) {
			String path = urlPathHelper.getPathWithinApplication(request);
			if (include(path)) {
				PageMap pageMap = PageMap.getInstance(getServletContext());
				Page page = pageMap.getPage(path);
				if (page != null && page.isFolder()) {
					request.getRequestDispatcher(path + servletSuffix)
							.forward(request, response);
					
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

	protected boolean include(String path) {
		if (exclude != null) {
			for (int i = 0; i < exclude.length; i++) {
				String pattern = exclude[i];
				if (pathMatcher.match(pattern, path)) {
					return false;
				}
			}
		}
		return true;
	}

}
