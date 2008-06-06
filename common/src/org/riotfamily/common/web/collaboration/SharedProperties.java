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
package org.riotfamily.common.web.collaboration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;


/**
 * Class that facilitates collaboration between controllers.
 * <p> 
 * The Spring DispatcherServlet performs an attribute clean-up after an include
 * so included controllers can't expose request attributes to other controllers
 * unless you turn this feature off, which usually isn't a good idea.
 * <p>
 * Another level of complexity comes into play when you want to cache the output
 * of a controller that contributes information needed by other collaborators.
 * If a cached version is served, the controller's handleRequest() method is not
 * invoked (which after all is the purpose of a cache), therefore the controller
 * has no chance to set any request attributes.
 * <p>
 * One solution would be to cache the dependent controller too. But in this case
 * we would have to ensure that the cached version of the first controller is 
 * invalidated as soon as the dependent cache item is removed from the cache.
 * As there is no easy way to achieve this, the SharedProperites class uses
 * another approach:
 * <p>
 * The {@link SharedPropertiesInterceptor} exposes a HashMap as request 
 * attribute before the top-level handler is executed. A controller that wants 
 * to hand data on to a subsequent controller can use the 
 * {@link #setProperty(HttpServletRequest, String, String)} method to add 
 * entries to this map. Because the map is already present <em>before</em> the 
 * the include is performed, it won't be cleaned up by the DispatcherServlet. 
 * Another controller can now invoke 
 * {@link #getProperty(HttpServletRequest, String)} to retrieve the previously
 * set values.
 * <p>
 * Currently shared properties are limited to Strings. The reason for this is
 * that Cachius is aware of shared properties and caches them along with the
 * actual output. It's basically a precaution to prevent users from caching 
 * their business objects. The fact that Cachius knows about shared properties
 * allows us to serve cached content and still expose data to others. 
 * <p>
 * A common use case for this is when you want to add information to the 
 * document title (or a meta-tag) that is provided by a controller which is 
 * deeply nested inside a template and would be processed too late. An elegant 
 * solution is to use the push-up feature of the TemplateController, which
 * takes the nested controller out of the regular rendering flow and processes
 * it first. This pushed-up controller can now expose shared properties which
 * are then available everywhere else on the page, even if the pushed-up content
 * comes from the cache.
 * <p>
 * Note: You can easily access shared properties in your FreeMarker views via 
 * the <code>common.getSharedProperty()</code> function and the 
 * <code>common.setSharedProperty()</code> macro.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SharedProperties {
	
	/** Name of the request attribute under which properties are exposed */
	private static final String PROPERTIES_ATTRIBUTE = 
			SharedProperties.class.getName() + ".properties";
	
	/**
	 * Checks whether a properties map is present in the request and creates
	 * a new one if no map is found.
	 * @see SharedPropertiesInterceptor
	 */
	static void exposeTo(HttpServletRequest request) {
		if (request.getAttribute(PROPERTIES_ATTRIBUTE) == null) {
			request.setAttribute(PROPERTIES_ATTRIBUTE, new HashMap<String, String>());
		}
	}
	
	/**
	 * Returns the properties map for the given request.
	 * @throws IllegalStateException if no properties map is found in the request
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> getProperties(HttpServletRequest request) {
		Map<String, String> properties = (Map<String, String>) request.getAttribute(PROPERTIES_ATTRIBUTE);
		Assert.state(properties != null, "No shared properties found in the " +
				"request. Please register a SharedPropertiesInterceptor " +
				"in order to use this feature.");
		
		return properties;
	}
	
	/**
	 * Sets a shared property.
	 * @throws IllegalStateException if no properties map is found in the request
	 */
	public static void setProperty(HttpServletRequest request, String key, String value) {
		getProperties(request).put(key, value);
	}
	
	/**
	 * Sets multiple shared property at once.
	 */
	@SuppressWarnings("unchecked")
	public static void setProperties(HttpServletRequest request, Map<String, String> properties) {
		if (properties != null) {
			Map<String, String> map = (Map<String, String>) request.getAttribute(PROPERTIES_ATTRIBUTE);
			if (map != null) {
				map.putAll(properties);
			}
		}
	}

	/**
	 * Retrieves a shared property.
	 * @throws IllegalStateException if no properties map is found in the request
	 */
	public static String getProperty(HttpServletRequest request, String key) {
		return (String) getProperties(request).get(key);
	}
	
	/**
	 * Returns all shared properties currently set.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getSnapshot(HttpServletRequest request) {
		Map<String, String> properties = (Map<String, String>) request.getAttribute(PROPERTIES_ATTRIBUTE);
		return properties != null ? new HashMap<String, String>(properties) : null;
	}
	
	/**
	 * Returns a map containing all properties that have been added or modified
	 * since the snapshot was made. 
	 */
	public static Map<String, String> getDiff(HttpServletRequest request,
			Map<String, String> snapshot) {
		
		Map<String, String> diff = getSnapshot(request);
		if (diff == null || diff.isEmpty()) {
			return null;
		}
		if (snapshot != null) {
			Iterator<Map.Entry<String, String>> it = diff.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				String key = entry.getKey();
				String value = entry.getValue();
				String oldValue = snapshot.get(key);
				if (ObjectUtils.nullSafeEquals(value, oldValue)) {
					it.remove();
				}
			}
		}
		return diff;
	}
}
