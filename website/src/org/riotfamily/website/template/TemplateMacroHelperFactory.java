package org.riotfamily.website.template;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.common.web.view.MacroHelperFactory;

public class TemplateMacroHelperFactory implements MacroHelperFactory {

	private CacheService cacheService;
	
	private CacheKeyAugmentor cacheKeyAugmentor;
	
	public TemplateMacroHelperFactory(CacheService cacheService,
			CacheKeyAugmentor cacheKeyAugmentor) {
		
		this.cacheService = cacheService;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {
		
		return new TemplateMacroHelper(cacheService, cacheKeyAugmentor, request);
	}

}
