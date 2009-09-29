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
package org.riotfamily.common.web.cache.freemarker;

import org.hibernate.SessionFactory;
import org.riotfamily.common.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.common.hibernate.HibernateUtils;
import org.riotfamily.common.web.cache.CacheTagUtils;
import org.riotfamily.common.web.cache.TagCacheItems;
import org.springframework.core.Ordered;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapperPlugin that tags cache items whenever a class with the
 * {@link TagCacheItems} annotation is accessed by a FreeMarker template.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TaggingObjectWrapperPlugin implements ObjectWrapperPlugin, Ordered {

	private int order = 0;

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order. Default is <code>0</code>.
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	public boolean supports(Object obj) {
		return obj.getClass().isAnnotationPresent(TagCacheItems.class);
	}

	public TemplateModel wrapSupportedObject(Object obj,
			PluginObjectWrapper wrapper) throws TemplateModelException {

		TaggingStringModel model = new TaggingStringModel(obj, wrapper);
		String id = HibernateUtils.getIdAsString(sessionFactory, obj);
		if (id == null) {
			throw new TemplateModelException("Model contains unsaved entity:" + obj);
		}
		model.addTag(CacheTagUtils.getTag(obj.getClass(), id));
		return model;
	}
	
}
