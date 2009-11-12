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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.type.Type;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * Hibernate {@link Interceptor} that scans the ApplicationContext for beans
 * implementing the {@link EntityListener} interface and invokes the appropriate 
 * callbacks.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
@SuppressWarnings("serial")
public class EntityListenerInterceptor extends EmptyInterceptor
		implements ApplicationContextAware, SessionFactoryAwareInterceptor {

	private SessionFactory sessionFactory;
	
	private Collection<EntityListener> listeners;
	
	private Map<Class<?>, List<EntityListener>> listenerMap = Generics.newHashMap();
	
	private static ThreadLocal<Interceptions> interceptions = Generics.newThreadLocal();

	private ThreadBoundHibernateTemplate template;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		template = new ThreadBoundHibernateTemplate(sessionFactory);
		template.setAlwaysUseNewSession(true);
	}

	public void setApplicationContext(ApplicationContext context) {
		this.listeners = SpringUtils.listBeansOfType(context, EntityListener.class);
	}
		
	private boolean listenerExists(Object entity) {
		return !getListeners(entity).isEmpty();
	}
	
	private List<EntityListener> getListeners(Object entity) {
		Class<?> entityClass = entity.getClass();
		List<EntityListener> result = listenerMap.get(entityClass);
		if (result == null) {
			result = Generics.newArrayList();
			listenerMap.put(entityClass, result);
			for (EntityListener listener : listeners) {
				if (listener.supports(entityClass)) {
					result.add(listener);
				}
			}
		}
		return result;
	}
	
	private Interceptions getInterceptions() {
		Interceptions i = interceptions.get(); 
		if (i == null) {	
			i = new Interceptions(sessionFactory);
			interceptions.set(i);
		}
		return i;
	}
	
	@Override
	public String getEntityName(Object object) {
		if (object instanceof ActiveRecord) {
			return object.getClass().getName();
		}
		return null;
	}
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {

		if (listenerExists(entity)) {
			getInterceptions().entitySaved(entity);
		}
		return false;
	}
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		if (listenerExists(entity)) {
			getInterceptions().entityUpdated(entity, id);
		}
		return false;
	}
	
	@Override
	public void onCollectionUpdate(Object collection, Serializable key)
			throws CallbackException {
		
		if (collection instanceof PersistentCollection) {
			PersistentCollection pc = (PersistentCollection) collection;
			Object entity = pc.getOwner();
			if (listenerExists(entity)) {
				getInterceptions().entityUpdated(entity, key);
			}
		}
	}
	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {
		
		getInterceptions().entityDeleted(entity);
		for (EntityListener listener : getListeners(entity)) {
			try {
				listener.onDelete(entity, sessionFactory.getCurrentSession());
			}
			catch (Exception e) {
				throw new CallbackException(e);
			}
		}
	}
		
	@Override
	@SuppressWarnings("unchecked")
	public void postFlush(Iterator entities) {
		final Interceptions i = getInterceptions();
		if (!i.isEmpty()) {
			interceptions.set(i.nested());
			try {
				template.execute(new HibernateCallbackWithoutResult() {
					@Override
					public void doWithoutResult(Session session) throws Exception {
						for (Object entity : i.getSavedEntities()) {
							for (EntityListener listener : getListeners(entity)) {
								listener.onSave(entity, session);
							}	
						}
						for (Update update : i.getUpdates()) {
							Object newState = update.getEntity();
							for (EntityListener listener : getListeners(newState)) {
								listener.onUpdate(newState, update.getOldState(), session);
							}	
						}
						session.flush();
					}
				});
				sessionFactory.getCurrentSession().flush();
			}
			catch (Exception e) {
				throw new CallbackException(e);
			}
			finally {
				i.closeOldStateSession();
			}
		}
	}
	
	@Override
	public void afterTransactionCompletion(Transaction tx) {
		interceptions.remove();
	}
			
	/**
	 * Interceptor that does nothing. This implementation is used with the
	 * temporary session created in {@link #postFlush(Iterator)} to prevent
	 * endless recursions. 
	 */
	private static class NoOpInterceptor extends EmptyInterceptor {
		static NoOpInterceptor INSTANCE = new NoOpInterceptor();
	}
	
	private static class Interceptions {
		
		private Session oldStateSession;
		
		private List<Object> savedEntities;
		
		private List<Update> updates;
		
		private Set<Object> ignore;
		
		public Interceptions(SessionFactory sessionFactory) {
			this(sessionFactory, Generics.newHashSet());
		}
		
		public Interceptions(SessionFactory sessionFactory, Set<Object> ignore) {
			oldStateSession = SessionFactoryUtils.getNewSession(sessionFactory, NoOpInterceptor.INSTANCE);
			this.ignore = ignore;
		}
		
		public void closeOldStateSession() {
			oldStateSession.close();
		}

		public void entitySaved(Object entity) {
			if (!ignore.contains(entity)) {
				if (savedEntities == null) {
					savedEntities = Generics.newArrayList();
				}
				savedEntities.add(entity);
				ignore.add(entity);
			}
		}
		
		public void entityUpdated(Object entity, Serializable id) {
			if (!ignore.contains(entity)) {
				if (updates == null) {
					updates = Generics.newArrayList();
				}
				Object oldState = oldStateSession.get(entity.getClass(), id);
				updates.add(new Update(oldState, entity));
				ignore.add(entity);
			}
		}
		
		public void entityDeleted(Object entity) {
			ignore.add(entity);
		}
				
		public List<Object> getSavedEntities() {
			return Generics.emptyIfNull(savedEntities);
		}
		
		public List<Update> getUpdates() {
			return Generics.emptyIfNull(updates);
		}
				
		public boolean isEmpty() {
			return savedEntities == null && updates == null;
		}
		
		public Interceptions nested() {
			return new Interceptions(oldStateSession.getSessionFactory(), ignore);
		}
	}
	
	private static class Update {
		
		private Object entity;
		
		private Object oldState;
		
		public Update(Object oldState, Object entity) {
			this.oldState = oldState;
			this.entity = entity;
		}
				
		public Object getOldState() {
			return oldState;
		}
		
		public Object getEntity() {
			return entity;
		}
		
	}
}
