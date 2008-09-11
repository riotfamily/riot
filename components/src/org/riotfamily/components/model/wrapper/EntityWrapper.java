package org.riotfamily.components.model.wrapper;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

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

	/* 
	 * NOTE: The original mapping was:
	 * 
	 * @Any(metaColumn=@Column(name="entity_table"), fetch=FetchType.LAZY) 
	 * @AnyMetaDef(idType="long", metaType="string", metaValues={})
	 * @JoinColumn(name="entity_id")
	 * 
	 * This causes an ObjectNotFoundException when the referenced entity
	 * has been deleted, as @NotFound(NotFoundAction.IGNORE) is currently
	 * not supported for Any-mappings. See:
	 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-3475
	 */
	
	@Type(type="org.riotfamily.riot.hibernate.support.FailSafeAnyType")
	@Columns(columns = {
	    @Column(name="entity_table"),
	    @Column(name="entity_id")
	})
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public Object getValue() {
		return value;
	}

	public void setValue(Object entity) {
		this.value = entity;
	}

}
