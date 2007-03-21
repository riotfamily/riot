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
package org.riotfamily.pages.mapping;

import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.PathAndLocale;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class DefaultPathAndLocaleResolver implements PathAndLocaleResolver {

	private Collection locales;
	
	public void setLocales(Collection locales) {
		this.locales = locales;
	}
	
	protected boolean localesInPath() {
		return locales != null && !locales.isEmpty();
	}
	
	public PathAndLocale getPathAndLocale(HttpServletRequest request) {
		String path = ServletUtils.getPathWithoutServletMapping(request);
		Locale locale = null;
		if (localesInPath()) {
			if (path.length() > 1) {
				int i = path.indexOf('/', 1);
				if (i > 1) {
					String localeString = path.substring(1, i);
					locale = StringUtils.parseLocaleString(localeString);
					path = path.substring(i);
				}
			}
		}
		return new PathAndLocale(path, locale);
	}
	
	public String getUrl(PathAndLocale location, HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		url.append(request.getContextPath());
		url.append(ServletUtils.getServletPrefix(request));
		if (localesInPath() && location.getLocale() != null) {
			url.append('/');
			url.append(location.getLocale().toString().toLowerCase());
		}
		url.append(location.getPath());
		url.append(ServletUtils.getServletSuffix(request));
		return url.toString();
	}
}
