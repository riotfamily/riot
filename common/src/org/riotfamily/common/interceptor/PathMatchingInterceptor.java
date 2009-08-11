package org.riotfamily.common.interceptor;

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
	
	private boolean includesOverwriteExcludes = false;
	
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
	
	public void setIncludesOverwriteExcludes(boolean includesOverwriteExcludes) {
		this.includesOverwriteExcludes = includesOverwriteExcludes;
	}

	public final boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		String lookupPath = urlPathHelper.getLookupPathForRequest(request);
		if (include(lookupPath)) {
			return doPreHandle(request, response, handler);	
		}
		return true;
	}
	
	protected boolean doPreHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		return true;
	}
	
	private boolean include(String path) {
		if (includesOverwriteExcludes) {
			return !anyMatch(excludes, path) || anyMatch(includes, path);
		}
		return anyMatch(includes, path) && !anyMatch(excludes, path);
	}
	
	protected boolean anyMatch(String[] patterns, String path) {
		if (patterns == null) {
			return true;
		}
		for (int i = 0; i < patterns.length; i++) {
			if (pathMatcher.match(patterns[i], path)) {
				return true;
			}
		}
		return false;
	}

}
