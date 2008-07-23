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
import java.util.Map;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.riot.hibernate.interceptor.EntityListener;

/**
 * EntityListener that invalidates tagged cache items whenever an entity with
 * a {@link TagCacheItems} annotation is modified or deleted.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CacheTagEntityListener implements EntityListener {

	private CacheService cacheService;
	
	public CacheTagEntityListener(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public boolean supports(Class<?> entityClass) {
		return entityClass.isAnnotationPresent(TagCacheItems.class);
	}
		
	public boolean preInit(Object entity, Serializable id,
			Map<String, Object> state) {
		
		CacheTagUtils.tag(entity.getClass(), id);
		return false;
	}
	
	public void preDelete(Object entity, Serializable id) {
		CacheTagUtils.invalidate(cacheService, entity.getClass(), id);
	}

	public boolean preSave(Object entity, Serializable id) {
		CacheTagUtils.invalidate(cacheService, entity.getClass());
		return false;
	}

	public boolean preUpdate(Object entity, Serializable id,
			Map<String, Object> previousState) {
	
		CacheTagUtils.invalidate(cacheService, entity.getClass(), id);
		return false;
	}

}
