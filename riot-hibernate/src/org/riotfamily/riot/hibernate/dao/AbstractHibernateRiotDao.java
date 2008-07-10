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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class AbstractHibernateRiotDao extends HibernateDaoSupport implements RiotDao {

	private Class<?> entityClass;

	public AbstractHibernateRiotDao(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getObjectId(Object entity) {
		return HibernateUtils.getIdAsString(getSessionFactory(), entity);
	}

	public Object load(String id) throws DataAccessException {
		return HibernateUtils.get(getSession(), entityClass, id);
	}

	public int getListSize(Object parent, ListParams params) throws DataAccessException {
		return -1;
	}
	
	public final Collection<?> list(Object parent, ListParams params) {
        return listInternal(parent, params);
	}
	
	protected List<?> listInternal(Object parent, ListParams params) throws DataAccessException {
		return getSession().createCriteria(entityClass).list();
	}
	
	public void save(Object entity, Object parent) throws DataAccessException {
		getSession().save(entity);
	}

	public Object merge(Object entity) throws DataAccessException {
		return getSession().merge(entity);
	}
	
	public void update(Object entity) throws DataAccessException {
	}
	
	public void delete(Object entity, Object parent) throws DataAccessException {
		getSession().delete(entity);
	}
	
}
