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
package org.riotfamily.common.web.mvc.mapping;

import java.util.Collection;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Class that performs URL lookups for handlers mapped by a 
 * {@link ReverseHandlerMapping}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HandlerUrlResolver implements ApplicationContextAware {

	private HandlerUrlResolver parent;

	private ApplicationContext applicationContext;
	
	private List<ReverseHandlerMapping> reverseMappings;
	
	public void setParent(HandlerUrlResolver parent) {
		this.parent = parent;
	}
	
	public void setApplicationContext(ApplicationContext ctx) {
		this.applicationContext = ctx;
	}

	protected synchronized List<ReverseHandlerMapping> getMappings() {
		if (reverseMappings == null) {
			Assert.notNull(applicationContext, "The ApplicationContext must be set");
			Collection<HandlerMapping> mappings = SpringUtils.listBeansOfType(
					applicationContext, HandlerMapping.class);
			
			List<ReverseHandlerMappingAdapter> adapters = 
					SpringUtils.orderedBeansIncludingAncestors(
							applicationContext, ReverseHandlerMappingAdapter.class);
			
			reverseMappings = Generics.newArrayList();
			for (HandlerMapping mapping : mappings) {
				if (mapping instanceof ReverseHandlerMapping) {
					reverseMappings.add((ReverseHandlerMapping) mapping);
				}
				else {
					for (ReverseHandlerMappingAdapter adapter : adapters) {
						if (adapter.supports(mapping)) {
							reverseMappings.add(adapter.adapt(mapping));
							break;
						}
					}
				}
			}
		}
		return reverseMappings;
	}
	
	public String getUrlForHandler(Class<?> handlerClass, Object... vars) {
		for (ReverseHandlerMapping mapping : getMappings()) {
			String url = mapping.getUrlForHandler(handlerClass, vars);
			if (url != null) {
				return url;
			}
		}
		if (parent != null) {
			return parent.getUrlForHandler(handlerClass, vars);
		}
		return null;
	}
	
	public String getUrlForHandler(String handlerName, Object... vars) {
		for (ReverseHandlerMapping mapping : getMappings()) {
			String url = mapping.getUrlForHandler(handlerName, vars);
			if (url != null) {
				return url;
			}
		}
		if (parent != null) {
			return parent.getUrlForHandler(handlerName, vars);
		}
		return null;
	}
	
}
