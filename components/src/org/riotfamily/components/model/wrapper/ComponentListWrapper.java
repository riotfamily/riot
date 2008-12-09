package org.riotfamily.components.model.wrapper;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;

@Entity
@DiscriminatorValue("ComponentList")
public class ComponentListWrapper extends ValueWrapper<ComponentList> {

	private ComponentList value;

	@ManyToOne
	@JoinColumn(name="component_list_id")
	@Cascade(CascadeType.ALL)
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
	
	public void each(ValueCallback callback) {
		for (Component component : value.getComponents()) {
			component.each(callback);
		}
	}
}
