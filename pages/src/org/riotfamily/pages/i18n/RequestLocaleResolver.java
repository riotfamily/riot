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
package org.riotfamily.pages.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Resolves the locale previously exported by an 
 * {@link org.riotfamily.pages.i18n.LocaleExporter LocaleExporter}.
 * 
 * Use this class if you are working with two DispatcherServlets, one for
 * toplevel (public) requests and one for includes.
 */
public class RequestLocaleResolver implements LocaleResolver {
	
	private Locale defaultLocale = Locale.getDefault();
	
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = LocaleExporter.getLocale(request);
		if (locale == null) {
			return defaultLocale;
		}
		return locale;
	}
	
	public void setLocale(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
		
		LocaleResolver resolver = LocaleExporter.getLocaleResolver(request);
		Assert.notNull(resolver, "No LocaleResolver found in request. " +
				"Make sure to use a LocaleExporter.");
		
		resolver.setLocale(request, response, locale);
	}
	
}
