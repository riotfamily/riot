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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.macro;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageHandlerMapping;
import org.riotfamily.pages.mapping.PageLocationResolver;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageMacroHelper {

	private PageDao pageDao;
	
	private PageLocationResolver resolver;

	private HttpServletRequest request;
	
	public PageMacroHelper(PageDao pageDao, PageLocationResolver resolver, 
			HttpServletRequest request) {
		
		this.pageDao = pageDao;
		this.resolver = resolver;
		this.request = request;
	}

	public Page getCurrentPage() {
		return PageHandlerMapping.getPage(request);
	}
	
	public String getHandlerUrl(String handlerName, String localeString) {
		Locale locale = StringUtils.parseLocaleString(localeString);
		return getHandlerUrl(handlerName, locale);
	}

	public String getHandlerUrl(String handlerName, Locale locale) {
		PageNode node = pageDao.findNodeForHandler(handlerName);
		if (node != null) {
			Page page = node.getPage(locale);
			if (page != null) {
				return resolver.getUrl(new PageLocation(page));
			}
		}
		return null;
	}
	
	public String getWildcardHandlerUrl(String handlerName, 
			String wildcardReplacement, Locale locale) {
		
		PageNode node = pageDao.findNodeForHandler(handlerName);
		if (node != null) {
			Page page = node.getPage(locale);
			if (page != null) {
				return resolver.getUrl(new PageLocation(page, wildcardReplacement));
			}
		}
		return null;
	}
	
	public String getPageUrl(Page page) {
		if (page != null) {
			return resolver.getUrl(new PageLocation(page));
		}
		return null;
	}
	
}
