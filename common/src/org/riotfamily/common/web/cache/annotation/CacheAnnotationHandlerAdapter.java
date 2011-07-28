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


import java.io.OutputStream;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.Principal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.quartz.CronExpression;
import org.riotfamily.cachius.CacheContext;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.http.AbstractHttpHandler;
import org.riotfamily.common.util.ExceptionUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.cache.CacheKeyAugmentor;
import org.riotfamily.common.web.mvc.view.ViewResolverHelper;
import org.riotfamily.common.web.support.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * Subclass of the {@link AnnotationMethodHandlerAdapter} that supports the
 * {@link Cache} annotation.
 * <p>
 * By default, the cacheKey is automatically constructed from the URL and the
 * arguments passed to the handler method:
 * <p>
 * <code><i>&lt;request-url&gt;</i><b>#</b><i>&lt;method-name&gt;</i><b>@RequestMapping(</b><i>&lt;mapping&gt;</i><b>) {</b><i>&lt;method-args&gt;</i><b>}</b></code>
 * <p>
 * ... where <i>&lt;method-args&gt;</i> is a list of the String-representations 
 * of all {@link #isSupportedArgument(Annotation[], Class) supported} handlerMethod arguments (separated by semicolons).
 * <p>
 * In case the argument list contains an unsupported argument, you have to provide a method
 * that manually constructs the <i>&lt;method-args&gt;</i> part. To do so, implement
 * a method with the following signature:
 * <p>
 * <code><b>public</b> <i>&lt;? extends CharSequence&gt;</i> getCacheKeyFor<i>&lt;method-name&gt;</i><b>(</b><i>&lt;method-args&gt;</i><b>)</b></code>
 * <p>
 * ... where the argument-list must be exactly the same as for the handler-method.
 * <p>
 * The same mechanism can be used to provide a last-modified date for a handler-method:
 * <p>
 * <code><b>public</b> long getLastModifiedFor<i>&lt;method-name&gt;</i><b>(</b><i>&lt;method-args&gt;</i><b>)</b></code>
 */
@SuppressWarnings("unchecked")
public class CacheAnnotationHandlerAdapter extends AnnotationMethodHandlerAdapter
		implements Ordered, InitializingBean {

	Logger log = LoggerFactory.getLogger(CacheAnnotationHandlerAdapter.class);
	
	private CacheService cacheService;
	
	CacheKeyAugmentor cacheKeyAugmentor;
	
	ViewResolverHelper viewResolverHelper;
	
	private int order = 0;
	
	private Set<Class<? extends Annotation>> ignoredAnnotations;
	
	private Set<Class<? extends Annotation>> supportedAnnotations;
	
	private Set<Class<?>> ignoredTypes;
	
	private Set<Class<?>> supportedTypes;
	
	public CacheAnnotationHandlerAdapter(CacheService cacheService,
			CacheKeyAugmentor cacheKeyAugmentor) {

		this.cacheService = cacheService;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
	}
	
	/**
	 * Returns the order in which this HandlerAdapter is processed.
	 */
	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Set the order in which this HandlerAdapter is processed.
	 */
	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	public void setSupportedAnnotations(Set<Class<? extends Annotation>> supportedAnnotations) {
		this.supportedAnnotations = supportedAnnotations;
	}
	
	public void setSupportedTypes(Set<Class<?>> supportedTypes) {
		this.supportedTypes = supportedTypes;
	}
	
	public void setIgnoredAnnotations(Set<Class<? extends Annotation>> ignoredAnnotations) {
		this.ignoredAnnotations = ignoredAnnotations;
	}

	public void setIgnoredTypes(Set<Class<?>> ignoredTypes) {
		this.ignoredTypes = ignoredTypes;
	}

	public void afterPropertiesSet() throws Exception {
		if (ignoredAnnotations == null) {
			ignoredAnnotations = Generics.newHashSet();
		}
		if (supportedAnnotations == null) {
			supportedAnnotations = Generics.newHashSet();
		}
		if (ignoredTypes == null) {
			ignoredTypes = Generics.newHashSet();
		}
		if (supportedTypes == null) {
			supportedTypes = Generics.newHashSet();
		}
		
		ignoredAnnotations.add(PathVariable.class);
		
		supportedAnnotations.addAll(Arrays.asList(
				RequestParam.class, RequestHeader.class, CookieValue.class));
	
		ignoredTypes.addAll(Arrays.asList(
				Model.class, ModelMap.class, Map.class, Errors.class,
				BindingResult.class, OutputStream.class, Writer.class,
				HttpServletResponse.class));
		
		supportedTypes.addAll(Arrays.asList(
				Locale.class, Principal.class));
	}

	@Override
	protected void initApplicationContext() throws BeansException {
		viewResolverHelper = new ViewResolverHelper(getApplicationContext());
	}
        
	@Override
	public ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		cacheService.handle(new AnnotationCacheHandler(request, response, handler));
		return null;
	}

	public ModelAndView doHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		return super.handle(request, response, handler);
	}
	
	protected String getDefaultCacheKeyPrefix(HttpServletRequest request, Method handlerMethod) {
		return ServletUtils.getOriginatingRequestUrl(request)
			.append('#').append(handlerMethod.getName())
			.append('@').append(StringUtils.unqualify(handlerMethod.getAnnotation(RequestMapping.class).toString()))
			.toString();
	}
	
	protected CharSequence getDefaultMethodLevelCacheKey(Method handlerMethod, Object[] args) {
		StringBuilder key = new StringBuilder();
		key.append(" {");
		Class<?>[] types = handlerMethod.getParameterTypes();
		Annotation[][] ann = handlerMethod.getParameterAnnotations();
		for (int i = 0; i < types.length; i++) {
			if (isSupportedArgument(ann[i], types[i])) {
				key.append(String.valueOf(args[i])).append(';');
			}
		}
		key.append('}');
		return key;
	}
	
	protected boolean isSupportedArgument(Annotation[] annotations, Class<?> type) {
		return !containsIgnoredAnnotation(annotations) && (containsSupportedAnnotation(annotations) || isSupportedType(type));
	}
	
	private boolean containsSupportedAnnotation(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (isSupportedAnnotation(annotation)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean containsIgnoredAnnotation(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (isIgnoredAnnotation(annotation)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isSupportedAnnotation(Annotation annotation) {
		if (supportedAnnotations.contains(annotation.annotationType())) {
			return true;
		}
		else if (!isIgnoredAnnotation(annotation)) {
			throw new IllegalStateException("Unsupported annotation: " + annotation); 
		}
		return false;
	}
	
	private boolean isIgnoredAnnotation(Annotation annotation) {
		return ignoredAnnotations.contains(annotation.annotationType());
	}
	
	private boolean isSupportedType(Class<?> type) {
		if (supportedTypes.contains(type)) {
			return true;
		}
		if (!ignoredTypes.contains(type)) {
			throw new IllegalStateException("Unsupported parameter type: " + type);
		}
		return false;
	}
	
	// ----------------------------------------------------------------------
	
	private class AnnotationCacheHandler extends AbstractHttpHandler {

		private Object handler;
		
		private Method handlerMethod;
		
		private Object[] args;
		
		private Cache annotation;
		
		private Method lastModifiedMethod;
		
		public AnnotationCacheHandler(HttpServletRequest request, 
				HttpServletResponse response, Object handler) {
			
			super(request, response);
			this.handler = handler;
			init();
		}
		
		private void init() {
			ProxyFactory proxyFactory = new ProxyFactory(handler);
			proxyFactory.setProxyTargetClass(true);
			HandlerMethodInterceptor interceptor = new HandlerMethodInterceptor();
			proxyFactory.addAdvice(interceptor);
			try {
				invokeHandlerMethod(getRequest(), getResponse(), proxyFactory.getProxy());
				MethodInvocation invocation = interceptor.getInvocation();
				handlerMethod = invocation.getMethod();
				args = invocation.getArguments();
				annotation = handlerMethod.getAnnotation(Cache.class);
				String name = "getLastModifiedFor" + StringUtils.capitalize(handlerMethod.getName());
				lastModifiedMethod = ReflectionUtils.findMethod(handler.getClass(), name, handlerMethod.getParameterTypes());
			}
			catch (Exception e) {
				throw ExceptionUtils.wrapReflectionException(e);
			}
		}
		
		@Override
		public String getCacheRegion() {
			CacheRegion region = handler.getClass().getAnnotation(CacheRegion.class);
			return region != null ? region.value() : null;
		}
		
		@Override
		public long getLastModified() {
			try {
				if (lastModifiedMethod != null) {
					Assert.isAssignable(Long.TYPE, lastModifiedMethod.getReturnType());
					return (Long) lastModifiedMethod.invoke(handler, args);
				}
				return System.currentTimeMillis();
			}
			catch (Exception e) {
				throw ExceptionUtils.wrapReflectionException(e);
			}
		}
		
		@Override
		protected boolean isCompressible() {
			return annotation.gzip();
		}
		
		@Override
		public String getCacheKey() {
			if (annotation == null) {
				return null;
			}
			try {
				Method method = ReflectionUtils.findMethod(handler.getClass(), 
						"getCacheKey", HttpServletRequest.class, Method.class);
				
				CharSequence prefix;
				if (method != null) {
					Assert.isAssignable(CharSequence.class, method.getReturnType());
					prefix = (CharSequence) method.invoke(handler, getRequest(), handlerMethod);
				}
				else {
					prefix = getDefaultCacheKeyPrefix(getRequest(), handlerMethod);
				}
				if (prefix == null) {
					return null;
				}

				CharSequence suffix;
				String name = "getCacheKeyFor" + StringUtils.capitalize(handlerMethod.getName());
				method = ReflectionUtils.findMethod(handler.getClass(), name, handlerMethod.getParameterTypes());
				if (method != null) {
					Assert.isAssignable(CharSequence.class, method.getReturnType());
					suffix = (CharSequence) method.invoke(handler, args);
				}
				else {
					suffix = getMethodLevelCacheKey();
				}
				
				if (suffix == null) {
					return null;
				}

				StringBuilder key = new StringBuilder(prefix).append(suffix);
				if (cacheKeyAugmentor != null) {
					cacheKeyAugmentor.augmentCacheKey(key, getRequest());
				}
				return key.toString();
			}
			catch (Exception e) {
				throw ExceptionUtils.wrapReflectionException(e);
			}
		}
		
		private CharSequence getMethodLevelCacheKey() {
			String name = "getCacheKeyFor" + StringUtils.capitalize(handlerMethod.getName());
			Method method = ReflectionUtils.findMethod(handler.getClass(), name, handlerMethod.getParameterTypes());
			if (method != null) {
				Assert.isAssignable(CharSequence.class, method.getReturnType());
				return (CharSequence) ReflectionUtils.invokeMethod(method, handler, args);
			}
			return getDefaultMethodLevelCacheKey(handlerMethod, args);
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
			if (annotation != null) {
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
				if (StringUtils.hasText(annotation.ttl())) {
					expireIn = FormatUtils.parseMillis(annotation.ttl());	
				}
				if (StringUtils.hasText(annotation.cron())) {
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
				else if (lastModifiedMethod != null) {
					CacheContext.expireIn(0);
				}
			}
		}
	}
	
	private static class HandlerMethodInterceptor implements MethodInterceptor {
		
		private MethodInvocation invocation;
		
		public Object invoke(MethodInvocation invocation) throws Throwable {
			this.invocation = invocation;
			return null;
		}
		
		public MethodInvocation getInvocation() {
			return invocation;
		}
	}
	
}
