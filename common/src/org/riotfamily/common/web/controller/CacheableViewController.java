/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.cache.controller.CacheableController;
import org.riotfamily.common.web.support.ServletUtils;

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
