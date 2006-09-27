package org.riotfamily.common.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

public class PathMatchingInterceptor extends HandlerInterceptorAdapter {
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	private String[] includes;
	
	private String[] excludes;
	
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	public final boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		String lookupPath = urlPathHelper.getLookupPathForRequest(request);
		if (anyMatch(excludes, lookupPath) && !anyMatch(includes, lookupPath)) {
			return true;
		}
		return doPreHandle(request, response, handler);
	}
	
	protected boolean doPreHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		return true;
	}
	
	protected boolean anyMatch(String[] patterns, String path) {
		if (patterns == null) {
			return false;
		}
		for (int i = 0; i < patterns.length; i++) {
			if (pathMatcher.match(patterns[i], path)) {
				return true;
			}
		}
		return false;
	}

}
