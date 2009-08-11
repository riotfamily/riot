package org.riotfamily.common.hibernate;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract base class for {@link ActiveRecord}s with a generated 
 * <code>java.lang.Long</code> identifier and bean-style property access.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
@MappedSuperclass
public abstract class ActiveRecordBeanSupport extends ActiveRecord 
		implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Long id;
	
	/**
	 * Returns the identifier of this persistent instance.
	 * 
	 * @return this instance's identifier 
	 */
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	/**
	 * Sets the identifier of this persistent instance.
	 * 
	 * @param id an identifier
	 */
	public void setId(Long id) {
		this.id = id;
	}

}
