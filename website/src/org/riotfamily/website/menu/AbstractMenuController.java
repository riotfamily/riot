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
package org.riotfamily.website.menu;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.website.mvc.cache.AbstractCachingPolicyController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

/**
 * Abstract base class for controllers that render navigation menus.
 */
public abstract class AbstractMenuController 
		extends AbstractCachingPolicyController {

	private Log log = LogFactory.getLog(AbstractMenuController.class);
	
	private MenuBuilder menuBuilder;
	
	private String viewName;
	
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	private String contextPath;
	
	private String servletPrefix;
	
	private String servletSuffix;

	private boolean includeQueryStringInCacheKey = false;
	
	
	public void setMenuBuilder(MenuBuilder menuBuilder) {
		this.menuBuilder = menuBuilder;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setIncludeQueryStringInCacheKey(boolean includeQueryStringInCacheKey) {
		this.includeQueryStringInCacheKey = includeQueryStringInCacheKey;
	}

	public void appendCacheKeyInternal(StringBuffer key, 
			HttpServletRequest request) {
		
		super.appendCacheKeyInternal(key, request);
		if (includeQueryStringInCacheKey && request.getQueryString() != null) {
			key.append('?');
			key.append(request.getQueryString());
		}
	}
	
	public long getLastModified(HttpServletRequest request) {
		return menuBuilder.getLastModified(request);
	}
	
	public long getTimeToLive(HttpServletRequest request) {
		return 0;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		List items = menuBuilder.buildMenu(request);
		log.debug("MenuItems (before processing): " + items);
		items = processItems(items, request);
		log.debug("MenuItems (after processing): " + items);
		completeLinks(items, request, response);
		ModelAndView mv = new ModelAndView(viewName);
		if (items != null) {
			mv.addObject("items", items);
		}
		return mv;
	}
	
	protected abstract List processItems(List items, 
			HttpServletRequest request);
	
	protected void completeLinks(Collection items, HttpServletRequest request, 
			HttpServletResponse response) {
		
		if (items == null) {
			return;
		}
		Iterator it = items.iterator();
		while (it.hasNext()) {
			MenuItem item = (MenuItem) it.next();
			StringBuffer link = new StringBuffer();
			link.append(getContextPath(request));
			link.append(getServletPrefix(request));
			link.append(item.getLink());
			link.append(getServletSuffix(request));
			item.setLink(response.encodeURL(link.toString()));
			completeLinks(item.getChildItems(), request, response);
		}
	}

	protected String getContextPath(HttpServletRequest request) {
		if (contextPath == null) {
			contextPath = urlPathHelper.getOriginatingContextPath(request);
		}
		return contextPath;
	}
	
	protected String getServletPrefix(HttpServletRequest request) {
		if (servletPrefix == null) {
			servletPrefix = ServletUtils.getServletPrefix(request);
		}
		return servletPrefix;
	}
	
	protected String getServletSuffix(HttpServletRequest request) {
		if (servletSuffix == null) {
			servletSuffix = ServletUtils.getServletSuffix(request);
		}
		return servletSuffix;
	}
}
