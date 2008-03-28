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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.beans.MapWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * Abstract base class for mappings that support reverse look-ups.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class AbstractReverseHandlerMapping 
		extends AbstractHandlerMapping implements ReverseHandlerMapping {
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param prefix Optional prefix to sort out ambiguities
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request The current request
	 */
	public String getUrlForHandler(String handlerName, String prefix, 
			Object attributes, HttpServletRequest request) {
		
		Map defaults = getDefaults(request);
		return getUrlForHandler(handlerName, prefix, attributes, defaults, request);
	}
	
	/**
	 * Returns a Map of default values that are used to build URLs. The default
	 * implementation return <code>null</code>.
	 */
	protected Map getDefaults(HttpServletRequest request) {
		return null;
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param prefix Optional prefix to sort out ambiguities
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param defaults Optional Map of default values that are used when no
	 * 		  attribute value was provided for a certain wildcard.
	 * @param request The current request
	 */
	protected String getUrlForHandler(String handlerName, String prefix, 
			Object attributes, Map defaults, HttpServletRequest request) {
		
		if (attributes == null) {
			return getUrlForHandler(handlerName, defaults, prefix, request);
		}
		if (attributes instanceof Map) {
			Map map;
			if (defaults != null) {
				map = new HashMap();
				map.putAll(defaults);
				map.putAll((Map) attributes);
			}
			else {
				map = (Map) attributes;
			}
			return getUrlForHandlerWithMap(handlerName, map, prefix, request); 
		}
		if (ClassUtils.isAssignable(String.class, attributes.getClass()) ||
				ClassUtils.isPrimitiveOrWrapper(attributes.getClass())) {
			
			return getUrlForHandlerWithAttribute(handlerName, attributes, 
					defaults, prefix, request);
		}
		return getUrlForHandlerWithBean(handlerName, attributes, defaults, 
				prefix, request);
	}

	/**
	 * Returns the URL for a handler without any extra wildcards. If the pattern
	 * contains wildcards all of them must be present in the defaults map.
	 */
	private String getUrlForHandler(String handlerName, Map defaults, 
			String prefix, HttpServletRequest request) {
		
		Set attributeNames = null;
		if (defaults != null) {
			attributeNames = defaults.keySet();
		}
		AttributePattern p = getPatternForHandler(handlerName, prefix, request, 
				attributeNames, 0);
		
		if (p == null) {
			return null;
		}
		return addServletMappingIfNecessary(p.toString(), request);
	}
	
	/**
	 * Returns the URL for a handler with one custom wildcard. If the pattern
	 * contains wildcards all of them must be present in the defaults map.
	 */
	private String getUrlForHandlerWithAttribute(String handlerName, 
			Object attribute, Map defaults, String prefix, 
			HttpServletRequest request) {
		
		Set attributeNames = null;
		if (defaults != null) {
			attributeNames = defaults.keySet();
		}
		AttributePattern p = getPatternForHandler(handlerName, prefix, request, 
				attributeNames, 1);
		
		if (p == null) {
			return null;
		}
		String url = p.fillInAttribute(attribute, defaults);
		return addServletMappingIfNecessary(url, request);
	}
	
	/**
	 * Returns the URL for a handler that is mapped with multiple wildcards.
	 * The wildcard replacements are taken from the given Map.
	 */
	private String getUrlForHandlerWithMap(String beanName, Map attributes,
			String prefix, HttpServletRequest request) {
		
		List patterns = getPatternsForHandler(beanName, prefix, request);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		Iterator it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = (AttributePattern) it.next();
			if (p.canFillIn(attributes.keySet(), 0)) {
				String path = p.fillInAttributes(new MapWrapper(attributes), null);
				return addServletMappingIfNecessary(path, request);
			}
		}
		return null;
	}
	
	/**
	 * Returns the URL for a handler that is mapped with multiple wildcards.
	 * The wildcard replacements are taken from the given bean or the map
	 * of defaults, in case the bean has no matching property or the property
	 * value is <code>null</code>.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	private String getUrlForHandlerWithBean(String beanName, Object bean,
			Map defaults, String prefix, HttpServletRequest request) {
		
		AttributePattern p = getPatternForHandler(beanName, prefix, request);
		if (p != null) {
			String path = p.fillInAttributes(new BeanWrapperImpl(bean), defaults);
			return addServletMappingIfNecessary(path, request);
		}
		return null;
	}
	
	/**
	 * Returns all {@link AttributePattern patterns} for the handler with the
	 * specified name that start with the given prefix.
	 *  
	 * @param handlerName Name of the handler
	 * @param prefix Optional prefix to narrow the the result
	 * @param request The current request
	 */
	protected List getPatternsForHandler(String handlerName, String prefix, 
			HttpServletRequest request) {
		
		List patterns = getPatternsForHandler(handlerName, request);
		if (patterns == null || patterns.isEmpty() 
				|| prefix == null || prefix.length() == 0) {
			
			return patterns;
		}
		ArrayList matchingPatterns = new ArrayList();
		Iterator it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = (AttributePattern) it.next();
			if (p.startsWith(prefix)) {
				matchingPatterns.add(p);
			}
		}
		return matchingPatterns;
	}
	
	/**
	 * Subclasses must implement this method and return all 
	 * {@link AttributePattern patterns} for the handler with the specified
	 * name.
	 */
	protected abstract List getPatternsForHandler(String beanName, 
			HttpServletRequest request);
	
	protected String addServletMappingIfNecessary(String path, 
			HttpServletRequest request) {
		
		return path;
	}
	
	/**
	 * Returns the pattern for the handler with the given name that contains
	 * all the given wildcard names and exactly 
	 * <code>attributeNames.size() + anonymousWildcards</code> wildcards
	 * in total.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	protected AttributePattern getPatternForHandler(String handlerName, 
			String prefix, HttpServletRequest request, Set attributeNames, 
			int anonymousWildcards) {
		
		List patterns = getPatternsForHandler(handlerName, prefix, request);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		Iterator it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = (AttributePattern) it.next();
			if (!p.canFillIn(attributeNames, anonymousWildcards)) {
				it.remove();
			}
		}
		if (patterns.size() != 1) {
			throw new IllegalArgumentException("Exactly one mapping with "
					+ anonymousWildcards + " anonymous wildcards required "
					+ "for hander "	+ handlerName);
		}
		return (AttributePattern) patterns.get(0);
	}
	
	/**
	 * Returns the pattern for the handler with the given name.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	protected AttributePattern getPatternForHandler(String handlerName, 
			String prefix, HttpServletRequest request) {
		
		List patterns = getPatternsForHandler(handlerName, prefix, request);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		if (patterns.size() != 1) {
			throw new IllegalArgumentException("Ambigious mapping - more than " 
					+ "one pattern is registered for hander " + handlerName);
		}
		return (AttributePattern) patterns.get(0);
	}
	
	/**
	 * Exposes the name of the matched handler as request attribute, unless
	 * the attribute is already present.
	 * @see #TOP_LEVEL_HANDLER_NAME_ATTRIBUTE
	 */
	protected void exposeHandlerName(String beanName, HttpServletRequest request) {
		if (request.getAttribute(TOP_LEVEL_HANDLER_NAME_ATTRIBUTE) == null) {
			request.setAttribute(TOP_LEVEL_HANDLER_NAME_ATTRIBUTE, beanName);
		}
	}

	public static Map getWildcardAttributes(HttpServletRequest request) {
		return (Map) request.getAttribute(AttributePattern.EXPOSED_ATTRIBUTES);
	}
}
