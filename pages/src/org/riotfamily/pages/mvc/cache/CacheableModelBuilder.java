package org.riotfamily.pages.mvc.cache;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mvc.ModelBuilder;

/**
 * ModelBuilder that builds cacheable models. The contract is the same as
 * for {@link org.riotfamily.cachius.spring.CacheableController 
 * cacheable controllers}.
 */
public interface CacheableModelBuilder extends ModelBuilder {

	public void appendCacheKey(StringBuffer key, HttpServletRequest request);
	
	public long getLastModified(HttpServletRequest request);
	
}
