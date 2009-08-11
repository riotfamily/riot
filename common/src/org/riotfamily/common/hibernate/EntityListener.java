package org.riotfamily.common.hibernate;

import org.hibernate.Session;

public interface EntityListener {

	public boolean supports(Class<?> entityClass);
	
	public void onSave(Object entity, Session session) throws Exception;
	
	public void onDelete(Object entity, Session session) throws Exception;
	
	public void onUpdate(Object entity, Object oldState, Session session) throws Exception;
	
}
