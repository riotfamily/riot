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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.support;

import java.io.Serializable;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.util.PropertyUtils;
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
			properties = PropertyUtils.getProperties(example);
		}
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			Object value = properties.get(name);
			if (value != null) {
				if (hql.length() > 0) {
					hql.append(" and ");
				}
				hql.append(alias).append('.').append(name);
				hql.append(" = :").append(name);
			}
		}
		return hql.length() > 0 ? hql.toString() : null;
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
}
