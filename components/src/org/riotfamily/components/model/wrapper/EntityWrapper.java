package org.riotfamily.components.model.wrapper;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@DiscriminatorValue("Entity")
public class EntityWrapper extends ValueWrapper<Object> {

	private Object value;
	
	@Override
	public EntityWrapper deepCopy() {
		EntityWrapper copy = new EntityWrapper();
		copy.wrap(value);
		return copy;
	}

	@Any(metaColumn=@Column(name="entity_table"), fetch=FetchType.LAZY)
	@AnyMetaDef(idType="long", metaType="string", metaValues={})
	@JoinColumn(name="entity_id")
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public Object getValue() {
		return value;
	}

	public void setValue(Object entity) {
		this.value = entity;
	}

}
