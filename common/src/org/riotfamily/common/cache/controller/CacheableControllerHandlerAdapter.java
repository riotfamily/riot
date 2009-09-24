/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.cache.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.cache.CacheKeyAugmentor;
import org.riotfamily.common.view.ViewResolverHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.util.WebUtils;

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
 * to support both cacheable and non-cacheable controllers you have to add
 * a {@link org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter}
 * to your context manually.
 * </p>
 * @author Felix Gnass
 */
public class CacheableControllerHandlerAdapter implements HandlerAdapter,
		ApplicationContextAware, Ordered {

    private CacheService cacheService;

    private ViewResolverHelper viewResolverHelper;

    private CacheKeyAugmentor cacheKeyAugmentor;
    
    private int order = 0;

    public CacheableControllerHandlerAdapter(CacheService cacheService) {
		this.cacheService = cacheService;
	}
    
    public CacheableControllerHandlerAdapter(CacheService cacheService, 
    		CacheKeyAugmentor cacheKeyAugmentor) {
    	
		this.cacheService = cacheService;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
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

	public void setCacheKeyAugmentor(CacheKeyAugmentor cacheKeyAugmentor) {
		this.cacheKeyAugmentor = cacheKeyAugmentor;
	}
	
    /**
     * Returns <code>true</code> if handler implements the
     * {@link CacheableController} interface.
     */
    public boolean supports(Object handler) {
        return handler instanceof CacheableController;
    }

    
    /**
     */
    public final ModelAndView handle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

    	cacheService.handle(new ControllerCacheHandler(
    			request, response, (CacheableController) handler, 
    			cacheKeyAugmentor, viewResolverHelper));
    	
        return null;
	}
	

    /**
     * Returns the lastModified date as reported by the CacheService.
     */
    public final long getLastModified(HttpServletRequest request, Object handler) {
    	if (handler instanceof LastModified && !WebUtils.isIncludeRequest(request)) {
	    	return cacheService.getLastModified(new ControllerCacheHandler(
	    			request, null, (CacheableController) handler, 
	    			cacheKeyAugmentor, viewResolverHelper));
    	}
        return -1L;
    }

    protected final String getCacheKey(CacheableController controller, 
    		HttpServletRequest request) {
    	
    	return controller.getCacheKey(request);
    }
}