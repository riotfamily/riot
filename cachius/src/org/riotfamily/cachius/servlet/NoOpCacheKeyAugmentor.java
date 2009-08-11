package org.riotfamily.cachius.servlet;

import javax.servlet.http.HttpServletRequest;

public class NoOpCacheKeyAugmentor implements CacheKeyAugmentor {

	public void augmentCacheKey(StringBuffer key, HttpServletRequest request) {
	}

}
