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
	 * Returns the key that is used to look up a previously cached version.
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
