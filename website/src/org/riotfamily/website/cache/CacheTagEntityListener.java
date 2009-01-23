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
package org.riotfamily.website.cache;

import java.io.Serializable;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.type.Type;
import org.riotfamily.cachius.CacheService;

/**
 * Hibernate Interceptor that invalidates tagged cache items whenever an entity 
 * with a {@link TagCacheItems} annotation is modified or deleted.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CacheTagEntityListener extends EmptyInterceptor {

	private CacheService cacheService;
	
	public CacheTagEntityListener(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.tag(entity.getClass(), id);
		}
		return false;
	}		
	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.invalidate(cacheService, entity.getClass(), id);
		}
	}
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.invalidate(cacheService, entity.getClass());
		}
		return false;
	}
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
	
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.invalidate(cacheService, entity.getClass(), id);
		}
		return false;
	}
	
	@Override
	public void onCollectionUpdate(Object collection, Serializable key)
			throws CallbackException {
		
		if (collection instanceof PersistentCollection) {
			PersistentCollection pc = (PersistentCollection) collection;
			Object entity = pc.getOwner();
			if (entity != null && entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
				CacheTagUtils.invalidate(cacheService, entity.getClass(), pc.getKey());		
			}
		}
	}
	
}
