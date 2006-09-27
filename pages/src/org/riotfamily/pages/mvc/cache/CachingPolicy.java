package org.riotfamily.pages.mvc.cache;

import javax.servlet.http.HttpServletRequest;

/**
 * Policy that governs the caching behaviour of controllers.
 * 
 * @see org.riotfamily.pages.mvc.cache.AbstractCachingPolicyController
 */
public interface CachingPolicy {

	/**
	 * Returns whether the cache should be bypassed.
	 */
	public boolean bypassCache(HttpServletRequest request);

	/**
	 * Returns whether the cached version should be discarded. Implementors
	 * may return <code>true</code> in order to force the recreation of the
	 * cached item.
	 */
	public boolean forceRefresh(HttpServletRequest request);

	/**
	 * Returns the number of milliseconds that need to elapse before the
	 * controller will perform a modification-check again.
	 */
	public long getTimeToLive();
	
	/**
	 * Implementors may append strings to the key that are common to all
	 * controllers using the policy. For example a policy could append the
	 * current locale to the cache key.
	 */
	public void appendCacheKey(StringBuffer key, HttpServletRequest request);
	
}
