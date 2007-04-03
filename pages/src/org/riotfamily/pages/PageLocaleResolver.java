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
package org.riotfamily.pages;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.mapping.PageHandlerMapping;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageLocaleResolver implements LocaleResolver {

	private LocaleResolver fallbackResolver = new AcceptHeaderLocaleResolver();
	
	public void setFallbackResolver(LocaleResolver fallbackResolver) {
		Assert.notNull(fallbackResolver, "Fallback LocaleResolver must not be null");
		this.fallbackResolver = fallbackResolver;
	}

	public Locale resolveLocale(HttpServletRequest request) {
		Page page = PageHandlerMapping.getPage(request);
		if (page != null) {
			return page.getLocale();
		}
		return fallbackResolver.resolveLocale(request);
	}
	
	public void setLocale(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
		
		fallbackResolver.setLocale(request, response, locale);
	}
}
