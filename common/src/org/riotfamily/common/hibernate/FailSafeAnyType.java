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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.EntityMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Transaction;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.LoadQueryInfluencers;
import org.hibernate.engine.NonFlushedChanges;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
import org.hibernate.event.EventListeners;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.jdbc.Batcher;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.AnyType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

/**
 * Custom Hibernate type that works like {@link AnyType} but does not
 * throw an {@link ObjectNotFoundException} when the referenced entity
 * has been deleted. 
 * 
 * @see http://opensource.atlassian.com/projects/hibernate/browse/HHH-3475
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class FailSafeAnyType extends AnyType {

	public FailSafeAnyType() {
		super(StandardBasicTypes.STRING, StandardBasicTypes.LONG);
	}
	
	public FailSafeAnyType(Type metaType, Type identifierType) {
		super(metaType, identifierType);
	}

	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		return super.nullSafeGet(rs, names, new FailSafeSessionWrapper(session), owner);
	}

	public Object resolve(Object value, SessionImplementor session, Object owner)
			throws HibernateException {
		
		return super.resolve(value, new FailSafeSessionWrapper(session), owner);
	}

	@Override
	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {

		return super.assemble(cached, new FailSafeSessionWrapper(session), owner);
	}

	/**
	 * We want to copy as little code as possible from the AnyType class and
	 * since the resolveAny() method (which we'd like to overwrite) is private, 
	 * we use a wrapper pattern instead.
	 */
	private static class FailSafeSessionWrapper implements SessionImplementor {
		
		private SessionImplementor session;
		
		public FailSafeSessionWrapper(SessionImplementor session) {
			this.session = session;
		}
		
		/**
		 * Quote from the Hibernate docs:
		 * <cite>
		 * When <tt>nullable</tt> is enabled, the method does not create 
		 * new proxies (but might return an existing proxy); if it does not 
		 * exist, return <tt>null</tt>.
		 * </cite>
		 */
		public Object internalLoad(String entityName, Serializable id,
				boolean eager, boolean nullable) throws HibernateException {
			
			return session.internalLoad(entityName, id, eager, true);
		}

		// -----------------------------------------------------------------
		// Delegate methods
		// -----------------------------------------------------------------
		
		public void afterScrollOperation() {
			session.afterScrollOperation();
		}

		public void afterTransactionCompletion(boolean successful,
				Transaction tx) {
			session.afterTransactionCompletion(successful, tx);
		}

		public void beforeTransactionCompletion(Transaction tx) {
			session.beforeTransactionCompletion(tx);
		}

		public String bestGuessEntityName(Object object) {
			return session.bestGuessEntityName(object);
		}

		public Connection connection() {
			return session.connection();
		}

		public int executeNativeUpdate(
				NativeSQLQuerySpecification specification,
				QueryParameters queryParameters) throws HibernateException {
			return session.executeNativeUpdate(specification, queryParameters);
		}

		public int executeUpdate(String query, QueryParameters queryParameters)
				throws HibernateException {
			return session.executeUpdate(query, queryParameters);
		}

		public void flush() {
			session.flush();
		}

		public Batcher getBatcher() {
			return session.getBatcher();
		}

		public CacheMode getCacheMode() {
			return session.getCacheMode();
		}

		public Serializable getContextEntityIdentifier(Object object) {
			return session.getContextEntityIdentifier(object);
		}

		public int getDontFlushFromFind() {
			return session.getDontFlushFromFind();
		}

		public Map<?, ?> getEnabledFilters() {
			return session.getEnabledFilters();
		}

		public EntityMode getEntityMode() {
			return session.getEntityMode();
		}

		public EntityPersister getEntityPersister(String entityName,
				Object object) throws HibernateException {
			return session.getEntityPersister(entityName, object);
		}

		public Object getEntityUsingInterceptor(EntityKey key)
				throws HibernateException {
			return session.getEntityUsingInterceptor(key);
		}

		public SessionFactoryImplementor getFactory() {
			return session.getFactory();
		}

		public String getFetchProfile() {
			return session.getFetchProfile();
		}

		public Type getFilterParameterType(String filterParameterName) {
			return session.getFilterParameterType(filterParameterName);
		}

		public Object getFilterParameterValue(String filterParameterName) {
			return session.getFilterParameterValue(filterParameterName);
		}

		public FlushMode getFlushMode() {
			return session.getFlushMode();
		}

		public Interceptor getInterceptor() {
			return session.getInterceptor();
		}

		public JDBCContext getJDBCContext() {
			return session.getJDBCContext();
		}

		public EventListeners getListeners() {
			return session.getListeners();
		}

		public Query getNamedQuery(String name) {
			return session.getNamedQuery(name);
		}

		public Query getNamedSQLQuery(String name) {
			return session.getNamedSQLQuery(name);
		}

		public PersistenceContext getPersistenceContext() {
			return session.getPersistenceContext();
		}

		public long getTimestamp() {
			return session.getTimestamp();
		}

		public String guessEntityName(Object entity) throws HibernateException {
			return session.guessEntityName(entity);
		}

		public Object immediateLoad(String entityName, Serializable id)
				throws HibernateException {
			return session.immediateLoad(entityName, id);
		}

		public void initializeCollection(PersistentCollection collection,
				boolean writing) throws HibernateException {
			session.initializeCollection(collection, writing);
		}

		public Object instantiate(String entityName, Serializable id)
				throws HibernateException {
			return session.instantiate(entityName, id);
		}

		public boolean isClosed() {
			return session.isClosed();
		}

		public boolean isConnected() {
			return session.isConnected();
		}

		public boolean isEventSource() {
			return session.isEventSource();
		}

		public boolean isOpen() {
			return session.isOpen();
		}

		public boolean isTransactionInProgress() {
			return session.isTransactionInProgress();
		}

		public Iterator<?> iterate(String query, QueryParameters queryParameters)
				throws HibernateException {
			return session.iterate(query, queryParameters);
		}

		public Iterator<?> iterateFilter(Object collection, String filter,
				QueryParameters queryParameters) throws HibernateException {
			return session.iterateFilter(collection, filter, queryParameters);
		}

		public List<?> list(CriteriaImpl criteria) {
			return session.list(criteria);
		}

		public List<?> list(NativeSQLQuerySpecification spec,
				QueryParameters queryParameters) throws HibernateException {
			return session.list(spec, queryParameters);
		}

		public List<?> list(String query, QueryParameters queryParameters)
				throws HibernateException {
			return session.list(query, queryParameters);
		}

		public List<?> listCustomQuery(CustomQuery customQuery,
				QueryParameters queryParameters) throws HibernateException {
			return session.listCustomQuery(customQuery, queryParameters);
		}

		public List<?> listFilter(Object collection, String filter,
				QueryParameters queryParameters) throws HibernateException {
			return session.listFilter(collection, filter, queryParameters);
		}

		public ScrollableResults scroll(CriteriaImpl criteria,
				ScrollMode scrollMode) {
			return session.scroll(criteria, scrollMode);
		}

		public ScrollableResults scroll(NativeSQLQuerySpecification spec,
				QueryParameters queryParameters) throws HibernateException {
			return session.scroll(spec, queryParameters);
		}

		public ScrollableResults scroll(String query,
				QueryParameters queryParameters) throws HibernateException {
			return session.scroll(query, queryParameters);
		}

		public ScrollableResults scrollCustomQuery(CustomQuery customQuery,
				QueryParameters queryParameters) throws HibernateException {
			return session.scrollCustomQuery(customQuery, queryParameters);
		}

		public void setAutoClear(boolean enabled) {
			session.setAutoClear(enabled);
		}

		public void setCacheMode(CacheMode cm) {
			session.setCacheMode(cm);
		}

		public void setFetchProfile(String name) {
			session.setFetchProfile(name);
		}

		public void setFlushMode(FlushMode fm) {
			session.setFlushMode(fm);
		}

		public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
			return session.getNonFlushedChanges();
		}

		public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
			session.applyNonFlushedChanges(nonFlushedChanges);
		}

		public LoadQueryInfluencers getLoadQueryInfluencers() {
			return session.getLoadQueryInfluencers();
		}
		
	}

}
