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

import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class PageController implements Controller {

	private PageResolver pageResolver;
	
	public PageController(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Page page = pageResolver.getPage(request);
		if (page == null || !page.isRequestable()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (page.isWildcardInPath()) {
			String path = pageResolver.getPathWithinSite(request);
			exposeAttributes(page.getPath(), path, request);
		}
		
		String pageType = page.getPageType();
		if (pageType == null) {
			pageType = "default";
		}
		return new ModelAndView(pageType + ".ftl");
	}

	protected void exposeAttributes(String antPattern, String urlPath,
			HttpServletRequest request) {

		AttributePattern pattern = new AttributePattern(antPattern);
		pattern.expose(urlPath, request);
	}
	
}
