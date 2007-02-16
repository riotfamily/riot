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

/**
 * Servlet filter that forwards request which point to a folder to the
 * the folder's welcome page.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @deprecated In Riot 6.4 this filter has been replaced by 
 *             the {@link FolderFilterPlugin}
 */
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
