/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.beans.PropertyUtils;
import org.springframework.util.StringUtils;

public final class HibernateUtils {

	private HibernateUtils() {
	}

	public static Object get(Session session, Class beanClass, String id) {
		SessionFactory sf = session.getSessionFactory();
		Serializable serialId = convertId(beanClass, id, sf);
		return session.get(beanClass, serialId);
	}

	public static Serializable convertId(Class beanClass, String id,
			SessionFactory sessionFactory) {

		Class identifierClass = sessionFactory.getClassMetadata(beanClass)
				.getIdentifierType().getReturnedClass();

		return (Serializable) PropertyUtils.convert(id, identifierClass);
	}

	public static String getIdAsString(SessionFactory sessionFactory, Object bean) {
		Class clazz = Hibernate.getClass(bean);
		ClassMetadata metadata = sessionFactory.getClassMetadata(clazz);
		return metadata.getIdentifier(bean, EntityMode.POJO).toString();
	}

	/**
	 * Returns a HQL term that can be used within a where-clause to perform
	 * a query-by-example.
	 * @since 6.4
	 */
	public static String getExampleWhereClause(Object example, String alias,
			String[] propertyNames) {

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
				if (value instanceof Collection) {
					Collection c = (Collection) value;
					hql.append("1 = 1");
					for (int j = 0; j < c.size(); j++) {
						hql.append(" and :").append(name).append("_").append(j)
								.append(" in elements(").append(alias)
								.append('.').append(name).append(')');
					}
				}
				else {
					hql.append(alias).append('.').append(name);
					hql.append(" = :").append(name);
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
	public static void setCollectionValueParams(Query query,
			String[] names, Object object) {

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			Object value;
			if (object instanceof Map) {
				value = ((Map) object).get(name);
			}
			else {
				value = PropertyUtils.getProperty(object, name);
			}
			if (value instanceof Collection) {
				int j = 0;
				Iterator values = ((Collection) value).iterator();
				while (values.hasNext()) {
					query.setParameter(name + "_" + j++, values.next());
				}
			}
		}
	}

	/**
	 * Returns a HQL term that can be used within a where-clause to perform
	 * a search. Example: <code>"(lower(&lt;alias&gt;.&lt;property[0]&gt;)
	 * like :&lt;searchParamName&gt; or lower(&lt;alias&gt;.&lt;property[1]&gt;)
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
			hql.append("lower(").append(alias).append('.').append(name);
			hql.append(") like :").append(searchParamName);
		}
		hql.append(')');
		return hql.toString();
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
				hql.append(' ').append(expression).append(' ');
			}
			hql.append(term);
		}
		return hql;
	}

	public static void addEqOrNull(Criteria c, String name, Object val) {
		if (val != null) {
			c.add(Expression.eq(name, val));
		}
		else {
			c.add(Expression.isNull(name));
		}
	}
}
