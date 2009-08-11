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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.HandlerMapping;

/**
 * Interface that can be implemented by {@link HandlerMapping HandlerMappings} 
 * in order to allow reverse lookups. This way you can obtain an URL for a 
 * mapped handler.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface ReverseHandlerMapping {

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the 
	 * beanName of the matched handler.
	 * <p>Note: This attribute is not required to be supported by all
	 * ReverseHandlerMapping implementations.
	 */
	String TOP_LEVEL_HANDLER_NAME_ATTRIBUTE = 
			ReverseHandlerMapping.class.getName() + ".topLevelHandlerName";

	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request Optional request that is used as context to look up 
	 *        default wildcard values.
	 */
	public String getUrlForHandler(String handlerName, Object attributes);
	
}
