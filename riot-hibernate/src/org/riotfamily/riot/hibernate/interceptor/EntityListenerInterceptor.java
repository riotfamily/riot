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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.type.Type;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EntityListenerInterceptor extends EmptyInterceptor
		implements ApplicationContextAware {

	private Collection<EntityListener> listeners;
	
	private Map<Class<?>, List<EntityListener>> cachedListenerns = Generics.newHashMap();
	
	public void setApplicationContext(ApplicationContext ctx) {
		this.listeners = SpringUtils.listBeansOfType(ctx, EntityListener.class);
	}
		
	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		boolean result = false;
		List<EntityListener> listeners = getListeners(entity.getClass());
		if (!listeners.isEmpty()) {
			Map<String, Object> stateMap = createStateMap(propertyNames, state);
			for (EntityListener listener : listeners) {
				result |= listener.onInit(entity, id, stateMap);
			}
		}
		return result;
	}
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		boolean result = false;
		List<EntityListener> listeners = getListeners(entity.getClass());
		for (EntityListener listener : listeners) {
			result |= listener.onSave(entity, id);
		}
		return result;
	}
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		boolean result = false;
		List<EntityListener> listeners = getListeners(entity.getClass());
		if (!listeners.isEmpty()) {
			Map<String, Object> prevStateMap = createStateMap(propertyNames, previousState);
			for (EntityListener listener : listeners) {
				result |= listener.onUpdate(entity, id, prevStateMap);
			}
		}
		return result;
	}
	
	@Override
	public void onCollectionUpdate(Object collection, Serializable key)
			throws CallbackException {
		
		if (collection instanceof PersistentCollection 
				&& collection instanceof Collection) {
			
			Collection<?> c = (Collection<?>) collection;
			PersistentCollection pc = (PersistentCollection) collection;
			Object entity = pc.getOwner();
			
			List<EntityListener> listeners = getListeners(entity.getClass());
			if (!listeners.isEmpty()) {
				Collection<?> prevState = (Collection<?>) pc.getStoredSnapshot();
				int i = pc.getRole().lastIndexOf('.');
				String property = pc.getRole().substring(i+1);
				for (EntityListener listener : listeners) {
					listener.onUpdateCollection(entity, key, c, prevState, property);
				}
			}
		}
	}
	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		List<EntityListener> listeners = getListeners(entity.getClass());
		for (EntityListener listener : listeners) {
			listener.onDelete(entity, id);
		}
	}
	

	private Map<String, Object> createStateMap(String[] propertyNames, Object[] values) {
		if (values == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> map = Generics.newHashMap();
		int i = 0;
		for (String name : propertyNames) {
			map.put(name, values[i++]);
		}
		return map;
	}

	private List<EntityListener> getListeners(Class<?> entityClass) {
		List<EntityListener> listeners = cachedListenerns.get(entityClass);
		if (listeners == null) {
			listeners = Generics.newArrayList();
			for (EntityListener listener : this.listeners) {
				if (listener.supports(entityClass)) {
					listeners.add(listener);
				}
			}
			cachedListenerns.put(entityClass, listeners);
		}
		return listeners;
	}
	
}
