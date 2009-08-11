package org.riotfamily.common.hibernate;

public interface Lifecycle {

	public void onSave();

	public void onDelete();
	
	public void onUpdate(Object oldState);
	
}
