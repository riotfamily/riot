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
package org.riotfamily.riot.hibernate.cachius;

import org.riotfamily.common.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.common.hibernate.QueryResult;
import org.riotfamily.website.cache.CacheTagUtils;
import org.riotfamily.website.cache.TagCacheItems;
import org.riotfamily.website.cache.TaggingSequence;
import org.springframework.core.Ordered;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapperPlugin that tags cache items whenever a {@link QueryResult}
 * for entities with the {@link TagCacheItems} annotation is accessed by a 
 * FreeMarker template.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class QueryResultObjectWrapperPlugin 
		implements ObjectWrapperPlugin, Ordered {

	private int order = Ordered.HIGHEST_PRECEDENCE;

	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order. Default is {@link Ordered#HIGHEST_PRECEDENCE}.
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	
	public boolean supports(Object obj) {
		return obj instanceof QueryResult<?>;
	}

	public TemplateModel wrapSupportedObject(Object obj,
			PluginObjectWrapper wrapper) throws TemplateModelException {
		
		QueryResult<?> result = (QueryResult<?>) obj;
		TaggingSequence seqence = new TaggingSequence(result, wrapper);
		for (Class<?> clazz : result.getResultClasses()) {
			if (clazz.isAnnotationPresent(TagCacheItems.class)) {
				seqence.addTag(CacheTagUtils.getTag(clazz));
			}
		}
		return seqence;
	}

}
