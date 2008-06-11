package org.riotfamily.components.model.wrapper;

public class EntityWrapper extends ValueWrapper {

	private Object entity;
	
	@Override
	public ValueWrapper deepCopy() {
		EntityWrapper copy = new EntityWrapper();
		copy.wrap(entity);
		return copy;
	}

	@Override
	public Object getValue() {
		return entity;
	}

	@Override
	public void setValue(Object value) {
		this.entity = value;
	}

}
