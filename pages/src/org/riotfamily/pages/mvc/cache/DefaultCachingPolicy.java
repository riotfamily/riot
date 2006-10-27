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
package org.riotfamily.pages.mvc.cache;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.riot.security.AccessController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Default CachingPolicy implementation.
 * <p>
 * If a ViewModeResolver is set and the request is performed by a Riot user 
 * in preview-mode, the cache will be bypassed, i.e. the output will never be
 * cached, hence the request will always be handled by the controller.
 * </p>
 * <p>
 * If the {@link #setForceRefreshForRiotUsers(boolean) forceRefreshForRiotUsers}
 * property is set to <code>true</code> (the default), a refresh will be forced
 * for every request made by an authenticated Riot user.
 * </p>
 * <p>
 * A global time-to-live may be set via the {@link #setExpiresAfter(String)}
 * method. This will prevent the <code>getLastModified()</code> method of any 
 * controller from beeing called for the specified period of time.
 * </p>
 * <p>
 * Another useful feature is that you may choose to append the current language
 * (as determined by the LocaleResolver) to every cache-key. To do so, set the
 * {@link #setAppendLanguageToCacheKey(boolean) appendLanguageToCacheKey} 
 * property to <code>true</code>.
 * </p>
 */
public class DefaultCachingPolicy implements CachingPolicy {

	private Log log = LogFactory.getLog(DefaultCachingPolicy.class);
	
	private long timeToLive;
	
	private boolean forceRefreshForRiotUsers = true;
	
	private boolean appendLanguageToCacheKey = false;
	
	private ViewModeResolver viewModeResolver;
	
	public void setExpiresAfter(String s) {
    	timeToLive = FormatUtils.parseMillis(s);
	}
	
	public void setForceRefreshForRiotUsers(boolean forceRefreshForRiotUsers) {
		this.forceRefreshForRiotUsers = forceRefreshForRiotUsers;
	}

	public void setAppendLanguageToCacheKey(boolean appendLanguageToCacheKey) {
		this.appendLanguageToCacheKey = appendLanguageToCacheKey;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	public boolean bypassCache(HttpServletRequest request) {
		if (viewModeResolver != null 
				&& viewModeResolver.isPreviewMode(request)) {
			
            log.debug("Preview mode -- bypassing cache");
            return true;
        }
		return false;
	}

	public boolean forceRefresh(HttpServletRequest request) {
		if (forceRefreshForRiotUsers 
				&& AccessController.isAuthenticatedUser()) {
			
			log.debug("Authenticated user -- forcing cache refresh");
            return true;
        }
		return false;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		if (appendLanguageToCacheKey) {
			Locale locale = RequestContextUtils.getLocale(request);
			key.append('_').append(locale.getLanguage());
		}
	}

}
