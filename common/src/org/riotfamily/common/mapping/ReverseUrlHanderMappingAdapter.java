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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.beans.property.MapPropertyAccessor;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

public class ReverseUrlHanderMappingAdapter implements ReverseHandlerMappingAdapter {

	private String servletPrefix = "";
	
	public void setServletPrefix(String servletPrefix) {
		this.servletPrefix = servletPrefix;
	}
	
	public boolean supports(HandlerMapping mapping) {
		return mapping instanceof AbstractUrlHandlerMapping;
	}
	
	public ReverseHandlerMapping adapt(HandlerMapping mapping) {
		return new ReverseUrlHandlerMapping((AbstractUrlHandlerMapping) mapping);
	}
	
	private class ReverseUrlHandlerMapping implements ReverseHandlerMapping {
		
		private Map<String, List<HandlerUrl>> urlMap = Generics.newHashMap();
		
		private ApplicationContext context;
		
		@SuppressWarnings("unchecked")
		public ReverseUrlHandlerMapping(AbstractUrlHandlerMapping mapping) {
			context = mapping.getApplicationContext();
			Map<String, ?> handlers = mapping.getHandlerMap();
			for (Map.Entry<String,?> entry : handlers.entrySet()) {
				String url = entry.getKey();
				Object handler = entry.getValue();
				String handlerName = getHandlerName(handler);
				List<HandlerUrl> urls = urlMap.get(handlerName);
				if (urls == null) {
					urls = Generics.newLinkedList();
					urlMap.put(handlerName, urls);
				}
				urls.add(new HandlerUrl(url));
				Collections.sort(urls);
			}
		}

	
		private String getHandlerName(Object handler) {
			if (handler instanceof String) {
				return (String) handler;
			}
			Map<String, ?> beans = context.getBeansOfType(
					handler.getClass(), false, false);
			
			for (Map.Entry<String, ?> entry : beans.entrySet()) {
				if (entry.getValue().equals(handler)) {
					return entry.getKey();
				}
			}
			return null;
		}
		
		public String getUrlForHandler(String name, Object... vars) {
			List<HandlerUrl> urls = urlMap.get(name);
			if (urls != null) {
				if (vars != null && vars.length == 1) {
					Object var = vars[0];
					if (var instanceof Map<?, ?>) {
						return getUrl(urls, new MapPropertyAccessor((Map<?, ?>) var));
					}
					if (var instanceof Collection<?>) {
						return getUrl(urls, (Collection<?>) var);
					}
					if (var.getClass().isArray()) {
						return getUrl(urls, CollectionUtils.arrayToList(var));		
					}
					if (var instanceof String
							|| ClassUtils.isPrimitiveOrWrapper(var.getClass())) {
						
						return getUrl(urls, Collections.singletonList(var));
					}
					return getUrl(urls, new BeanWrapperImpl(var));
				}
				return getUrl(urls, CollectionUtils.arrayToList(vars));
			}
			return null;
		}

		private String getUrl(List<HandlerUrl> urls, PropertyAccessor pa) {
			for (HandlerUrl uri : urls) {
				if (uri.canFillIn(pa)) {
					return servletPrefix + uri.fillIn(pa);
				}
			}
			return null;
		}

		private String getUrl(List<HandlerUrl> urls, Collection<?> values) {
			for (HandlerUrl uri : urls) {
				if (uri.canFillIn(values)) {
					return servletPrefix + uri.fillIn(values);
				}
			}
			return null;
		}
		
	}

}
