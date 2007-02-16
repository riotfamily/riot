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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor that exposes the nearest ancestor page for the current 
 * request URI. Use this Interceptor with HandlerMappings that don't use the
 * PageMap (like the BeanNameHandlerMapping) when you want to map fixed
 * URLs into your sitemap. This way your menus will highlight the correct
 * (ancestor) page.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PageInterceptor extends HandlerInterceptorAdapter {

	private PageMap pageMap;
	
	public PageInterceptor(PageMap pageMap) {
		this.pageMap = pageMap;
	}

	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		Page page = PageUtils.getPage(request);
		if (page == null) {
			PageUtils.exposePageMap(request, pageMap);
			String path = ServletUtils.getOriginatingPathWithoutServletMapping(request);
			page = pageMap.getPageOrAncestor(path);
			PageUtils.exposePage(request, page);
		}
		return true;
	}

}
