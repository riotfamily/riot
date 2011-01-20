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
package org.riotfamily.common.web.cache.tags;

import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.HibernateUtils;
import org.riotfamily.common.web.cache.TagCacheItems;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * CacheTagGenerator that handles classes annotated with {@link TagCacheItems}.
 * 
 * @see CacheTagUtils#getTag(Class, java.io.Serializable)
 * @see HibernateUtils#getIdAsString(SessionFactory, Object)
 */
public class AnnotationCacheTagGenerator implements CacheTagGenerator {

	private SessionFactory sessionFactory;

	public AnnotationCacheTagGenerator(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String generateTag(Object obj) {
		if (obj.getClass().isAnnotationPresent(TagCacheItems.class)) {
			String id = HibernateUtils.getIdAsString(sessionFactory, obj);
			Assert.notNull(id, "Model contains unsaved entity:" + obj);
			Class<?> target = AnnotationUtils.findAnnotationDeclaringClass(TagCacheItems.class ,obj.getClass());
			return CacheTagUtils.getTag(target , id);
		}
		return null;
	}

}
