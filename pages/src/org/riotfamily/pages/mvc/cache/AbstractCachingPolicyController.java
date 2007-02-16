/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
		key.append(ServletUtils.getOriginatingRequestUri(request));
	}
	
}

