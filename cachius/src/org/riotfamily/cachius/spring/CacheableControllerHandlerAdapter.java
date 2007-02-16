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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;
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
 * If a controller does not implement the {@link CacheableController} interface
 * the adapter does the same as Spring's 
 * {@link org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter}.
 * </p>
 * 
 * @author Felix Gnass
 */
public class CacheableControllerHandlerAdapter implements HandlerAdapter, 
		DisposableBean, ApplicationContextAware {
	
    private Log log = LogFactory.getLog(CacheableControllerHandlerAdapter.class);

    private Cache cache;
    
    private ViewResolverHelper viewResolverHelper;
    
    public CacheableControllerHandlerAdapter(Cache cache) {
		this.cache = cache;
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
     * {@link Controller Controller} interface.
     */
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    /**
     * If the given handler is cacheable, 
     * {@link #handleCacheableController handleCacheableController()} is
     * invoked. Otherwise the handler is casted to {@link Controller Controller} 
     * and its <code>handleRequest()</code> method is called.
     */
    public ModelAndView handle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

    	if (!(handler instanceof CacheableController)) {
    		return ((Controller) handler).handleRequest(request, response);
    	}
    	
        CacheableController controller = (CacheableController) handler;
    	return handleCacheableController(request, response, controller);
    }

    /**
     * Retrieves a CacheItem from the cache by calling 
     * {@link #getCacheItem(CacheableController, HttpServletRequest)}.
     * If an item was found and it's up-to-date, the cached content is served.
     * Otherwise {@link #handleRequestAndUpdateCacheItem} is invoked.
     */
	protected ModelAndView handleCacheableController(HttpServletRequest request, 
			HttpServletResponse response, CacheableController controller) 
			throws Exception {
		
		CacheItem cacheItem = getCacheItem(controller, request);
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
		
		try {
			TaggingContext.openNestedContext(request);
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
		finally {
			cacheItem.setTags(TaggingContext.popTags(request));
		}
	}
    
    protected CacheItem getCacheItem(CacheableController controller,
    		HttpServletRequest request) {
    	
        String cacheKey = controller.getCacheKey(request);
        if (cacheKey == null) {
            log.debug("Cache key is null - Response won't be cached.");
            return null;
        }
        
        boolean encodedUrls = SessionUtils.urlsNeedEncoding(request); 
        if (encodedUrls) {
            cacheKey += ";jsessionid";
        }

        CacheItem cacheItem = cache.getItem(cacheKey);
        if (cacheItem == null) {
            log.warn("Failed to create cache item");
        }
        else if (cacheItem.isNew() || !cacheItem.exists()) {
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

        long mtime = controller.getLastModified(request, cacheItem);
        if (mtime > cacheItem.getLastModified()) {
            // Update the timestamp so that other threads won't
            // call the handleRequest() method, too. Note: For new items 
            // lastModified is set by the  CacheItem.update() method.
            cacheItem.setLastModified(System.currentTimeMillis());
            return false;
        }
       	return true;
    }
    
    
    /**
     * Delegates the call to the handler, if it implements the
     * {@link LastModified} interface. Otherwise <code>-1</code> is returned.
     */
    public long getLastModified(HttpServletRequest request, Object handler) {
        if (handler instanceof LastModified) {
            return ((LastModified) handler).getLastModified(request);
        }
        return -1L;
    }

    
    
}