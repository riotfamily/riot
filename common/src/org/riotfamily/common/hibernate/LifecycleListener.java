package org.riotfamily.common.hibernate;

import org.hibernate.Session;

public class LifecycleListener extends TypedEntityListener<Lifecycle> {
	
	@Override
	protected void entitySaved(Lifecycle entity, Session session) throws Exception {
		entity.onSave();
	}
	
	@Override
	protected void entityDeleted(Lifecycle entity, Session session) throws Exception {
		entity.onDelete();
	}
	
	@Override
	protected void entityUpdated(Lifecycle entity, Lifecycle oldState, Session session) throws Exception {
		entity.onUpdate(oldState);
	}
}
