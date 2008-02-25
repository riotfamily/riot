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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 7.0
 */
public class PageController extends AbstractCacheableController {

	private PageResolver pageResolver;
	
	private String viewName;

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public PageController(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Page page = pageResolver.getPage(request);
		if (page != null && page.isRequestable()) {
			TaggingContext.tag(request, Page.class.getName());
			return new ModelAndView(viewName, "page", page);
		}
		
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

	public long getTimeToLive(HttpServletRequest request) {
		return CacheableController.CACHE_ETERNALLY;
	}

}
