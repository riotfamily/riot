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
 * artundweise GmbH, Neteye GmbH
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Alf Werder [alf dot werder at artundweise dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.support;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.LockMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Use as base class for persistent entity beans if you prefer the
 * active record pattern.
 * 
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 8.0
 */

@MappedSuperclass
public abstract class ActiveRecord {
	private Long id;
	private static transient SessionFactory sessionFactory;

	/**
	 * Returns the identifier of this persistent instance.
	 * 
	 * @return this instance's identifier 
	 */
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	/**
	 * Sets the identifier of this persistent instance.
	 * 
	 * @param id an identifier
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Persists this transient instance, first assigning a generated
	 * identifier.
	 * <p>
	 * Why is this method <code>final</code>? Changing the implementation of
	 * this method could seriously defect the whole persistence mechanism.
	 * 
	 * @see Session#save(Object)
	 */
	public final void save() {
		getSession().save(this);
	}
	
	/**
	 * Copies the state of this object onto the persistent object with the
	 * same identifier.
	 * <p>
	 * Why is this method <code>final</code>? Changing the implementation of
	 * this method could seriously defect the whole persistence mechanism.
	 *  
	 * @return a detached instance with state to be copied
	 * 
	 * @see Session#merge(Object)
	 */
	@SuppressWarnings("unchecked")
	public final <T> T merge() {
		return (T) getSession().merge(this);
	}

	/**
	 * Removes this persistent instance from the data store.
	 * <p>
	 * Why is this method <code>final</code>? Changing the implementation of
	 * this method could seriously defect the whole persistence mechanism. 
	 * 
	 * @see Session#delete(Object)
	 */
	public final void delete() {
		getSession().delete(this);
	}
	
	/**
	 * Returns the persistent instance of the given entity class with the given
	 * identifier, or null if there is no such persistent instance.
	 * <p>
	 * Under the hood {@link Session#get(Class, java.io.Serializable)} is used,
	 * not {@link Session#load(Class, java.io.Serializable)} as one might
	 * expect because of this method's name. See Hibernate documentation for
	 * a detailed discussion of the difference. 
	 * 
	 * @param clazz a persistent class
	 * @param id an identifier
	 * @return a persistent instance or null
	 * 
	 * @see Session#get(Class, java.io.Serializable)
	 * @see Session#load(Class, java.io.Serializable)
	 */
	@SuppressWarnings("unchecked")
	public static<T> T load(Class<T> clazz, Long id) {
		return (T) getSession().get(clazz, id);
	}
	
	/**
	 * Obtains the specified lock level upon this persistent instance.
	 * <p>
	 * Why is this method <code>final</code>? Changing the implementation of
	 * this method could seriously defect the whole persistence mechanism.
	 * 
	 * @param lockMode a {@link LockMode}
	 * 
	 * @see Session#lock(Object, LockMode)
	 */
	public final void lock(LockMode lockMode) {
		getSession().lock(this, lockMode);
	}

	/**
	 * Returns the Hibernate {@link Session} to use for persistence operations.
	 * 
	 * @return the Hibernate {@link Session}
	 */
	protected final static Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * Sest the Hibernate {@link SessionFactory} to use within the whole JVM.
	 *  
	 * @param sessionFactory a {@link SessionFactory}
	 */
	public static void setSessionFactory(SessionFactory sessionFactory) {
		ActiveRecord.sessionFactory = sessionFactory;
	}
	
	/**
	 * Creates a HQL {@link Query}, binding a number of values to "?"
	 * parameters in the query string. 
	 * 
	 * @param hql a query expressed in Hibernate's query language that may
	 *            contain one or more '?' parameter placeholders
	 * @param params the values of the parameters
	 * @return a newly created {@link Query}
	 */
	protected static Query createQuery(String hql, Object... params) {
		Query query = getSession().createQuery(hql);
		
		if (params != null) {
			int index = 0;
			for (Object param: params) {
				query.setParameter(index ++, param);
			}
		}
		
		return query;
	}
	
	/**
	 * Executes a HQL query, binding a number of values to "?" parameters in
	 * the query string.
	 * 
	 * @param hql a query expressed in Hibernate's query language that may
	 *            contain one or more '?' parameter placeholders
	 * @param params the values of the parameters
	 * @return a {@link List} containing the results of the query execution
	 */
	@SuppressWarnings("unchecked")
	protected static<T> List<T> find(String hql, Object... params) {
		return createQuery(hql, params).list();
	}

	/**
	 * Convenience method to return a single instance that matches the query,
	 * or null if the query returns no results.
	 * 
	 * @param hql a query expressed in Hibernate's query language that may
	 *        contain one or more '?' parameter placeholders
	 * @param params the values of the parameters 
	 * @return the single result or <code>null</code>
	 * @throws NonUniqueResultException if there is more than one matching
	 *                                  result
	 */
	@SuppressWarnings("unchecked")
	protected static<T> T load(String hql, Object... params)
		throws NonUniqueResultException {
		
		return (T) createQuery(hql, params).uniqueResult();
	}

	/**
	 * Convenience method to perform some code for every single result obtained
	 * by the given query. It's save to call this method on a result set of
	 * arbitrary length.
	 * <p>
	 * This is achieved by two design decisions: {@link Query#scroll()}
	 * is used to obtain a scrollale result set and after processing a single
	 * result, {@link Session#evict(Object)} is called for the entity just
	 * processed. So, implementors of {@link ForEachCallback} must be aware
	 * that only the currently processed entity is attached to the
	 * Hibernate {@link Session}.
	 * 
	 * @param <T> a mapped type or {@link Object[]} if the query returns more
	 *            than 1 column per row
	 * @param callback a {@link ForEachCallback} to call for every entity
	 *                 returned by the given query
	 * @param hql a query expressed in Hibernate's query language that may
	 *            contain one or more '?' parameter placeholders
	 * @param params the values of the parameters
	 * @see Query#scroll()
	 * @see Session#evict(Object)
	 * @see ForEachCallback
	 */
	@SuppressWarnings("unchecked")
	protected static <T> void forEach(ForEachCallback<T> callback, String hql,
		Object... params) {
		
		ScrollableResults results = createQuery(hql, params).scroll();
		
		while (results.next()) {
			Object[] row = results.get();
			callback.execute(row.length > 1? (T) row: (T) row[0]);
			for (Object entity: row) {
				getSession().evict(entity);
			}
		}
	}
	
	protected static interface ForEachCallback<T> {
		public void execute(T t);
	}
}