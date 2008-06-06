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
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.StringUtils;

/**
 * HandlerMapping that works like Spring's BeanNameUrlHandlerMapping and
 * can expose parts of the matched URL as request attributes.
 * <p>The handler name <code>/foo/bar/@{some}/@{value}</code> would be
 * equivalent to <code>/foo/bar/&#42;/&#42;</code>, the last two wildcards
 * would be exposed as attributes "some" and "value".
 */
public class AdvancedBeanNameHandlerMapping 
		extends AbstractReverseHandlerMapping {

	private ArrayList<Mapping> mappings = new ArrayList<Mapping>();
	
    private HashMap<String, List<AttributePattern>> patternsByBeanName = 
    		new HashMap<String, List<AttributePattern>>();
    
	private boolean stripServletMapping = true;
	
	private Object rootHandler;
	
	public final void setStripServletMapping(boolean stripServletMapping) {
		this.stripServletMapping = stripServletMapping;
	}
	
	protected final boolean isStripServletMapping() {
		return this.stripServletMapping;
	}
	
	/**
	 * Set the root handler for this handler mapping, that is,
	 * the handler to be registered for the root path ("/").
	 * <p>Default is <code>null</code>, indicating no root handler.
	 */
	public final void setRootHandler(Object rootHandler) {
		this.rootHandler = rootHandler;
	}
	
	/**
	 * Return the root handler for this handler mapping (registered for "/"),
	 * or <code>null</code> if none.
	 */
	protected final Object getRootHandler() {
		return this.rootHandler;
	}
	
    /**
	 * <strong>Copied from BeanNameUrlHandlerMapping</strong>
	 */
	public void initApplicationContext() throws ApplicationContextException {
		super.initApplicationContext();
    	String[] beanNames = getApplicationContext().getBeanDefinitionNames();
		for (String beanName : beanNames) {
			// Get all URL-like aliases ...
			String[] urls = checkForUrl(beanName);
			if (urls.length > 0) {
				ArrayList<AttributePattern> patterns = new ArrayList<AttributePattern>();
				// Create a mapping for each alias
				for (int j = 0; j < urls.length; j++) {
					String attributePattern = urls[j];
					AttributePattern p = new AttributePattern(attributePattern);
					registerHandler(p, beanName);
					patterns.add(p);
					
				}
				registerPatterns(beanName, patterns);
			}
		}
	}
	
	/**
	 * Check name and aliases of the given bean for URLs,
	 * detected by starting with "/".
	 * <p><strong>Copied from BeanNameUrlHandlerMapping</strong>
	 */
	private String[] checkForUrl(String beanName) {
		List<String> urls = new ArrayList<String>();
		if (isMapping(beanName)) {
			urls.add(beanName);
		}
		String[] aliases = getApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			if (isMapping(alias)) {
				urls.add(alias);
			}
		}
		return StringUtils.toStringArray(urls);
	}
	
	protected boolean isMapping(String beanName) {
		return beanName.startsWith("/") || beanName.startsWith("@{");	
	}
	
	/**
	 * Registers the list of patterns for the given beanName and all aliases
	 * not starting with "/".
	 */
	private void registerPatterns(String beanName, List<AttributePattern> patterns) {
		patternsByBeanName.put(beanName, patterns);
		String[] aliases = getApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			if (!isMapping(alias)) {
				patternsByBeanName.put(alias, patterns);		
			}
		}
	}
	
	private void registerHandler(AttributePattern pattern, String handlerName) {
		Object handler = handlerName;
		
		// Eagerly resolve handler if referencing singleton via name.
		if (getApplicationContext().isSingleton(handlerName)) {
			handler = getApplicationContext().getBean(handlerName);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Mapped URL path [" + pattern
					+ "] onto handler [" + handler + "]");
		}
		mappings.add(new Mapping(pattern, handlerName, handler));
	}

	protected String getLookupPath(HttpServletRequest request) {
		if (stripServletMapping) {
			return ServletUtils.getPathWithoutServletMapping(request);
		}
		else {
			return ServletUtils.getPathWithinApplication(request);
		}
	}
	
	/**
	 * Look up a handler for the given request, falling back to the default
	 * handler if no specific one is found.
	 * @param request current HTTP request
	 * @return the looked up handler instance, or the default handler
	 */
	public Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		String lookupPath = getLookupPath(request);
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up handler for [" + lookupPath + "]");
		}
		return lookupHandler(lookupPath, request);
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
		if (rootHandler != null && urlPath.equals("/")) {
			return rootHandler;
		}
		Mapping bestMatch = null;
		for (Mapping mapping : mappings) {
			if (mapping.matches(urlPath) && mapping.isMoreSpecific(bestMatch)) {
				bestMatch = mapping;
			}
		}
		if (bestMatch != null) {
			bestMatch.pattern.expose(urlPath, request);
			exposeHandlerName(bestMatch.handlerName, request);
			return bestMatch.handler;
		}
		return null;
	}
		
	protected String addServletMappingIfNecessary(String path, 
			HttpServletRequest request) {
		
		if (path != null && isStripServletMapping()) {
			return ServletUtils.addServletMapping(path, request);
		}
		return path;
	}
	
	protected List<AttributePattern> getPatternsForHandler(String beanName, UrlResolverContext context) {
		return patternsByBeanName.get(beanName);
	}
	
	private static class Mapping {
		
		private AttributePattern pattern;
		
		private String handlerName;
		
		private Object handler;

		public Mapping(AttributePattern pattern, String handlerName, Object handler) {
			this.pattern = pattern;
			this.handlerName = handlerName;
			this.handler = handler;
		}
	
		private boolean matches(String path) {
			return pattern.matches(path);
		}
		
		private boolean isMoreSpecific(Mapping other) {
			if (other == null) {
				return true;
			}
			return pattern.isMoreSpecific(other.pattern);
		}
	}
	
}
