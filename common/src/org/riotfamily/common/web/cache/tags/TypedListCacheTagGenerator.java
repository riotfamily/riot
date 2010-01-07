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
import org.riotfamily.common.collection.TypedList;
import org.riotfamily.common.hibernate.HibernateUtils;
import org.riotfamily.common.web.cache.TagCacheItems;

/**
 * CacheTagGenerator that handles {@link TypedList typed lists} whose 
 * {@link TypedList#getItemClass() itemClass} is annotated with 
 * {@link TagCacheItems}.
 * 
 * @see CacheTagUtils#getTag(Class)
 * @see HibernateUtils#getIdAsString(SessionFactory, Object)
 */
public class TypedListCacheTagGenerator implements CacheTagGenerator {

	public String generateTag(Object obj) {
		if (obj instanceof TypedList<?>) {
			TypedList<?> list = (TypedList<?>) obj;
			Class<?> itemClass = list.getItemClass();
			if (itemClass.isAnnotationPresent(TagCacheItems.class)) {
				return CacheTagUtils.getTag(itemClass);
			}
		}
		return null;
	}

}
