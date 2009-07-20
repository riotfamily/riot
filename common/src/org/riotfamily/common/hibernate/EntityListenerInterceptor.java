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
package org.riotfamily.common.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
	
	private ThreadBoundHibernateTemplate template;
	
	private Collection<EntityListener> listeners;
	
	private Map<Class<?>, List<EntityListener>> mergedListeners = Generics.newHashMap();
	
	private ThreadLocal<Interceptions> interceptions = Generics.newThreadLocal();

	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.template = new ThreadBoundHibernateTemplate(sessionFactory);
		this.template.setAlwaysUseNewSession(true);
		this.template.setEntityInterceptor(NoOpInterceptor.INSTANCE);
	}

	public void setApplicationContext(ApplicationContext context) {
		this.listeners = SpringUtils.listBeansOfType(context, EntityListener.class);
	}
		
	private boolean listenerExists(Object entity) {
		return !getListeners(entity).isEmpty();
	}
	
	private List<EntityListener> getListeners(Object entity) {
		Class<?> entityClass = entity.getClass();
		List<EntityListener> result = mergedListeners.get(entityClass);
		if (result == null) {
			result = Generics.newArrayList();
			mergedListeners.put(entityClass, result);
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
			String[] propertyNames, Type[] types) {
		
		try {
			Session session = sessionFactory.getCurrentSession();
			for (EntityListener listener : getListeners(entity)) {
				listener.onDelete(entity, session);
			}
		}
		catch (Exception e) {
			throw new CallbackException(e);
		}
	}
		
	@Override
	@SuppressWarnings("unchecked")
	public void postFlush(Iterator entities) {
		final Interceptions i = getInterceptions();
		try {
			template.execute(new HibernateCallbackWithoutResult() {
				public void doWithoutResult(Session session) throws Exception {
					for (Object entity : i.getSavedEntites()) {
						Object mergedEntity = session.merge(entity);
						for (EntityListener listener : getListeners(mergedEntity)) {
							listener.onSave(mergedEntity, session);
						}	
					}
					for (Update update : i.getUpdates()) {
						Object newState = session.merge(update.getEntity());
						for (EntityListener listener : getListeners(newState)) {
							listener.onUpdate(newState, update.getOldState(), session);
						}	
					}
				}
			});
		}
		catch (Exception e) {
			throw new CallbackException(e);
		}
		finally {
			i.closeOldStateSession();
			interceptions.remove();			
		}
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
		
		private Set<Object> savedEntites;
		
		private Set<Update> updates;
		
		public Interceptions(SessionFactory sessionFactory) {
			oldStateSession = SessionFactoryUtils.getNewSession(
					sessionFactory, NoOpInterceptor.INSTANCE);
		}
		
		public void closeOldStateSession() {
			oldStateSession.close();
		}

		public void entitySaved(Object entity) {
			if (savedEntites == null) {
				savedEntites = Generics.newHashSet();
			}
			savedEntites.add(entity);
		}
		
		public void entityUpdated(Object entity, Serializable id) {
			if (updates == null) {
				updates = Generics.newHashSet();
			}
			Object oldState = oldStateSession.get(entity.getClass(), id);
			updates.add(new Update(oldState, entity));
		}
		
		public Set<Object> getSavedEntites() {
			if (savedEntites == null) {
				return Collections.emptySet();
			}
			return savedEntites;
		}
		
		public Set<Update> getUpdates() {
			if (updates == null) {
				return Collections.emptySet();
			}
			return updates;
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
