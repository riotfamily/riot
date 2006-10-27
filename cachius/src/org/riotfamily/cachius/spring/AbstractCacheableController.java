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
package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.CacheItem;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.beans.factory.BeanNameAware;


/**
 * Abstract base class for cacheable controllers.
 * 
 * @author Felix Gnass
 */
public abstract class AbstractCacheableController 
		implements CacheableController, BeanNameAware {

    protected static final long DEFAULT_TTL = 5000;

    private Log log = LogFactory.getLog(AbstractCacheableController.class);

	private String beanName;
	
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}
		
	protected String getBeanName() {
		return beanName;
	}
	
    public final String getCacheKey(HttpServletRequest request) {
    	if (bypassCache(request)) {
    		return null;
    	}
    	StringBuffer key = new StringBuffer();
    	appendCacheKey(key, request);
        return key.toString();
    }
            
    protected boolean bypassCache(HttpServletRequest request) {
		return false;
	}
	
	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		key.append(beanName).append(':');
		key.append(ServletUtils.getOriginalRequestUri(request));
	}
	
    /**
     * First checks if given CacheItem is older than the time to live. If
     * it has expired, the abstract <code>getLastModified()</code> method is 
     * called. In case the actual modification time is not newer than cached 
     * item, the item is "touched" and will live for another 
     * <code>timeToLive</code> milliseconds.
     */
    public final long getLastModified(HttpServletRequest request,
            CacheItem cacheItem) throws Exception {
            
        long now = System.currentTimeMillis();
        if (forceRefresh(request)) {
        	return now;
        }
        
        long cacheTime = cacheItem.getLastModified();
        long timeToLive = getTimeToLive(request);
        if (timeToLive < 0) {
	        log.debug("Negative TTL - Item will be cached eternally.");
			return cacheTime;
        }
        
        if (cacheTime + timeToLive < now) {
        
            log.debug("CacheItem has expired - Checking for modification...");
            long lastModified = getLastModified(request);
                    
            if (lastModified <= cacheTime) {
                log.debug("Not newer than the cacheEntry - Just touching it.");
                cacheItem.setLastModified(now);
                return now;
            }
            else {
                return lastModified;
            }
        }
        return cacheTime;
    }
    
    /**
	 * Returns whether the cached version should be discarded. Subclasses
	 * may return <code>true</code> in order to force the recreation of the
	 * cached item.
	 */
	protected boolean forceRefresh(HttpServletRequest request) {
		return false;
	}
	
    /**
     * The default implementation returns 
     * <code>System.currentTimeMillis()</code> so that the item is 
     * refreshed as soon as it expires. Subclasses should override this
     * method to return something reasonable.
     */
    public long getLastModified(HttpServletRequest request) {
        return System.currentTimeMillis();
    }
    
    /**
     * Returns <code>5000</code> as default.
     */
    public long getTimeToLive(HttpServletRequest request) {
        return DEFAULT_TTL;
    }
    
}
