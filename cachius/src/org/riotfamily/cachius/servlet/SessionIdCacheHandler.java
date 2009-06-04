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
package org.riotfamily.cachius.servlet;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheHandler;
import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.support.SessionIdEncoder;

/**
 * Abstract CacheHandler that can cache content containing links with 
 * <code>jsessionId</code>s. The actual sessionIds are replaced by a special
 * token which in turn is replaced with the current sessionId upon rendering.
 */
public abstract class SessionIdCacheHandler implements CacheHandler {
	
	private HttpServletRequest request;
	
	private SessionIdEncoder sessionIdEncoder;

	private CacheKeyAugmentor cacheKeyAugmentor;
	
	public SessionIdCacheHandler(HttpServletRequest request, 
			CacheKeyAugmentor cacheKeyAugmentor) {
		
		this.request = request;
		this.sessionIdEncoder = new SessionIdEncoder(request);
		this.cacheKeyAugmentor = cacheKeyAugmentor;
	}
	
	protected HttpServletRequest getRequest() {
		return request;
	}
	
	protected SessionIdEncoder getSessionIdEncoder() {
		return sessionIdEncoder;
	}
	
	public final String getCacheKey() {
		String cacheKey = getCacheKeyInternal();
        if (cacheKey == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(cacheKey);
        if (sessionIdEncoder.urlsNeedEncoding()) {
            sb.append(";jsessionid");
        }
        augmentCacheKey(sb);
        return sb.toString();
    }
	
	protected abstract String getCacheKeyInternal();
	
	protected void augmentCacheKey(StringBuffer key) {
		if (cacheKeyAugmentor != null) {
			cacheKeyAugmentor.augmentCacheKey(key, request);
		}
	}
	
	public long getLastModified() throws Exception {
		return System.currentTimeMillis();
	}

	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}
		
	public final boolean updateCacheItem(CacheItem cacheItem) 
			throws Exception {
		
		boolean ok = updateCacheItemInternal(cacheItem);
		postProcess(cacheItem);
		return ok;		
	}
	
	protected abstract boolean updateCacheItemInternal(CacheItem cacheItem) 
			throws Exception;
	
	protected void postProcess(CacheItem cacheItem) throws Exception {
	}
		
}
