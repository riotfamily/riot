package org.riotfamily.components.model.wrapper;

import org.riotfamily.components.model.ComponentList;

public class ComponentListWrapper extends ValueWrapper {

	private ComponentList componentList;

	public ComponentListWrapper() {
	}

	public void setValue(Object object) {
		this.componentList = (ComponentList) object;
	}

	public Object getValue() {
		return componentList;
	}

	public ValueWrapper deepCopy() {
		ComponentListWrapper copy = new ComponentListWrapper();
		copy.wrap(componentList.createCopy());
		return copy;
	}	
	
}
