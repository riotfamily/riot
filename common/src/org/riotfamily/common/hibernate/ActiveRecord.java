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
package org.riotfamily.common.hibernate;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.injection.ConfigurableBean;

/**
 * Use as base class for persistent entity beans if you prefer the
 * active record pattern.
 * <p>
 * By convention, subclasses should define a static load method:
 * <pre>
 * public static Foo load(Long id) {
 *     return load(Foo.class, id);
 * }
 * </pre>
 * <p>
 * To look up a list of entities, subclasses should define finder-methods
 * according to the following pattern:
 * <pre>
 * public static List<Foo> findByName(String name) {
 *     return query(Foo.class, "from {} where name = ?", name).find();
 * }
 * </pre> 
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @author Felix Gnass [felix.gnass at riotfamily dot org]
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
	protected static<T> T load(Class<T> clazz, Serializable id) {
		return (T) getSession().get(clazz, id);
	}

	/**
	 * Creates a {@link TypedQuery} from the given HQL string and sets the
	 * varargs as positional parameters. All occurrences of <code>{}</code> will
	 * be replaced with the entity name of the given class.
	 * <p>
	 * <b>Example:</b>
	 * <pre>
	 * public static Foo loadByName(String name) {
	 *   return query(Foo.class, "from {} where name = ?", name).load();
	 * }
	 * </pre>
	 * @param type The entity class
	 * @param hql The HQL query string
	 * @param params Positional parameters 
	 */
	protected static<T> TypedQuery<T> query(Class<T> type, String hql, Object... params) {
		String entityName = sessionFactory.getClassMetadata(type).getEntityName();
		Query query = getSession().createQuery(hql.replace("{}", entityName));
		if (params != null) {
			int index = 0;
			for (Object param : params) {
				query.setParameter(index++, param);
			}
		}
		return new TypedQuery<T>(query, type);
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
	 *        than 1 column per row
	 * @param query the Query to execute
	 * @param callback a {@link ForEachCallback} to call for every entity
	 *        returned by the given query
	 * 
	 * @see Query#scroll()
	 * @see Session#evict(Object)
	 * @see ForEachCallback
	 */
	@SuppressWarnings("unchecked")
	protected static <T> void forEach(Query query, ForEachCallback<T> callback) {
		ScrollableResults results = query.scroll();
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