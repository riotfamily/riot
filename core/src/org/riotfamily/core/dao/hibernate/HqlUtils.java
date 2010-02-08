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
package org.riotfamily.core.dao.hibernate;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Query;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.springframework.util.StringUtils;

public class HqlUtils {
	
	/**
	 * Appends the given term to the StringBuilder. If the buffer is not empty,
	 * the provided expression is inserted right before the term (surrounded
	 * by spaces).
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
	
	/**
	 * Returns a HQL term that can be used within a where-clause to perform
	 * a query-by-example.
	 * @since 6.4
	 */
	public static String getExampleWhereClause(Class<?> entityClass, 
			Object example, String alias, String... propertyNames) {

		if (example == null || propertyNames == null) {
			return null;
		}
		StringBuilder hql = new StringBuilder();
		Map<?, ?> properties;
		if (example instanceof Map<?, ?>) {
			properties = (Map<?, ?>) example;
		}
		else {
			properties = PropertyUtils.getProperties(example, propertyNames);
		}
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			Object value = properties.get(name);
			if (value != null) {
				Class<?> propertyClass = PropertyUtils.getPropertyType(entityClass, name);
				if (propertyClass != null) {
					if (hql.length() > 0) {
						hql.append(" and ");
					}
					if (Collection.class.isAssignableFrom(propertyClass)) {
						Collection<?> c = null;
						if (value instanceof Collection<?>) {
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
	 * Joins all path properties requested by the search property names
	 */
	public static StringBuilder appendJoinsForSearch(StringBuilder hql, String alias, String... propertyNames) {
		if (propertyNames == null || propertyNames.length == 0) {
			return hql;
		}		
		for (String name : propertyNames) {
			int i = name.lastIndexOf('.');
			if (i != -1) {			
				String s = name.substring(0, i);
				hql.append(" left join ").append(alias).append('.').append(s).append(" as ").append(s.replace('.', '_'));
			}			
		}				
		return hql;
	}

	
	public static String getSearchWhereClause(String entityName, String alias,
			String searchParamName, String... propertyNames) {

		if (propertyNames == null || propertyNames.length == 0) {
			return null;
		}
		StringBuilder hql = new StringBuilder("(");
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			if (i > 0) {
				hql.append(" or ");
			}
			
			hql.append('(');
			int d = name.lastIndexOf('.');
			String s = alias;
			if (d != -1) {	
				String searchAlias = "s";
				hql.append(alias).append(".id in (select distinct ").append(searchAlias).append(".id from ");
				hql.append(entityName).append(' ').append(searchAlias);
				appendJoinsForSearch(hql, searchAlias, propertyNames);
				hql.append(" where ");
				s = name.substring(0, d).replace('.', '_');
				name = name.substring(d + 1);
				hql.append(s).append(" is not null and ");
			}
			hql.append("lower(str(").append(s).append('.').append(name);
			hql.append(")) like :").append(searchParamName).append(')');
			if (d != -1) {
				hql.append(')');
			}
		}
		hql.append(')');		
		return hql.toString();
	}
}
