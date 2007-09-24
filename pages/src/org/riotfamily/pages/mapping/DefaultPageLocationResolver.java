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

import org.riotfamily.common.web.util.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class DefaultPageLocationResolver implements PageLocationResolver {

	private Collection locales;

	private Locale fixedLocale = null;

	private PathCompleter pathCompleter;

	public DefaultPageLocationResolver(PathCompleter pathCompleter) {
		this.pathCompleter = pathCompleter;
	}

	public void setLocales(Collection locales) {
		this.locales = locales;
		if (locales != null && locales.size() == 1) {
			fixedLocale = (Locale) locales.iterator().next();
		}
	}

	protected boolean localesInPath() {
		return locales != null && locales.size() > 1;
	}

	public PageLocation getPageLocation(HttpServletRequest request) {
		String path = ServletUtils.getOriginatingPathWithinApplication(request);
		if (!path.endsWith("/")) {
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex >= 0) {
				path = path.substring(0, dotIndex);
			}
		}
		if (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
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
		else {
			locale = fixedLocale;
		}
		return new PageLocation(null, path, locale);
	}

	public String getUrl(Page page) {
		PageLocation location = new PageLocation(page);
		StringBuffer url = new StringBuffer();
		if (localesInPath() && location.getLocale() != null) {
			url.append('/');
			url.append(location.getLocale().toString().toLowerCase());
		}
		url.append(location.getPath());
		if (!page.isFolder() || pathCompleter.isPrefixMapping()) {
			pathCompleter.addServletMapping(url);
		}
		return url.toString();
	}
}
