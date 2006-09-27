package org.riotfamily.pages.mvc.cache;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;


/**
 * Abstract base class for Controllers that cache content based on a 
 * {@link CachingPolicy CachingPolicy}.
 */
public abstract class AbstractCachingPolicyController 
		extends AbstractCacheableController 
		implements BeanFactoryAware, InitializingBean {

	private static final String DEFAULT_CACHING_POLICY = "defaultCachingPolicy";
	
	private Log log = LogFactory.getLog(AbstractCachingPolicyController.class);
	
	/** BeanFactory to lookup the defaultCachingPolicy */
	private BeanFactory beanFactory;
	
	/** Whether the controller is cacheable */
	private boolean cacheable = true;
	
	private CachingPolicy cachingPolicy;
		

	public final void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	public final void setCachingPolicy(CachingPolicy cachingPolicy) {
		this.cachingPolicy = cachingPolicy;
		if (cachingPolicy == null) {
			cacheable = false;
		}
	}
	
	public final CachingPolicy getCachingPolicy() {
		return cachingPolicy;
	}
	
	public final boolean isCacheable() {
		return cacheable;
	}
	
	public final void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
		if (cacheable && cachingPolicy == null) {
			throw new IllegalArgumentException("A CachingPolicy must be set.");
		}
	}
	
	public final void afterPropertiesSet() {
		initController();
		if (cacheable && getCachingPolicy() == null) {
			CachingPolicy defaultPolicy = (CachingPolicy) beanFactory
					.getBean(DEFAULT_CACHING_POLICY, CachingPolicy.class);
			
			if (defaultPolicy != null) {
				setCachingPolicy(defaultPolicy);
			}
			else {
				cacheable = false;
				log.warn("Controller is cacheable but no CachingPolicy was found!");
			}
		}
	}
	
	protected void initController() {
	}
	
	protected boolean bypassCache(HttpServletRequest request) {
		return !cacheable || cachingPolicy.bypassCache(request);
	}
	
	protected boolean forceRefresh(HttpServletRequest request) {
		return cachingPolicy.forceRefresh(request);
	}
	
	public long getTimeToLive(HttpServletRequest request) {
		return cachingPolicy.getTimeToLive();
	}
	
	protected final void appendCacheKey(StringBuffer key, 
			HttpServletRequest request) {
		
		appendCacheKeyInternal(key, request);
		cachingPolicy.appendCacheKey(key, request);
	}
	
	protected void appendCacheKeyInternal(StringBuffer key, 
			HttpServletRequest request) {

		key.append(getBeanName()).append(':');
		key.append(ServletUtils.getOriginalRequestUri(request));
	}
	
}

