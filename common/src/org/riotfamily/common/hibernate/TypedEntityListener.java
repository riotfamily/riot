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
