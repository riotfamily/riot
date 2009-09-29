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
package org.riotfamily.common.web.cache.annotation;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.CronExpression;
import org.riotfamily.cachius.CacheContext;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.http.AbstractHttpHandler;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.cache.CacheKeyAugmentor;
import org.riotfamily.common.web.mvc.view.ViewResolverHelper;
import org.riotfamily.common.web.support.ServletUtils;
import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class CacheAnnotationHandlerAdapter extends AnnotationMethodHandlerAdapter
		implements Ordered {

	private CacheService cacheService;
	
	private CacheKeyAugmentor cacheKeyAugmentor;
	
	private ViewResolverHelper viewResolverHelper;
	
	private int order = 0;
	
	
	public CacheAnnotationHandlerAdapter(CacheService cacheService,
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
	
	@Override
	protected void initApplicationContext() throws BeansException {
		viewResolverHelper = new ViewResolverHelper(getApplicationContext());
	}
        
	@Override
	public ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		if (handler.getClass().isAnnotationPresent(Cache.class)) {
			cacheService.handle(new AnnotationCacheHandler(request, response, handler));
			return null;
		}
		else {
			return doHandle(request, response, handler);
		}
	}

	public ModelAndView doHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		return super.handle(request, response, handler);
	}
	
	
	private class AnnotationCacheHandler extends AbstractHttpHandler {

		private Object handler;
		
		private Cache annotation;
		
		public AnnotationCacheHandler(HttpServletRequest request, 
				HttpServletResponse response, Object handler) {
			
			super(request, response);
			this.handler = handler;
			this.annotation = handler.getClass().getAnnotation(Cache.class);
		}
		
		@Override
		public String getCacheRegion() {
			return annotation.region();
		}
		
		@Override
		protected boolean isCompressible() {
			return annotation.gzip();
		}

		@Override
		public String getCacheKey() {
			StringBuilder key = new StringBuilder();
			key.append(getRequest().getScheme());
			key.append("://");
			key.append(getRequest().getServerName());
			key.append(ServletUtils.getRequestUri(getRequest()));
			ServletUtils.appendRequestParameters(key, getRequest());
			cacheKeyAugmentor.augmentCacheKey(key, getRequest());
			return key.toString();
		}
		
		@Override
		protected void handleRequest(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			applyContextSettings();
			ModelAndView mv = doHandle(request, response, handler);
			if (mv != null) {
		    	View view = viewResolverHelper.resolveView(getRequest(), mv);
		    	view.render(mv.getModel(), getRequest(), response);
		    }
		}
		
		private void applyContextSettings() throws ParseException {
			if (annotation.serveStaleOnError()) {
				CacheContext.serveStaleOnError();
			}
			if (annotation.serveStaleUntilExpired()) {
				CacheContext.serveStaleUntilExpired();
			}
			if (annotation.serveStaleWhileRevalidate()) {
				CacheContext.serveStaleWhileRevalidate();
			}
			
			Long expireIn = null;
			if (!annotation.ttl().isEmpty()) {
				expireIn = FormatUtils.parseMillis(annotation.ttl());	
			}
			if (!annotation.cron().isEmpty()) {
				CronExpression cron = new CronExpression(annotation.cron());
				long delta = cron.getNextValidTimeAfter(new Date()).getTime() 
						- System.currentTimeMillis();
				
				if (expireIn == null || delta < expireIn) {
					expireIn = delta;
				}
			}
			
			if (expireIn != null) {
				CacheContext.expireIn(expireIn);
			}
		}
		
	}
	
}
