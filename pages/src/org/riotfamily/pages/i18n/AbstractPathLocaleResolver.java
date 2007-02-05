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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Abstract base class for {@link org.springframework.web.servlet.LocaleResolver
 * LocaleResolvers} that resolve the locale from the requested path. 
 */
public abstract class AbstractPathLocaleResolver implements LocaleResolver {
	
	private Log log = LogFactory.getLog(AbstractPathLocaleResolver.class);
	
	private LocaleResolver defaultResolver = new AcceptHeaderLocaleResolver();
	
	public void setDefaultResolver(LocaleResolver defaultResolver) {
		Assert.notNull(defaultResolver);
		this.defaultResolver = defaultResolver;
	}

	public final Locale resolveLocale(HttpServletRequest request) {
		String path = ServletUtils.getLookupPathForOriginatingRequest(request);
		Locale locale = resolveLocaleForPath(path);
		log.debug("Locale for path [" + path + "] is: " + locale);
		if (locale == null) {
			locale = defaultResolver.resolveLocale(request);
		}
		return locale;
	}
	
	protected Locale resolveLocaleForPath(String path) {
		String language = resolveLanguage(path);
		if (language == null) {
			return null;
		}
		String country = resolveCountry(path);
		if (country == null) {
			return new Locale(language);
		}
		String variant = resolveVariant(path);
		if (variant == null) {
			return new Locale(language, country);
		}
		return new Locale(language, country, variant);
	}
	
	protected abstract String resolveLanguage(String path);

	protected String resolveCountry(String path) {
		return null;
	}
	
	protected String resolveVariant(String path) {
		return null;
	}

	public void setLocale(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
		
		defaultResolver.setLocale(request, response, locale);
	}
	

}
