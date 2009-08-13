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
package org.riotfamily.riot.hibernate.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.core.security.AccessController;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.util.StringUtils;

public final class HibernateUtils {

	public static final String LIVE_MODE_FILTER_NAME = "liveMode";
	
	public static final String PUBLISHED_PARAM_NAME = "published";
	
	private HibernateUtils() {
	}
	
	private static RiotLog getLog() {
		return RiotLog.get(HibernateUtils.class);
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

	/**
	 * Returns a HQL term that can be used within a where-clause to perform
	 * a query-by-example.
	 * @since 6.4
	 */
	@SuppressWarnings("unchecked")
	public static String getExampleWhereClause(Class<?> entityClass, 
			Object example , String alias, String[] propertyNames) {

		if (example == null || propertyNames == null) {
			return null;
		}
		StringBuffer hql = new StringBuffer();
		Map properties;
		if (example instanceof Map) {
			properties = (Map) example;
		}
		else {
			properties = PropertyUtils.getProperties(example, propertyNames);
		}
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			Object value = properties.get(name);
			if (value != null) {
				if (hql.length() > 0) {
					hql.append(" and ");
				}				
				Class<?> propertyClass = PropertyUtils.getPropertyType(entityClass, name);
				if (propertyClass != null) {
					if (Collection.class.isAssignableFrom(propertyClass)) {
						Collection<?> c = null;
						if (value instanceof Collection) {
							c = (Collection<?>) value;
						}
						else {
							c = Collections.singleton(value);
						}					
						hql.append("1 = 1");
						for (int j = 0; j < c.size(); j++) {
							hql.append(" and :").append(name).append("_").append(j)
									.append(" in elements(").append(alias)
									.append('.').append(name).append(')');
						}
					}
					else {
						hql.append(alias).append('.').append(name);
						hql.append(" = :").append(name.replaceAll("\\.", "_dot_"));
					}
				}
			}
		}
		return hql.length() > 0 ? hql.toString() : null;
	}

	/**
	 * Sets collection values as individual query parameters. Use this method
	 * together with {@link #getExampleWhereClause(Object, String, String[])}
	 * when your example contains collections.
	 * <p>
	 * The method iterates over the provides names array and inspects the given
	 * bean (or map). If there's a property (or map entry) of the type
	 * <code>java.util.Collection</code>, the methods iterates over the
	 * collection and sets a query parameter for each item. The name is suffixed
	 * with an underscore and the item's index.
	 *
	 * @since 6.4
	 */
	@SuppressWarnings("unchecked")
	public static void setCollectionValueParams(Query query,
			String[] names, Class<?> entityClass, Object object) {

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			Object value;
			if (object instanceof Map) {
				value = ((Map) object).get(name);
			}
			else {
				value = PropertyUtils.getProperty(object, name);
			}
			if (value != null) {
				Class<?> propertyClass = PropertyUtils.getPropertyType(entityClass, name);
				if (propertyClass != null && Collection.class.isAssignableFrom(propertyClass)) {
					Collection<?> c = null;
					if (value instanceof Collection) {
						c = (Collection<?>) value;
					}
					else {
						c = Collections.singleton(value);
					}
					int j = 0;
					Iterator<?> values = c.iterator();
					while (values.hasNext()) {
						query.setParameter(name + "_" + j++, values.next());
					}
				}
			}
		}
	}

	/**
	 * Returns a HQL term that can be used within a where-clause to perform
	 * a search. Example: <code>"(lower(str(&lt;alias&gt;.&lt;property[0]&gt;))
	 * like :&lt;searchParamName&gt; or lower(str(&lt;alias&gt;.&lt;property[1]&gt;))
	 * like :&lt;searchParamName&gt; or ...)"</code>
	 * @since 6.4
	 */
	public static String getSearchWhereClause(String alias,
			String[] propertyNames, String searchParamName) {

		if (propertyNames == null || propertyNames.length == 0) {
			return null;
		}
		StringBuffer hql = new StringBuffer("(");
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			if (i > 0) {
				hql.append(" or ");
			}
			
			hql.append('(');
			if (name.indexOf('.') != -1) {				
				String[] path = name.split("\\.");
				for (int j = 0; j < path.length - 1; j++) {
					hql.append(alias).append('.');
					for (int k = 0; k <= j; k++) {
						hql.append(path[k]);
						if (k < j) {
							hql.append('.');
						}
					}
					hql.append(" is not null ");
					if (j < path.length - 2) {
						hql.append(" and ");
					}
				}
				hql.append(" and ");
			}
			hql.append("lower(str(").append(alias).append('.').append(name);
			hql.append(")) like :").append(searchParamName).append(')');;
		}
		hql.append(')');		
		return hql.toString();
	}
	
	/**
	 * Joins all path properties requested by the search property names
	 * Example: search should be foo.bar = :search -&gt <code>join this.foo</code>	 * 
	 */
	public static StringBuffer appendJoinsForSearch(StringBuffer hql, String alias, String[] propertyNames) {
		if (propertyNames == null || propertyNames.length == 0) {
			return hql;
		}		
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];			
			
			if (name.indexOf('.') != -1) {				
				String[] path = name.split("\\.");
				for (int j = 0; j < path.length - 1; j++) {
					hql.append(" left join ").append(alias).append('.');
					for (int k = 0; k <= j; k++) {
						hql.append(path[k]);
						if (k < j) {
							hql.append('.');
						}
					}					
				}				
			}			
		}				
		return hql;
	}

	/**
	 * Appends the given term to the StringBuffer. If the buffer is not empty,
	 * the provided expression is inserted right before the term (surrounded
	 * by spaces).
	 * @since 6.4
	 */
	public static StringBuffer appendHql(StringBuffer hql,
			String expression, String term) {

		if (StringUtils.hasText(term)) {
			if (expression != null && hql.length() > 0) {
				hql.append(' ').append(expression);
			}
			hql.append(' ');
			hql.append(term);
		}
		return hql;
	}
	
	/**
	 * Appends the given term to the StringBuffer. If the buffer is not empty,
	 * the provided expression is inserted right before the term (surrounded
	 * by spaces).
	 * @since 8.0.1
	 */
	public static StringBuilder appendHql(StringBuilder hql,
			String expression, String term) {

		if (StringUtils.hasText(term)) {
			if (expression != null && hql.length() > 0) {
				hql.append(' ').append(expression);
			}
			hql.append(' ');
			hql.append(term);
		}
		return hql;
	}

	public static void addEqOrNull(Criteria c, String name, Object val) {
		if (val != null) {
			c.add(Restrictions.eq(name, val));
		}
		else {
			c.add(Restrictions.isNull(name));
		}
	}

	public static boolean isLiveModeFilterDefined(SessionFactory sf) {
		return sf.getDefinedFilterNames().contains(LIVE_MODE_FILTER_NAME);
	}
	
	public static void enableLiveModeFilterIfNecessary(Session session) {
		if (!AccessController.isAuthenticatedUser()) {
			if (isLiveModeFilterDefined(session.getSessionFactory())) {
				session.enableFilter(LIVE_MODE_FILTER_NAME).setParameter(
						PUBLISHED_PARAM_NAME, Boolean.TRUE);	
			}
			else {
				getLog().warn("No filter named " + LIVE_MODE_FILTER_NAME 
						+ " defined for SessionFactory");
			}
		}
	}

	public static void setParameter(Query query, String name, Object value) {
		if (value != null && query.getQueryString().matches(".+:" + name + "\\b.*")) {
			query.setParameter(name, value);
		}
	}
}
