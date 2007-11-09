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
package org.riotfamily.common.web.interceptor;

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
