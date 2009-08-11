package org.riotfamily.common.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.injection.ConfigurableBean;

/**
 * Use as base class for persistent entity beans if you prefer the
 * active record pattern.
 * 
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 8.0
 */
public abstract class ActiveRecord extends ConfigurableBean {
	
	private static SessionFactory sessionFactory;
	
	final static void setSessionFactory(SessionFactory sessionFactory) {
		ActiveRecord.sessionFactory = sessionFactory;
	}
	
	protected static final SessionFactory getSessionFactory() {
		return ActiveRecord.sessionFactory;
	}
	
	/**
	 * Returns the Hibernate {@link Session} to use for persistence operations.
	 * 
	 * @return the Hibernate {@link Session}
	 */
	protected final static Session getSession() {
		return getSessionFactory().getCurrentSession();
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
	 * Updates the persistent instance with the same identifier as this 
	 * detached instance. If there is a persistent instance with the same 
	 * identifier, an exception is thrown.
	 *
	 * @see Session#update(Object)
	 * @deprecated Please use {@link #merge()} instead to ensure JPA compatibility.
	 */
	public final void update() {
		getSession().update(this);
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

		return new QueryWrapper(query);
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
	protected static<T> T load(Class<T> clazz, Serializable id) {
		return (T) getSession().get(clazz, id);
	}
	
	/**
	 * Convenience method to return a single instance that matches the query,
	 * or null if the query returns no results.
	 * 
	 * @param hql a query expressed in Hibernate's query language that may
	 *        contain one or more '?' parameter placeholders
	 * @param params the values of the parameters 
	 * @return the single result or <code>null</code>
	 * @throws NonUniqueResultException if there is more than one matching result
	 */
	@SuppressWarnings("unchecked")
	protected static<T> T load(String hql, Object... params)
		throws NonUniqueResultException {
		
		return (T) createQuery(hql, params).uniqueResult();
	}
	
	/**
	 * Convenience method to return a single instance with the given property
	 * value or null if the query returns no results.
	 * 
	 * @param clazz a persistent class 
	 * @param property the property name
	 * @param value the property value
	 * @return the single result or <code>null</code>
	 * @throws NonUniqueResultException if there is more than one matching result
	 */
	@SuppressWarnings("unchecked")
	protected static<T> T loadByProperty(Class<T> clazz, String property, 
			Object value) throws NonUniqueResultException {
		
		return (T) createQuery("from " + clazz.getName() 
				+ " where " + property + " = ?", value).uniqueResult();
	}

	/**
	 * Convenience method to perform some code for every single result obtained
	 * by the given query. It's save to call this method on a result set of
	 * arbitrary length.
	 * <p>
	 * This is achieved by two design decisions: {@link Query#scroll()}
	 * is used to obtain a scrollable result set and after processing a single
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
	
	/**
	 * Default implementation that always returns <code>0</code>. This 
	 * implementation is very inefficient for large collections as all instances
	 * will end up in the same hash bucket. Yet this is the only safe 
	 * generic implementation, as it guarantees that the hashCode does not 
	 * change while the object is contained in a collection.
	 * <p>
	 * If you plan to put your entities into large HashSets or HashMaps, you 
	 * should consider to overwrite this method and implement it based 
	 * on an immutable business key.
	 * <p>
	 * If your entities don't have such an immutable key you can use the id 
	 * property instead, but keep in mind that collections won't be intact if 
	 * you save a transient object after adding it to a set or map.
	 * 
	 * @see http://www.hibernate.org/109.html
	 */
	@Override
	public int hashCode() {
		return 0;
	}
	
	/**
	 * Generic implementation that uses the Hibernate meta-data API to
	 * compare the identifiers.
	 * 
	 * @see ActiveRecordUtils#equals(ActiveRecord, Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return ActiveRecordUtils.equals(this, obj);
	}
	
	/**
	 * Generic implementation that returns a String with the pattern 
	 * <code>&lt;className&gt;#&lt;id&gt;</code> for persistent objects, or 
	 * <code>&lt;className&gt;@&lt;identityHashCode&gt;</code> if the instance
	 * is unsaved.
	 * 
	 * @see ActiveRecordUtils#toString(ActiveRecord)
	 */
	@Override
	public String toString() {
		return ActiveRecordUtils.toString(this);
	}
	
}