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
