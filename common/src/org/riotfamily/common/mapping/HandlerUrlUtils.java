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
package org.riotfamily.common.mapping;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

public final class HandlerUrlUtils {

	private static Pattern typePattern = Pattern.compile("\\((.+)\\)\\s*(.+)");
	
	private HandlerUrlUtils() {
	}
	
	public static HandlerUrlResolver getUrlResolver(HttpServletRequest request) {
		return getUrlResolver(RequestContextUtils.getWebApplicationContext(request));
	}
	
	public static HandlerUrlResolver getUrlResolver(BeanFactory lbf) {
		if (lbf.containsBean("handlerUrlResolver")) {
			return SpringUtils.getBean(lbf,	"handlerUrlResolver", 
					HandlerUrlResolver.class);
		}
		return SpringUtils.beanOfType(lbf, HandlerUrlResolver.class);
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request The current request
	 */
	public static String getUrl(HttpServletRequest request,
			String handlerName, Object... attributes) {
		
		return request.getContextPath() + getContextRelativeUrl(
				request, handlerName, attributes);
	}
	
	/**
	 * Returns the URL of a mapped handler <em>without</em> the context-path.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards.
	 * @param request The current request
	 */
	public static String getContextRelativeUrl(HttpServletRequest request, 
			String handlerName, Object... attributes) {
		
		return getUrlResolver(request).getUrlForHandler(handlerName, attributes);
	}
	
	/**
	 * Sends a redirect to the handler with the specified name.
	 * @param request The current request
	 * @param response The current response
	 * @param handlerName The name of the handler
	 * @throws IOException
	 */
	public static void sendRedirect(HttpServletRequest request, 
			HttpServletResponse response, String handlerName) 
			throws IOException {
		
		String url = getContextRelativeUrl(request, handlerName);
		ServletUtils.resolveAndRedirect(request, response, url);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPathVariables(HttpServletRequest request) {
		Map<String, String> vars = (Map<String, String>) request.getAttribute(
				HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		
		if (vars == null) {
			vars = Generics.newHashMap();
		}
		return vars;
	}
	
	public static String getPathVariable(HttpServletRequest request, String name) {
		return getPathVariables(request).get(name);
	}
	
	public static Map<String, Object> getTypedPathVariables(HttpServletRequest request) {
		Map<String, Object> vars = Generics.newHashMap();
  		for (Map.Entry<String, String> var : getPathVariables(request).entrySet()) {
			Object value = var.getValue();
			if (value != null) {
				String name = var.getKey();
				Matcher m = typePattern.matcher(name);
				String type = m.group(1);
				if (type != null) {
					name = m.group(2);
					value = convert(var.getValue(), type);
				}
				vars.put(name, value);
			}
		}
  		return vars;
	}
	
	private static Object convert(String s, String type) {
		if (type == null || type.equalsIgnoreCase("String")) {
			return s;
		}
		if (type.equalsIgnoreCase("Integer")) {
			return Integer.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Long")) {
			return Long.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Short")) {
			return Short.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Double")) {
			return Double.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Float")) {
			return Float.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Boolean")) {
			return Boolean.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Character")) {
			return new Character(s.charAt(0));
		}
		else {
			throw new IllegalArgumentException("Unsupported type: " + type 
					+ " - must be Integer, Long, Short, Double, Float," 
					+ " Boolean or Character");
		}
	}

	public static String getPathWithinMapping(HttpServletRequest request) {
		return (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	}
	
}
