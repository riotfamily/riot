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

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.proxy.HibernateProxy;
import org.riotfamily.cachius.CacheContext;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.collection.TypedList;
import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.common.hibernate.ActiveRecordUtils;
import org.riotfamily.common.web.cache.TagCacheItems;
import org.springframework.util.ClassUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class CacheTagUtils {

	private CacheTagUtils() {
	}
	
	public static String getTag(Class<?> clazz) {
		if (HibernateProxy.class.isAssignableFrom(clazz)) {
			return clazz.getSuperclass().getName();
		}
		return ClassUtils.getUserClass(clazz).getName();
	}
	
	public static String getTag(Class<?> clazz, Serializable id) {
		return getTag(clazz) + '#' + id; 
	}
		
	public static void tag(Class<?> clazz, Serializable id) {
		CacheContext.tag(getTag(clazz, id));
	}
	
	public static void tag(ActiveRecord record) {
		Serializable id = ActiveRecordUtils.getId(record);
		CacheContext.tag(getTag(record.getClass(), id));
	}
	
	public static void tag(Class<?> clazz) {
		CacheContext.tag(getTag(clazz));
	}
	
	public static void tag(String className) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		tag(clazz);
	}
	
	public static void tagIfSupported(Class<?> clazz) {
		if (clazz.isAnnotationPresent(TagCacheItems.class)) {
			tag(clazz);
		}
	}
	
	public static void tagIfSupported(Collection<?> c) {
		if (c instanceof TypedList<?>) {
			tagIfSupported(((TypedList<?>) c).getItemClass());
		}
	}
	
	public static void tagIfSupported(Object obj) {
		if (obj instanceof ActiveRecord && obj.getClass().isAnnotationPresent(TagCacheItems.class)) {
			tag((ActiveRecord) obj);
		}
	}
	
	public static void invalidate(CacheService cacheService, Class<?> clazz) {
		invalidate(cacheService, clazz, null);
	}
	
	public static void invalidate(CacheService cacheService, Class<?> clazz, Serializable objectId) {
		if (cacheService != null && clazz != null) {
		    cacheService.invalidateTaggedItems(getTag(clazz));
			if (objectId != null) {
				cacheService.invalidateTaggedItems(getTag(clazz, objectId));
			}
		}
	}
	
	public static void invalidate(CacheService cacheService, ActiveRecord record) {
		invalidate(cacheService, record.getClass(), ActiveRecordUtils.getId(record));
	}

}
