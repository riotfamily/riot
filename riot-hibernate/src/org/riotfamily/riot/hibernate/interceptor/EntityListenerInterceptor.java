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
package org.riotfamily.riot.hibernate.interceptor;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.type.Type;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Hibernate {@link Interceptor} that scans the ApplicationContext for beans
 * implementing the {@link EntityListener} interface and invokes the appropriate 
 * callbacks.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class EntityListenerInterceptor extends EmptyInterceptor
		implements ApplicationContextAware {

	private ApplicationContext context;
	
	private String sessionFactoryName;
	
	private SessionFactory sessionFactory;
	
	private Map<Class<?>, List<EntityListener<Object>>> listeners = Generics.newHashMap();
	
	private ThreadLocal<Interceptions> interceptions = Generics.newThreadLocal();
	
	
	public void setSessionFactoryName(String sessionFactoryName) {
		this.sessionFactoryName = sessionFactoryName;
	}

	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
		for (EntityListener<Object> listener : SpringUtils.listBeansOfType(
				context, EntityListener.class)) {

			registerListener(getEntityClass(listener), listener);
		}
	}
	
	/**
	 * Returns the generic type argument of the given EntityListener.
	 */
	private Class<?> getEntityClass(EntityListener<?> listener) {
		return Generics.getTypeArguments(EntityListener.class, listener.getClass()).get(0);		
	}
	
	private void registerListener(Class<?> entityClass, EntityListener<Object> listener) {
		List<EntityListener<Object>> list = listeners.get(entityClass);
		if (list == null) {
			list = Generics.newArrayList();
			listeners.put(entityClass, list);
		}
		list.add(listener);
	}
	
	private boolean listenerExists(Object entity) {
		return !getListeners(entity).isEmpty();
	}
	
	private List<EntityListener<Object>> getListeners(Object entity) {
		Class<? >entityClass = entity.getClass();
		List<EntityListener<Object>> result = listeners.get(entityClass);
		if (result == null) {
			for (Class<?> registeredClass : listeners.keySet()) {
				if (registeredClass.isAssignableFrom(entityClass)) {
					result = listeners.get(registeredClass);
					listeners.put(entityClass, result);
					break;
				}
			}
			if (result == null) {
				result = Collections.emptyList();
			}
		}
		return result;
	}
	
	
	private Interceptions getInterceptions() {
		Interceptions result = interceptions.get();
		if (result == null) {
			result = new Interceptions();
			interceptions.set(result);
		}
		return result;
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
			getInterceptions().entityUpdated(entity);
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
				getInterceptions().entityUpdated(entity);
			}
		}
	}
	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		for (EntityListener<Object> listener : getListeners(entity)) {
			listener.onDelete(entity);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void postFlush(Iterator entities) {
		Interceptions i = interceptions.get();
		if (i != null) {
			try {
				Session session = createTemporarySession();
				for (Object entity : i.getSavedEntites()) {
					Object mergedEntity = session.merge(entity);
					List<EntityListener<Object>> listeners = getListeners(mergedEntity);
					for (EntityListener<Object> listener : listeners) {
						listener.onSave(mergedEntity);
					}	
				}
				for (Object entity : i.getUpdatedEntites()) {
					Object mergedEntity = session.merge(entity);
					List<EntityListener<Object>> listeners = getListeners(mergedEntity);
					for (EntityListener<Object> listener : listeners) {
						listener.onUpdate(mergedEntity);
					}	
				}
				session.flush();
				session.close();
			}
			finally {
				interceptions.set(null);			
			}
		}
	}
	
	/**
	 * Lazily retrieves the SessionFactory from the ApplicationContext. If a
	 * sessionFactoryName {@link #setSessionFactoryName(String) is set}, the
	 * method will try to look up a bean with the specified name. Otherwise
	 * the ApplicationContext must contain exactly one bean that implements
	 * the {@link SessionFactory} interface.
	 * <p>
	 * Note: The SessionFactory can't be injected directly as this would result
	 * in an unresolvable cyclic dependency.
	 * </p>  
	 */
	private SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			if (sessionFactoryName != null) {
				sessionFactory = SpringUtils.getBean(context, 
						sessionFactoryName, SessionFactory.class);
			}
			else {
				sessionFactory = SpringUtils.beanOfType(context, SessionFactory.class);
			}
		}
		return sessionFactory;
	}
	
	@SuppressWarnings("deprecation")
	private Session createTemporarySession() {
		SessionFactory sf = getSessionFactory();
		Connection connection = sf.getCurrentSession().connection();
		return sf.openSession(connection, NoOpInterceptor.INSTANCE);
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
		
		private Set<Object> savedEntites;
		
		private Set<Object> updatedEntites;
		
		public void entitySaved(Object entity) {
			if (savedEntites == null) {
				savedEntites = Generics.newHashSet();
			}
			savedEntites.add(entity);
		}
		
		public void entityUpdated(Object entity) {
			if (updatedEntites == null) {
				updatedEntites = Generics.newHashSet();
			}
			updatedEntites.add(entity);
		}
		
		public Set<Object> getSavedEntites() {
			if (savedEntites == null) {
				return Collections.emptySet();
			}
			return savedEntites;
		}
		
		public Set<Object> getUpdatedEntites() {
			if (updatedEntites == null) {
				return Collections.emptySet();
			}
			return updatedEntites;
		}
		
	}
}
