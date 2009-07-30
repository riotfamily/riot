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
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.controller;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.common.servlet.ServletUtils;

public class CacheableViewController extends ConfigurableViewController 
		implements CacheableController {

	/**
     * The default implementation always returns <code>CACHE_ETERNALLY</code>.
     */
	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}
	
	/**
     * The default implementation returns 
     * <code>System.currentTimeMillis()</code> so that the item is 
     * refreshed as soon as it expires.
     */
    public long getLastModified(HttpServletRequest request) {
        return System.currentTimeMillis();
    }
    
    /**
     * Whether the cache should be bypassed for the given request. The default
     * implementation always returns <code>false</code>.
     */
    protected boolean bypassCache(HttpServletRequest request) {
    	return false;
    }
    
    public String getCacheKey(HttpServletRequest request) {
    	if (bypassCache(request)) {
    		return null;
    	}
    	StringBuffer key = request.getRequestURL();
		if (!ServletUtils.isDirectRequest(request)) {
			key.append('#').append(ServletUtils.getPathWithinApplication(request));
		}
		appendCacheKey(key, request);
		return key.toString();
    }
    
    /**
     * Subclasses may overwrite this method to append values to the cache-key.
     * The default implementation does nothing.
     */
	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
	}
	
}
