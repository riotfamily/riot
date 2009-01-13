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
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

public class GenericEntityListener<T> implements EntityListener {

	public boolean supports(Class<?> entityClass) {
		return getEntityClass().isAssignableFrom(entityClass);
	}
	
	protected Class<?> getEntityClass() {
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<?>) type.getActualTypeArguments()[0];
	}

	
	@SuppressWarnings("unchecked")
	public final boolean onInit(Object entity, Serializable id,
			Map<String, Object> state) {
		
		return init((T) entity, id, state);
	}
	
	protected boolean init(T entity, Serializable id, 
			Map<String, Object> state) {
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public final boolean onSave(Object entity, Serializable id) {
		return save((T) entity, id);
	}	
	
	protected boolean save(T entity, Serializable id) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public final boolean onUpdate(Object entity, Serializable id,
			Map<String, Object> previousState) {
		
		return update((T) entity, id, previousState); 
	}
	
	protected boolean update(T entity, Serializable id, 
			Map<String, Object> previousState) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public final void onUpdateCollection(Object entity, Serializable id,
			Collection<?> c, Collection<?> previousState, String property) {
		
		updateCollection((T) entity, id, c, previousState, property); 
	}
	
	protected void updateCollection(T entity, Serializable id, 
			Collection<?> c, Collection<?> previousState, String property) {
	}

	@SuppressWarnings("unchecked")
	public final void onDelete(Object entity, Serializable id) {
		delete((T) entity, id);
	}
	
	protected void delete(T entity, Serializable id) {
	}

}
