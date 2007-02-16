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
package org.riotfamily.common.web.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

/**
 * HandlerMapping that works like Spring's BeanNameUrlHandlerMapping and
 * can expose parts of the matched URL as request attributes. 
 * <p>The handler name <code>/foo/bar/@{some}/@{value}</code> would be 
 * equivalent to <code>/foo/bar/&#42;/&#42;</code>, the last two wildcards
 * would be exposed as attributes "some" and "value".
 */
public class AdvancedBeanNameHandlerMapping extends WebApplicationObjectSupport
		implements HandlerMapping, UrlMapping, Ordered {
    
	private static final Pattern ATTRIBUTE_NAME_PATTERN = 
			Pattern.compile("@\\{(.*?)(\\*?)\\}");
	
	private static final Pattern STAR_PATTERN = 
			Pattern.compile("\\\\\\*");
	
	private static final Pattern DOUBLE_STAR_PATTERN = 
			Pattern.compile("\\\\\\*\\\\\\*");
	
	private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

	private Object defaultHandler;

	private HandlerInterceptor[] interceptors;

	private PathMatcher pathMatcher = new AntPathMatcher();

	private final Map handlerMap = new HashMap();
	    
    private HashMap patternsByAntPath = new HashMap();
    
    private HashMap patternsByBeanName = new HashMap();

    private String servletMappingPrefix;
    
    private String servletMappingSuffix;
    
    public void setServletMappingPrefix(String prefix) {
		this.servletMappingPrefix = prefix;
	}

    public void setServletMappingSuffix(String suffix) {
        this.servletMappingSuffix = suffix;
    }
    
    /**
	 * @deprecated 
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
	}

	/**
	 * @deprecated
	 */
	public void setUrlDecode(boolean urlDecode) {
	}

	public final void setOrder(int order) {
		this.order = order;
	}

	public final int getOrder() {
		return order;
	}

	/**
	 * Set the default handler for this handler mapping.
	 * This handler will be returned if no specific mapping was found.
	 * <p>Default is <code>null</code>, indicating no default handler.
	 * @param defaultHandler default handler instance, or <code>null</code> if none
	 */
	public final void setDefaultHandler(Object defaultHandler) {
		this.defaultHandler = defaultHandler;
		if (logger.isInfoEnabled()) {
			logger.info("Default mapping to handler [" + this.defaultHandler + "]");
		}
	}

	/**
	 * Return the default handler for this handler mapping.
	 * @return the default handler instance, or <code>null</code> if none
	 */
	protected final Object getDefaultHandler() {
		return defaultHandler;
	}

	/**
	 * Set the interceptors to apply for all handlers mapped by this handler mapping.
	 * <p>Supported interceptor types are HandlerInterceptor and WebRequestInterceptor.
	 * @param interceptors array of handler interceptors, or <code>null</code> if none
	 * @see #adaptInterceptor
	 * @see org.springframework.web.servlet.HandlerInterceptor
	 * @see org.springframework.web.context.request.WebRequestInterceptor
	 */
	public final void setInterceptors(Object[] interceptors) {
		if (interceptors != null) {
			this.interceptors = new HandlerInterceptor[interceptors.length];
			for (int i = 0; i < interceptors.length; i++) {
				this.interceptors[i] = adaptInterceptor(interceptors[i]);
			}
		}
		else {
			this.interceptors = null;
		}
	}

	/**
	 * Adapt the given interceptor object to the HandlerInterceptor interface.
	 * <p>Supported interceptor types are HandlerInterceptor and WebRequestInterceptor.
	 * Can be overridden in subclasses.
	 * @param interceptor the specified interceptor object
	 * @return the interceptor wrapped as HandlerInterceptor
	 * @see org.springframework.web.servlet.HandlerInterceptor
	 * @see org.springframework.web.context.request.WebRequestInterceptor
	 */
	protected HandlerInterceptor adaptInterceptor(Object interceptor) {
		if (interceptor instanceof HandlerInterceptor) {
			return (HandlerInterceptor) interceptor;
		}
		else if (interceptor instanceof WebRequestInterceptor) {
			return new WebRequestHandlerInterceptorAdapter((WebRequestInterceptor) interceptor);
		}
		else {
			throw new IllegalArgumentException("Interceptor type not supported: " + interceptor);
		}
	}


	/**
	 * Look up a handler for the given request, falling back to the default
	 * handler if no specific one is found.
	 * @param request current HTTP request
	 * @return the looked up handler instance, or the default handler
	 * @see #getHandlerInternal
	 */
	public final HandlerExecutionChain getHandler(HttpServletRequest request) 
			throws Exception {
		
		String lookupPath = ServletUtils.getPathWithoutServletMapping(request);
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up handler for [" + lookupPath + "]");
		}
		
		Object handler = lookupHandler(lookupPath, request);
		
		if (handler == null) {
			handler = this.defaultHandler;
		}
		if (handler == null) {
			return null;
		}
		
		// bean name of resolved handler?
		if (handler instanceof String) {
			String handlerName = (String) handler;
			handler = getApplicationContext().getBean(handlerName);
			if (handler instanceof UrlMappingAware) {
				((UrlMappingAware) handler).setUrlMapping(this);
			}
		}
		return new HandlerExecutionChain(handler, this.interceptors);
	}
	
	/**
	 * Look up a handler instance for the given URL path.
	 * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
	 * and various Ant-style pattern matches, e.g. a registered "/t*" matches
	 * both "/test" and "/team". For details, see the AntPathMatcher class.
	 * <p>Looks for the most exact pattern, where most exact is defined as
	 * the longest path pattern.
	 * <p><strong>Copied from AbstractUrlHandlerMapping</strong>
	 * @param urlPath URL the bean is mapped to
	 * @return the associated handler instance, or <code>null</code> if not found
	 * @see org.springframework.util.AntPathMatcher
	 */
	protected Object lookupHandler(String urlPath, HttpServletRequest request) {
		// direct match?
		Object handler = this.handlerMap.get(urlPath);
		if (handler == null) {
			// pattern match?
			String bestPathMatch = null;
			for (Iterator it = this.handlerMap.keySet().iterator(); it.hasNext();) {
				String registeredPath = (String) it.next();
				if (this.pathMatcher.match(registeredPath, urlPath) &&
						(bestPathMatch == null || bestPathMatch.length() <= registeredPath.length())) {
					
					handler = this.handlerMap.get(registeredPath);
					bestPathMatch = registeredPath;
				}
			}
			if (handler != null) {
				exposeAttributes(bestPathMatch, urlPath, request);
				exposePathWithinMapping(this.pathMatcher.extractPathWithinPattern(bestPathMatch, urlPath), request);
			}
		}
		else {
			exposePathWithinMapping(urlPath, request);
		}
		return handler;
	}

	/**
	 * Register the given handler instance for the given URL path.
	 * <p><strong>Copied from AbstractUrlHandlerMapping</strong>
	 * @param urlPath URL the bean is mapped to
	 * @param handler the handler instance
	 * @throws BeansException if the handler couldn't be registered
	 */
	protected void registerHandler(String urlPath, Object handler) throws BeansException {
		Object mappedHandler = this.handlerMap.get(urlPath);
		if (mappedHandler != null) {
			throw new ApplicationContextException(
					"Cannot map handler [" + handler + "] to URL path [" + urlPath +
					"]: there's already handler [" + mappedHandler + "] mapped");
		}

		// Eagerly resolve handler if referencing singleton via name.
		if (handler instanceof String) {
			String handlerName = (String) handler;
			if (getApplicationContext().isSingleton(handlerName)) {
				handler = getApplicationContext().getBean(handlerName);
			}
		}
		if (handler instanceof UrlMappingAware) {
			((UrlMappingAware) handler).setUrlMapping(this);
		}

		if (urlPath.equals("/*")) {
			setDefaultHandler(handler);
		}
		else {
			this.handlerMap.put(urlPath, handler);
			if (logger.isDebugEnabled()) {
				logger.debug("Mapped URL path [" + urlPath 
						+ "] onto handler [" + handler + "]");
			}
		}
	}

	/**
	 * <strong>Copied from AbstractUrlHandlerMapping</strong>
	 */
	protected void exposePathWithinMapping(String pathWithinMapping, HttpServletRequest request) {
		request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping);
	}
    
	/**
	 * <strong>Copied from BeanNameUrlHandlerMapping</strong>
	 */
	public void initApplicationContext() throws ApplicationContextException {
    	String[] beanNames = getApplicationContext().getBeanDefinitionNames();

		// Take any bean name or alias that begins with a slash.
		for (int i = 0; i < beanNames.length; i++) {
			String[] urls = checkForUrl(beanNames[i]);
			if (urls.length > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("Found URL mapping [" + beanNames[i] + "]");
				}
				ArrayList patterns = new ArrayList();
				// Create a mapping to each part of the path.
				for (int j = 0; j < urls.length; j++) {
					String attributePattern = urls[j];
					String antPattern = convertToAntPattern(attributePattern);
					registerHandler(antPattern, beanNames[i]);
					AttributePattern p = new AttributePattern(attributePattern);
					patternsByAntPath.put(antPattern, p);
					patterns.add(p);
				}
				patternsByBeanName.put(beanNames[i], patterns);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Rejected bean name '" + beanNames[i] + "'");
				}
			}
		}
	}
	
	/**
	 * Check name and aliases of the given bean for URLs,
	 * detected by starting with "/".
	 * <p><strong>Copied from BeanNameUrlHandlerMapping</strong>
	 */
	protected String[] checkForUrl(String beanName) {
		List urls = new ArrayList();
		if (beanName.startsWith("/")) {
			urls.add(beanName);
		}
		String[] aliases = getApplicationContext().getAliases(beanName);
		for (int j = 0; j < aliases.length; j++) {
			if (aliases[j].startsWith("/")) {
				urls.add(aliases[j]);
			}
		}
		return StringUtils.toStringArray(urls);
	}
    
	protected String convertToAntPattern(String urlPattern) {
		return ATTRIBUTE_NAME_PATTERN.matcher(urlPattern).replaceAll("*$2");
	}
		
	protected void exposeAttributes(String antPattern, String urlPath, 
			HttpServletRequest request) {
		
		AttributePattern pattern = (AttributePattern) patternsByAntPath.get(antPattern);
		pattern.expose(urlPath, request);
		
	}
	
	public String getUrl(String beanName, Map attributes) {
		List patterns = (List) patternsByBeanName.get(beanName);
		Iterator it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = (AttributePattern) it.next();
			if (p.matches(attributes)) {
				StringBuffer url = p.buildUrl(attributes);
				if (servletMappingPrefix != null) {
					url.insert(0, servletMappingPrefix);
				}
				else if (servletMappingSuffix != null) {
					url.append(servletMappingSuffix);
				}
				return url.toString();
			}
		}
		//TODO Throw RuntimeException ...
		return null;
	}
	
	private static class AttributePattern {
		
		private String attributePattern;
		
		private Pattern pattern;
		
		private ArrayList attributeNames;
		
		public AttributePattern(String attributePattern) {
			this.attributePattern = attributePattern;
			
			attributeNames = new ArrayList();
			Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
			while (m.find()) {
				attributeNames.add(m.group(1));
			}
			
			pattern = Pattern.compile(convertAttributePatternToRegex(attributePattern));
		}
		
		// Example pattern: /resources/*/@{resource*}
		private String convertAttributePatternToRegex(final String antPattern) {
			String regex = FormatUtils.escapeChars(antPattern, "()", '\\'); // ... just in case 
			regex = ATTRIBUTE_NAME_PATTERN.matcher(antPattern).replaceAll("(*$2)"); // /resources/*/(**)
			regex = "^" + FormatUtils.escapeChars(regex, ".+*?{^$", '\\') + "$"; // ^/resources/\*/(\*\*)$ 
			regex = DOUBLE_STAR_PATTERN.matcher(regex).replaceAll(".*?"); // ^/resources/\*/(.*?)$
			regex = STAR_PATTERN.matcher(regex).replaceAll("[^/]*"); // ^/resources/[^/]*/.*?$
			return regex;
		}
				
		public void expose(String urlPath, HttpServletRequest request) {
			Matcher m = pattern.matcher(urlPath);
			Assert.isTrue(m.matches());
			for (int i = 0; i < attributeNames.size(); i++) {
				request.setAttribute((String) attributeNames.get(i), m.group(i + 1));
			}
		}
		
		public boolean matches(Map attributes) {
			if (attributes != null) {
				Collection names = attributes.keySet();
				return names.size() == attributeNames.size() &&
					attributeNames.containsAll(names);
			}
			else {
				return attributeNames.isEmpty();
			}
		}
		
		public StringBuffer buildUrl(Map attributes) {
			StringBuffer url = new StringBuffer();
			Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
			while (m.find()) {
				String name = m.group(1);
				Object value = attributes.get(name);
				m.appendReplacement(url, value != null ? value.toString() : "");
			}
			m.appendTail(url);
			return url;
		}
	}
	
}
