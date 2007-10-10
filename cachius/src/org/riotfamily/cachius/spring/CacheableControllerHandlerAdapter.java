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
package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.CachiusResponseWrapper;
import org.riotfamily.cachius.ItemUpdater;
import org.riotfamily.cachius.support.SessionUtils;
import org.riotfamily.common.web.view.ViewResolverHelper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.LastModified;

/**
 * Adapter that handles {@link CacheableController cacheable controllers}.
 * <p>
 * The adapter checks if a Controller is cacheable and whether there is
 * an up-to-date cache item which can be served. If not, the controller's
 * <code>handleRequest()</code> method is invoked and the output is captured
 * and written to the cache.
 * </p>
 * <p>
 * Since 6.5 the adapter does no longer support regular controllers. In order
 * to support both cacheable and non-cacheable controllers you hav to add
 * a {@link org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter}
 * to your context manually.
 * </p>
 * @author Felix Gnass
 */
public class CacheableControllerHandlerAdapter implements HandlerAdapter,
		DisposableBean, ApplicationContextAware, Ordered {

    private Log log = LogFactory.getLog(CacheableControllerHandlerAdapter.class);

    private Cache cache;

    private ViewResolverHelper viewResolverHelper;

    private int order = 0;

    public CacheableControllerHandlerAdapter(Cache cache) {
		this.cache = cache;
	}

    /**
	 * Returns the order in which this HandlerAdapter is processed.
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Set the order in which this HandlerAdapter is processed.
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	public void setApplicationContext(ApplicationContext context) {
        viewResolverHelper = new ViewResolverHelper(context);
    }

    /**
     * Persists the cache on shutdown.
     * @see DisposableBean
     */
    public void destroy() {
        cache.persist();
    }

    /**
     * Returns <code>true</code> if handler implements the
     * {@link CacheableController} interface.
     */
    public boolean supports(Object handler) {
        return handler instanceof CacheableController;
    }

    /**
     * Retrieves a CacheItem from the cache by calling
     * {@link #getCacheItem(CacheableController, HttpServletRequest)}.
     * If an item was found and it's up-to-date, the cached content is served.
     * Otherwise {@link #handleRequestAndUpdateCacheItem} is invoked.
     */
    public ModelAndView handle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

    	CacheableController controller = (CacheableController) handler;
    	String cacheKey = controller.getCacheKey(request);
		CacheItem cacheItem = getCacheItem(cacheKey, request);
        if (cacheItem == null) {
            log.debug("No cacheItem - Response won't be cached");
            return controller.handleRequest(request, response);
        }

        if (isUpToDate(cacheItem, controller, request)) {
            cacheItem.writeTo(request, response);
            return null;
        }
        else {
        	return handleRequestAndUpdateCacheItem(request, response,
        			controller, cacheItem);
        }
	}

	protected ModelAndView handleRequestAndUpdateCacheItem(
			HttpServletRequest request, HttpServletResponse response,
			CacheableController controller, CacheItem cacheItem)
			throws Exception {

		ItemUpdater update = new ItemUpdater(cacheItem, request);
		CachiusResponseWrapper wrapper = new CachiusResponseWrapper(
				response, update);

		ModelAndView mv = controller.handleRequest(request, wrapper);
		if (mv == null) {
			wrapper.flushBuffer();
	        update.updateCacheItem();
	        return null;
	    }
	    else {
	    	View view = viewResolverHelper.resolveView(request, mv);
	    	View cachingView = new CachingView(view, wrapper, update);
	        return new ModelAndView(cachingView, mv.getModel());
	    }
	}

    protected CacheItem getCacheItem(String cacheKey,
    		HttpServletRequest request) {

        if (cacheKey == null) {
            log.debug("Cache key is null - Response won't be cached.");
            return null;
        }

        boolean encodedUrls = SessionUtils.urlsNeedEncoding(request);
        if (encodedUrls) {
            cacheKey += ";jsessionid";
        }
        log.debug("Getting cache item for key " + cacheKey);
        CacheItem cacheItem = cache.getItem(cacheKey);
        if (cacheItem != null && (cacheItem.isNew() || !cacheItem.exists())) {
        	cacheItem.setFilterSessionId(encodedUrls);
        }
        return cacheItem;
    }

    /**
     * Checks whether the given item is up-to-date. If the item is new or
     * does not exist <code>false</code> is returned. Otherwise
     * {@link CacheableController#getLastModified getLastModified()} is
     * invoked on the controller and compared to the item's timestamp.
     * <p>
     * NOTE: As a side effect, the item's timestamp is set to the current
     * time if a modification has occurred. This will prevent other threads
     * from invoking handleRequest(), too.
     * </p>
     */
    protected boolean isUpToDate(CacheItem cacheItem,
    		CacheableController controller, HttpServletRequest request)
    		throws Exception {

        // No need to check if the item has just been constructed or
        // the cache file has been deleted
        if (cacheItem.isNew() || !cacheItem.exists()) {
        	log.debug("Item is new or has been invalidated");
            return false;
        }

        long now = System.currentTimeMillis();
        long ttl = controller.getTimeToLive();
        if (ttl == CacheableController.CACHE_ETERNALLY) {
        	log.debug("Item is cached eternally");
        	return true;
        }
        if (cacheItem.getLastCheck() + ttl < now) {
	        long mtime = controller.getLastModified(request);
	        log.debug("Last modified: " + mtime);
	        cacheItem.setLastCheck(now);
	        if (mtime > cacheItem.getLastModified()) {
	            // Update the timestamp so that other threads won't call
	            // the handleRequest() method, too. Note: For new items
	            // lastModified is set by the CacheItem.update() method.
	            cacheItem.setLastModified(now);
	        	log.debug("Item is expired");
	            return false;
	        }
        }
        else if (log.isDebugEnabled()) {
        	log.debug("Item not expired yet, will live for another " +
        			(ttl - (now - cacheItem.getLastCheck())) + " ms.");
        }
       	return true;
    }

    /**
     * Returns the lastModified date as reported by the Controller/CacheItem.
     */
    public long getLastModified(HttpServletRequest request, Object handler) {
    	if (handler instanceof LastModified) {
	    	CacheableController controller = (CacheableController) handler;
			String cacheKey = controller.getCacheKey(request);
			CacheItem cacheItem = getCacheItem(cacheKey, request);
			if (cacheItem != null) {
				if (!cacheItem.isNew()) {
					long now = System.currentTimeMillis();
			        long ttl = controller.getTimeToLive();
			        if (ttl == CacheableController.CACHE_ETERNALLY
			        		|| cacheItem.getLastCheck() + ttl <= now) {

			        	return cacheItem.getLastModified();
			        }
				}
	    		try {
	    			return controller.getLastModified(request);
	    		}
	    		catch (Exception e) {
	    			log.error("Error invoking the last-modified method", e);
	    		}
			}
    	}
        return -1L;
    }

}