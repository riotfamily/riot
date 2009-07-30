package org.riotfamily.website.cache;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.common.view.MacroHelperFactory;

public class CacheMacroHelperFactory implements MacroHelperFactory {

	private CacheService cacheService;
	
	private CacheKeyAugmentor cacheKeyAugmentor;

	
	public CacheMacroHelperFactory(CacheService cacheService,
			CacheKeyAugmentor cacheKeyAugmentor) {

		this.cacheService = cacheService;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
	}


	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {
		
		return new CacheMacroHelper(cacheService, cacheKeyAugmentor, request);
	}

}
