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
package org.riotfamily.common.hibernate;

import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.springframework.beans.PropertyAccessorUtils;

public final class HibernateUtils {

	private HibernateUtils() {
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T get(Session session, Class<T> beanClass, String id) {
		SessionFactory sf = session.getSessionFactory();
		Serializable serialId = convertId(beanClass, id, sf);
		return (T) session.get(beanClass, serialId);
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Serializable> getIdentifierClass(
			Class<?> beanClass, SessionFactory sessionFactory) {
		
		return sessionFactory.getClassMetadata(beanClass)
				.getIdentifierType().getReturnedClass();	
	}
	
	public static Serializable convertId(Class<?> beanClass, String id,
			SessionFactory sessionFactory) {

		return PropertyUtils.convert(id, getIdentifierClass(beanClass, sessionFactory));
	}

	public static Serializable getId(SessionFactory sessionFactory, Object bean) {
		Class<?> clazz = Hibernate.getClass(bean);
		ClassMetadata metadata = sessionFactory.getClassMetadata(clazz);
		return metadata.getIdentifier(bean, EntityMode.POJO);
	}
	
	public static Serializable getIdAndSaveIfNecessary(SessionFactory sessionFactory, Object bean) {
		Serializable id = getId(sessionFactory, bean);
		if (id == null) {
			sessionFactory.getCurrentSession().save(bean);
		}
		return getId(sessionFactory, bean);
	}
	
	public static String getIdAsString(SessionFactory sessionFactory, Object bean) {
		Serializable id = getId(sessionFactory, bean);
		return (id != null) ? id.toString() : null;
	}
	
	public static boolean isPersistentProperty(SessionFactory sessionFactory, 
			Class<?> clazz, String propertyPath) {
		
		int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
		// Handle nested properties recursively.
		if (pos > -1) {
			String nestedProperty = propertyPath.substring(0, pos);
			String nestedPath = propertyPath.substring(pos + 1);
			Class<?> nestedClazz = PropertyUtils.getPropertyType(clazz, nestedProperty);
			return isPersistentProperty(sessionFactory, clazz, nestedProperty)
					&& isPersistentProperty(sessionFactory, nestedClazz, nestedPath);
		}
		else {
			try {
				ClassMetadata metadata = sessionFactory.getClassMetadata(clazz);
				if (metadata == null) {
					return false;
				}
				return metadata.getPropertyType(propertyPath) != null;
			}
			catch (HibernateException e) {
				return false;
			}
		}
	}

	public static boolean queryContainsParameter(Query query, String name) {
		return query.getQueryString().matches(".+:" + name + "\\b.*");
	}
	
	public static void setParameter(Query query, String name, Object value) {
		if (value != null && queryContainsParameter(query, name)) {
			query.setParameter(name, value);
		}
	}
}
