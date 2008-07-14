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
import java.util.Map;

public class GenericEntityListener<T> implements EntityListener {

	public Class<?> getEntityClass() {
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<?>) type.getActualTypeArguments()[0];
	}

	@SuppressWarnings("unchecked")
	public final boolean preSave(Object entity, Serializable id) {
		return onSave((T) entity, id);
	}	
	
	protected boolean onSave(T entity, Serializable id) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public final boolean preUpdate(Object entity, Serializable id,
			Map<String, Object> previousState) {
		
		return onUpdate((T) entity, id, previousState); 
	}
	
	protected boolean onUpdate(T entity, Serializable id, 
			Map<String, Object> previousState) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public final void preDelete(Object entity, Serializable id) {
		onDelete((T) entity, id);
	}
	
	protected void onDelete(T entity, Serializable id) {
	}

}
