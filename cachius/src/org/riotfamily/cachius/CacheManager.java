package org.riotfamily.cachius;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

	private Map<String, Cache> caches = new HashMap<String, Cache>();
	
	private Cache defaultCache = new Cache();
	
	public Cache getCache(String region) {
		if (region == null) {
			return defaultCache;
		}
		Cache cache = caches.get(region);
		if (cache == null) {
			cache = defaultCache;
		}
		return cache;
	}
}
