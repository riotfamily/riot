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
