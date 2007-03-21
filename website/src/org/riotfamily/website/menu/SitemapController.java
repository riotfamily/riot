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

import org.riotfamily.website.mvc.cache.AbstractCachingPolicyController;
import org.springframework.web.servlet.ModelAndView;

public class SitemapController extends AbstractCachingPolicyController {

	private SitemapBuilder sitemapBuilder;
	
	private String viewName;
	
	public void setSitemapBuilder(SitemapBuilder sitemapBuilder) {
		this.sitemapBuilder = sitemapBuilder;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
		HttpServletResponse response) throws Exception {
		
		List items = sitemapBuilder.buildSitemap(request);
		encodeLinks(items, request, response);
		return new ModelAndView(viewName, "items", items);
	}
		
	protected void encodeLinks(Collection items, HttpServletRequest request, 
			HttpServletResponse response) {
		
		if (items == null) {
			return;
		}
		Iterator it = items.iterator();
		while (it.hasNext()) {
			MenuItem item = (MenuItem) it.next();
			item.setLink(response.encodeURL(item.getLink()));
			encodeLinks(item.getChildItems(), request, response);
		}
	}

	public void appendCacheKeyInternal(StringBuffer key, 
			HttpServletRequest request) {
		
		super.appendCacheKeyInternal(key, request);		
		sitemapBuilder.appendCacheKey(key, request);
	}
	
	public long getLastModified(HttpServletRequest request) {
		return sitemapBuilder.getLastModified(request);
	}

}
