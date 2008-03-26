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

import org.springframework.web.servlet.mvc.Controller;


/**
 * Controllers that want their output to be cached must implement this
 * interface.
 */
public interface CacheableController extends Controller {
    
	public static final long CACHE_ETERNALLY = -1L;
	
	/**
	 * returns the key that is used to look up a previously cached version.
	 * The key must include all values that govern the output, such as the 
	 * requested URL, parameters, attributes, cookie values or the name of the 
	 * controller. The key itself is not interpreted in any way and thus can 
	 * have an arbitrary format.
	 */
    public String getCacheKey(HttpServletRequest request);
    
    /**
     * Returns the time in milliseconds that has to be elapsed since the last
     * up-to-date check before another check is performed. Implementors may 
     * return {@link #CACHE_ETERNALLY} to indicate that the content should be 
     * cached eternally.
     */
    public long getTimeToLive();
    
    /**
     * Returns the date (as timestamp) when the content was modified for the 
     * last time. The controller will not be executed unless this date is newer 
     * than the timestamp of the cached version.
     */
    public long getLastModified(HttpServletRequest request) throws Exception;

}
