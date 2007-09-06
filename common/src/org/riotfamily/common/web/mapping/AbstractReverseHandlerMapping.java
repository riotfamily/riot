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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.beans.MapWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class AbstractReverseHandlerMapping 
		extends AbstractHandlerMapping implements ReverseHandlerMapping {
	
	
	protected abstract List getPatternsForHandler(String beanName, 
			HttpServletRequest request);
	
	protected String addServletMappingIfNecessary(String path, 
			HttpServletRequest request) {
		
		return path;
	}
	
	protected AttributePattern getPatternForHandler(String handlerName, 
			HttpServletRequest request, int numberOfWildcards) {
		
		List patterns = getPatternsForHandler(handlerName, request);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		Iterator it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = (AttributePattern) it.next();
			if (p.getNumberOfWildcards() != numberOfWildcards) {
				it.remove();
			}
		}
		if (patterns.size() != 1) {
			throw new IllegalArgumentException("Exactly one mapping with "
					+ numberOfWildcards + " wildcards required for hander " 
					+ handlerName);
		}
		return (AttributePattern) patterns.get(0);
	}
	
	protected AttributePattern getPatternForHandler(String handlerName, 
			HttpServletRequest request) {
		
		List patterns = getPatternsForHandler(handlerName, request);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		if (patterns.size() > 1) {
			throw new IllegalArgumentException("Ambigious mapping - more than " 
					+ "one pattern is registered for hander " + handlerName);
		}
		return (AttributePattern) patterns.get(0);
	}
	
	public String getUrlForHandler(String handlerName, 
			HttpServletRequest request) {
		
		AttributePattern p = getPatternForHandler(handlerName, request, 0);
		if (p == null) {
			return null;
		}
		return addServletMappingIfNecessary(p.toString(), request);
	}
	
	public String getUrlForHandlerWithAttribute(String handlerName, 
			Object attribute, HttpServletRequest request) {
		
		AttributePattern p = getPatternForHandler(handlerName, request, 1);
		if (p == null) {
			return null;
		}
		String url = p.fillInAttribute(attribute);
		return addServletMappingIfNecessary(url, request);
	}
	
	public String getUrlForHandlerWithAttributes(String handlerName, 
			Object attributes, HttpServletRequest request) {
		
		if (attributes == null) {
			return getUrlForHandler(handlerName, request);
		}
		if (attributes instanceof Map) {
			return getUrlForHandlerWithMap(handlerName, 
					(Map) attributes, request); 
		}
		return getUrlForHandlerWithBean(handlerName, attributes, request);
	}
	
	protected String getUrlForHandlerWithMap(String beanName, 
			Map attributes, HttpServletRequest request) {
		
		List patterns = getPatternsForHandler(beanName, request);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		Iterator it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = (AttributePattern) it.next();
			if (p.matches(attributes)) {
				String path = p.fillInAttributes(new MapWrapper(attributes));
				return addServletMappingIfNecessary(path, request);
			}
		}
		return null;
	}
	
	protected String getUrlForHandlerWithBean(String beanName, 
			Object bean, HttpServletRequest request) {
		
		AttributePattern p = getPatternForHandler(beanName, request);
		if (p != null) {
			String path = p.fillInAttributes(new BeanWrapperImpl(bean));
			return addServletMappingIfNecessary(path, request);
		}
		return null;
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

}
