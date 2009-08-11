package org.riotfamily.cachius.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface used by various CacheHandler implementations to add additional
 * tokens to a cacheKey. 
 */
public interface CacheKeyAugmentor {

	public void augmentCacheKey(StringBuffer key, HttpServletRequest request);

}
