package org.riotfamily.common.mapping;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.beans.property.MapWrapper;
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
	
	private String servletPrefix = "";
	
	private String servletSufix = "";

	public void setServletPrefix(String servletPrefix) {
		if (servletPrefix == null) {
			servletPrefix = "";
		}
		this.servletPrefix = servletPrefix;
	}
	
	public void setServletSufix(String servletSufix) {
		if (servletSufix == null) {
			servletSufix = "";
		}
		this.servletSufix = servletSufix;
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 */
	@SuppressWarnings("unchecked")
	public String getUrlForHandler(String handlerName, Object attributes) {
		
		Map<String, Object> defaults = getDefaults();
		String url = null;
		if (attributes == null) {
			url = getUrlForHandlerWithoutAttributes(handlerName, defaults);
		}
		else if (attributes instanceof Map) {
			url = getUrlForHandlerWithMap(handlerName,
					(Map<String, Object>) attributes, defaults); 
		}
		else if (attributes instanceof String ||
				ClassUtils.isPrimitiveOrWrapper(attributes.getClass())) {
			
			url = getUrlForHandlerWithSingleAttribute(handlerName, attributes, defaults);
		}
		else {
			if (attributes instanceof Collection) {
				Collection c = (Collection) attributes;
				attributes = c.toArray(new Object[c.size()]);
			}
			
			if (attributes.getClass().isArray()) {
				url = getUrlForHandlerWithArray(handlerName, (Object[]) attributes);
			}
			else {
				url = getUrlForHandlerWithBean(handlerName, attributes, defaults);				
			}
		}
		return addServletMapping(url);
	}
	
	protected String addServletMapping(String url) {
		return servletPrefix + url + servletSufix;
	}

	/**
	 * Returns a Map of default values that are used to build URLs. The default
	 * implementation returns <code>null</code>.
	 */
	protected Map<String, Object> getDefaults() {
		return null;
	}

	/**
	 * Returns the URL for a handler without any extra wildcards. If the pattern
	 * contains wildcards all of them must be present in the defaults map.
	 */
	private String getUrlForHandlerWithoutAttributes(String handlerName,
			Map<String, Object> defaults) {
		
		AttributePattern p = getPatternForHandler(handlerName, null, defaults, 0);
		if (p == null) {
			return null;
		}
		return p.fillInAttribute(null, defaults);
	}
	
	/**
	 * Returns the URL for a handler with one single unnamed wildcard. 
	 */
	private String getUrlForHandlerWithSingleAttribute(String handlerName, 
			Object attribute, Map<String, Object> defaults) {
		
		AttributePattern p = getPatternForHandler(handlerName, null, defaults, 1);
		if (p == null) {
			return null;
		}
		return p.fillInAttribute(attribute, defaults);
	}
	
	private String getUrlForHandlerWithArray(String handlerName, Object[] attributes) {
		AttributePattern p = getPatternForHandler(handlerName, null, null, attributes.length);
		if (p == null) {
			return null;
		}
		return p.fillInAttributes(attributes);
	}
	
	/**
	 * Returns the URL for a handler that is mapped with multiple wildcards.
	 * The wildcard replacements are taken from the given Map.
	 */
	private String getUrlForHandlerWithMap(String beanName, 
			Map<String, Object> attributes, Map<String, Object> defaults) {
		
		List<AttributePattern> patterns = getPatternsForHandler(beanName, defaults);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		for (AttributePattern p : patterns) {
			if (p.canFillIn(attributes, defaults, 0)) {
				return p.fillInAttributes(new MapWrapper(attributes), defaults);
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
			Map<String, Object> defaults) {
		
		AttributePattern p = getPatternForHandler(beanName, defaults);
		if (p != null) {
			return p.fillInAttributes(new BeanWrapperImpl(bean), defaults);
		}
		return null;
	}
	
	/**
	 * Subclasses must implement this method and return all 
	 * {@link AttributePattern patterns} for the handler with the specified
	 * name.
	 */
	protected abstract List<AttributePattern> getPatternsForHandler(
			String beanName, Map<String, Object> defaults);
	
	/**
	 * Returns the pattern for the handler with the given name that contains
	 * all the given wildcards.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	protected AttributePattern getPatternForHandler(String handlerName, 
			Map<String, Object> attributes, Map<String, Object> defaults, 
			int anonymousWildcards) {
		
		List<AttributePattern> patterns = getPatternsForHandler(handlerName, defaults);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}

		AttributePattern result = null;
		Iterator<AttributePattern> it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = it.next();
			if (p.canFillIn(attributes, defaults, anonymousWildcards)) {
				if (result != null) {
					throw new IllegalArgumentException("Exactly one mapping with "
							+ anonymousWildcards + " anonymous wildcards is required "
							+ "for hander "	+ handlerName);
				}
				result = p;
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("Could not find mapping with "
					+ anonymousWildcards + " anonymous wildcards "
					+ "for hander "	+ handlerName);
		}
		return result;
	}
	
	/**
	 * Returns the pattern for the handler with the given name.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	protected AttributePattern getPatternForHandler(String handlerName, 
			Map<String, Object> defaults) {
		
		List<AttributePattern> patterns = getPatternsForHandler(handlerName, defaults);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		if (patterns.size() != 1) {
			throw new IllegalArgumentException("Ambigious mapping - more than " 
					+ "one pattern is registered for hander " + handlerName);
		}
		return patterns.get(0);
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

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWildcardAttributes(HttpServletRequest request) {
		return (Map<String, Object>) request.getAttribute(AttributePattern.EXPOSED_ATTRIBUTES);
	}
}
