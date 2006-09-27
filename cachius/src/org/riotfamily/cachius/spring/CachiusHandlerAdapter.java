package org.riotfamily.cachius.spring;

import org.riotfamily.cachius.Cache;

/**
 * @deprecated Use {@link CacheableControllerHandlerAdapter} instead.
 * 
 * The original class has been renamed to CacheableControllerHandlerAdapter
 * to match Spring's naming scheme.
 */
public class CachiusHandlerAdapter extends CacheableControllerHandlerAdapter {

	public CachiusHandlerAdapter(Cache cache) {
		super(cache);
	}

}
