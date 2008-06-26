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
package org.riotfamily.riot.hibernate.support;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * Class that provides helper methods to work with contextual sessions.
 * All HibernateExceptions are caught and converted to DataAccessExceptions.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HibernateHelper extends HibernateSupport {

	private String defaultCacheRegion = null;

	public HibernateHelper(SessionFactory sessionFactory) {
		this(sessionFactory, null);
	}

	public HibernateHelper(SessionFactory sessionFactory,
			String defaultCacheReqion) {

		setSessionFactory(sessionFactory);
		this.defaultCacheRegion = defaultCacheReqion;
	}

	/**
	 * Returns the current session.
	 * @see SessionFactory#getCurrentSession()
	 */
	public Session getSession() {
		try {
			return super.getSession();
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Creates a Criteria for the given Class.
	 */
	public Criteria createCriteria(Class<?> clazz) {
		try {
			return super.createCriteria(clazz);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Creates a cacheable Criteria for the given Class, using the helper's
	 * defaultCacheRegion.
	 */
	public Criteria createCacheableCriteria(Class<?> clazz) {
		return createCacheableCriteria(defaultCacheRegion, clazz);
	}

	/**
	 * Creates a cacheable Criteria for the given Class, using the specified
	 * cache region.
	 */
	public Criteria createCacheableCriteria(String cacheRegion, Class<?> clazz) {
		try {
			Criteria c = super.createCriteria(clazz);
			c.setCacheRegion(cacheRegion);
			c.setCacheable(true);
			return c;
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Creates Query for the given HQL.
	 */
	public Query createQuery(String hql) {
		try {
			return super.createQuery(hql);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Creates a cacheable Query for the given HQL, using the helper's
	 * defaultCacheRegion.
	 */
	public Query createCacheableQuery(String hql) {
		return createCacheableQuery(defaultCacheRegion, hql);
	}

	/**
	 * Creates a cacheable Query for the given HQL, using the specified
	 * cache region.
	 */
	public Query createCacheableQuery(String cacheRegion, String hql) {
		try {
			Query query = super.createQuery(hql);
			query.setCacheRegion(cacheRegion);
			query.setCacheable(true);
			return query;
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Creates filter for the given object.
	 */
	public Query createFilter(Object object, String hql) {
		try {
			return super.createFilter(object, hql);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}
	
	/**
	 * Returns the persistent instance of the given Class, assuming that the
	 * instance exists.
	 * @see Session#load(Class, Serializable)
	 */
	@SuppressWarnings("unchecked")
	public<T> T load(Class<T> clazz, Serializable id) throws DataAccessException {
		try {
			return (T) getSession().load(clazz, id);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Returns the persistent instance of the given Class, or <code>null</code>
	 * if no such instance exists.
	 * @see Session#get(Class, Serializable)
	 */
	@SuppressWarnings("unchecked")
	public<T> T get(Class<T> clazz, Serializable id) throws DataAccessException {
		try {
			return (T) getSession().get(clazz, id);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Persists the given transient instance.
	 * @see Session#save(Object)
	 */
	public Serializable save(Object object) throws DataAccessException {
		try {
			return getSession().save(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}
	
	/**
	 * Persists the given transient instance.
	 * @see Session#persist(Object)
	 */
	public void persist(Object object) throws DataAccessException {
		try {
			getSession().persist(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}
	
	/**
	 * Either saves or updates the given instance, depending upon resolution 
	 * of the unsaved-value checks.
	 */
	public void saveOrUpdate(Object object) throws DataAccessException {
		try {
			getSession().saveOrUpdate(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Updates the given persistent instance.
	 * @see Session#update(Object)
	 */
	public void update(Object object) throws DataAccessException {
		try {
			getSession().update(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Merges the given detached instance.
	 * @see Session#merge(Object)
	 */
	@SuppressWarnings("unchecked")
	public<T> T merge(T object) throws DataAccessException {
		try {
			return (T) getSession().merge(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Obtain the specified lock leve upon the given object.
	 * @see Session#lock(Object, LockMode)
	 */
	public void lock(Object object, LockMode lockMode) throws DataAccessException {
		try {
			getSession().lock(object, lockMode);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Deletes the given persistent instance.
	 * @see Session#delete(Object)
	 */
	public void delete(Object object) throws DataAccessException {
		try {
			getSession().delete(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Re-reads the state of the given persistent or detached instance.
	 * @see Session#refresh(Object)
	 */
	public void refresh(Object object) throws DataAccessException {
		try {
			getSession().refresh(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	/**
	 * Forces the current session to flush.
	 * @see Session#flush()
	 */
	public void flush() throws DataAccessException {
		try {
			getSession().flush();
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}
	
	/**
	 * Remove this instance from the session cache.
	 * @see Session#evict(Object)
	 */
	public void evict(Object object) throws DataAccessException {
		try {
			getSession().evict(object);
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public<T> List<T> list(Query query) throws DataAccessException {
		try {
			return query.list();
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public<T> T uniqueResult(Query query) throws DataAccessException {
		try {
			return (T) query.uniqueResult();
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public<T> List<T> list(Criteria c) throws DataAccessException {
		try {
			return c.list();
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public<T> T uniqueResult(Criteria c) throws DataAccessException {
		try {
			return (T) c.uniqueResult();
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	public void setParameter(Query query, String name, Object val) {
		if (val != null) {
			try {
				query.setParameter(name, val);
			}
			catch (HibernateException e) {
				throw SessionFactoryUtils.convertHibernateAccessException(e);
			}
		}
	}

	public int executeUpdate(Query query) {
		try {
			return query.executeUpdate();
		}
		catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

}
