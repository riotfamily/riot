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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.beans.factory.BeanNameAware;


/**
 * Abstract base class for cacheable controllers.
 * 
 * @author Felix Gnass
 */
public abstract class AbstractCacheableController 
		implements CacheableController, BeanNameAware {

	private String beanName;
	
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}
		
	protected String getBeanName() {
		return beanName;
	}
	
	/**
	 * Returns the cache-key for the request unless 
	 * {@link #bypassCache(HttpServletRequest)} returns <code>true</code>.
	 * The method creates a StringBuffer and passes it to 
	 * {@link #appendCacheKey(StringBuffer, HttpServletRequest)}.
	 */
    public final String getCacheKey(HttpServletRequest request) {
    	if (bypassCache(request)) {
    		return null;
    	}
    	StringBuffer key = new StringBuffer();
    	appendCacheKey(key, request);
        return key.toString();
    }
            
    /**
     * Returns whether the cache should be bypassed. The default implementation 
     * always returns <code>false</code>. 
     */
    protected boolean bypassCache(HttpServletRequest request) {
		return false;
	}

    /**
     * Subclasses may overwrite this method to append values to the cache-key.
     * The default implementation appends 
     * {@link ServletUtils#getIncludeUri(HttpServletRequest)}.
     */
	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		key.append(ServletUtils.getIncludeUri(request));
	}

	/**
	 * The default implementation returns <code>0</code> so that 
	 * {@link #getLastModified(HttpServletRequest)} is invoked every time the
	 * controller is requested.
	 */
	public long getTimeToLive() {
		return 0;
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
    
}
