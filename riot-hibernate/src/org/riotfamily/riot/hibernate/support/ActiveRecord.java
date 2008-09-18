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
 *   alf
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.support;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Use as base class for persistent entity beans if you prefer the
 * active record pattern.
 * 
 * @author Alf Werder <alf dot werder at artundweise dot de>
 * @since 8.0
 */

@MappedSuperclass
public abstract class ActiveRecord {
	private Long id;
	private static transient SessionFactory sessionFactory;

	/**
	 * Return the identifier of this instance.
	 * 
	 * @return this instance's identifier 
	 */
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	/**
	 * Set the identifier of this instance.
	 * 
	 * @param id an identifier
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Persist this transient instance, first assigning a generated
	 * identifier.
	 * 
	 * @see Session#save(Object)
	 */
	public void save() {
		getSession().save(this);
	}
	
	/**
	 * Copy the state of this object onto the persistent object with the
	 * same identifier.
	 * 
	 * @return a detached instance with state to be copied
	 * 
	 * @see Session#merge(Object)
	 */
	//TODO: Revisit for proper return type after learning more about java generics
	@SuppressWarnings("unchecked")
	public <T> T merge() {
		return (T) getSession().merge(this);
	}

	/**
	 * Remove this persistent instance from the datastore.
	 * 
	 * @see Session#delete(Object)
	 */
	public void delete() {
		getSession().delete(this);
	}
	
	/**
	 * Return the persistent instance of the given entity class with the given
	 * identifier, or null if there is no such persistent instance.
	 * 
	 * Under the hood {@link Session#get(Class, java.io.Serializable)} is used,
	 * not {@link Session#load(Class, java.io.Serializable)} as one might
	 * expect because of the method's name. See the hibernate documentation for
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
	 * Obtain the specified lock level upon the given object.
	 * 
	 * @param lockMode a {@link LockMode}
	 * 
	 * @see Session#lock(Object, LockMode)
	 */
	public void lock(LockMode lockMode) {
		getSession().lock(this, lockMode);
	}

	/**
	 * Return the Hibernate {@link Session} to use for persistence operations.
	 * 
	 * @return the Hibernate {@link Session}
	 */
	protected static Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * Set the Hibernate {@link SessionFactory} to use within the whole JVM.
	 *  
	 * @param sessionFactory a {@link SessionFactory}
	 */
	public static void setSessionFactory(SessionFactory sessionFactory) {
		ActiveRecord.sessionFactory = sessionFactory;
	}
	
	/**
	 * Execute an HQL query, binding a number of values to "?" parameters in
	 * the query string.
	 * 
	 * @param hql a query expressed in Hibernate's query language that may
	 *            contain one or more '?' parameter placeholders
	 * @param params the values of the parameters 
	 * @return a {@link List} containing the results of the query execution
	 */
	@SuppressWarnings("unchecked")
	protected static<T> List<T> find(String hql, Object... params) {
		Query query =
			getSession().createQuery(hql);
		if (params != null) {
			int index = 0;
			for (Object param: params) {
				query.setParameter(index ++, param);
			}
		}
		return query.list();
	}
}
