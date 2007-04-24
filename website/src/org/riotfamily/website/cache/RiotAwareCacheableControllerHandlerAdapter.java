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
package org.riotfamily.website.cache;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.spring.CacheableControllerHandlerAdapter;
import org.riotfamily.riot.security.AccessController;

/**
 * CacheableControllerHandlerAdapter subclass that adds a prefix to the 
 * cache-key when a request is performed by an authenticated Riot user.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RiotAwareCacheableControllerHandlerAdapter 
		extends CacheableControllerHandlerAdapter {

	private String riotCacheKeyPrefix = "riot:";
	
	public RiotAwareCacheableControllerHandlerAdapter(Cache cache) {
		super(cache);
	}

	/**
	 * Sets the prefix that is added to the cache-key if the request is 
	 * performed by an authenticated Riot user.
	 */
	public void setRiotCacheKeyPrefix(String riotCacheKeyPrefix) {
		this.riotCacheKeyPrefix = riotCacheKeyPrefix;
	}

	protected CacheItem getCacheItem(String cacheKey, 
			HttpServletRequest request) {
		
		if (cacheKey != null && AccessController.isAuthenticatedUser()) {
			cacheKey = riotCacheKeyPrefix + cacheKey;
		}
		return super.getCacheItem(cacheKey, request);
	}
}
