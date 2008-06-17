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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Base class for implementing DAOs based on plain Hibernate3 API.
 * Hibernate 3.0.1 introduced a feature called "contextual Sessions", 
 * where Hibernate itself manages one current Session per transaction. 
 * {@linkplain http://static.springframework.org/spring/docs/2.0.x/reference/orm.html#orm-hibernate-straight}
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class HibernateSupport {

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	protected Criteria createCriteria(Class<?> clazz) {
		return getSession().createCriteria(clazz);
	}
	
	protected Query createQuery(String hql) {
		return getSession().createQuery(hql);
	}
	
	protected Query createFilter(Object object, String hql) {
		return getSession().createFilter(object, hql);
	}

}
