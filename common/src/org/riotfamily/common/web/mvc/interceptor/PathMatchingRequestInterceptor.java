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
package org.riotfamily.common.web.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

public abstract class PathMatchingRequestInterceptor extends RequestInterceptorAdapter {

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
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String lookupPath = urlPathHelper.getLookupPathForRequest(request);
		if (include(lookupPath)) {
				return preHandleMatch(request, response);
		}
		return true;
	}
	
	protected abstract boolean preHandleMatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

}
