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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.hibernate;

import org.hibernate.Session;
import org.riotfamily.common.util.Generics;

public abstract class TypedEntityListener<T> implements EntityListener {

	private Class<?> type;
	
	public TypedEntityListener() {
		type = Generics.getTypeArguments(TypedEntityListener.class, getClass()).get(0);
	}

	public boolean supports(Class<?> entityClass) {
		return type.isAssignableFrom(entityClass);
	}
	
	@SuppressWarnings("unchecked")
	public final void onSave(Object entity, Session session) throws Exception {
		entitySaved((T) entity, session);
	}
	
	protected void entitySaved(T entity, Session session) throws Exception {
	}
	
	@SuppressWarnings("unchecked")
	public final void onDelete(Object entity, Session session) throws Exception {
		entityDeleted((T) entity, session);
	}
	
	protected void entityDeleted(T entity, Session session) throws Exception {
	}

	@SuppressWarnings("unchecked")
	public final void onUpdate(Object entity, Object oldState, Session session) throws Exception {
		entityUpdated((T) entity, (T) oldState, session); 
	}
	
	protected void entityUpdated(T entity, T oldState, Session session) throws Exception {
	}

}
