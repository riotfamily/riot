package org.riotfamily.components.model.wrapper;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.riotfamily.components.model.ComponentList;

@Entity
@DiscriminatorValue("ComponentList")
public class ComponentListWrapper extends ValueWrapper<ComponentList> {

	private ComponentList value;

	@ManyToOne
	@JoinColumn(name="component_list_id")
	public ComponentList getValue() {
		return value;
	}
	
	public void setValue(ComponentList value) {
		this.value = value;
	}

	public ComponentListWrapper deepCopy() {
		ComponentListWrapper copy = new ComponentListWrapper();
		copy.wrap(value.createCopy());
		return copy;
	}	
	
}
