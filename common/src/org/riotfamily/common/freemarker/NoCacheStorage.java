package org.riotfamily.common.freemarker;

import freemarker.cache.CacheStorage;

/**
 * FreeMarker CacheStorage that does not cache anything. 
 * Can be used to disable the template caching which can come in handy
 * if you need to find memory leaks within your application.
 * 
 * @since 6.4 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class NoCacheStorage implements CacheStorage {

	public void clear() {
	}

	public Object get(Object key) {
		return null;
	}

	public void put(Object key, Object value) {
	}

	public void remove(Object key) {
	}
	
}
