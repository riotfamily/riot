package org.riotfamily.common.hibernate;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract base class for {@link ActiveRecord}s with a generated 
 * <code>java.lang.Long</code> identifier and field access. 
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
@MappedSuperclass
public abstract class ActiveRecordFieldSupport extends ActiveRecord 
		implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	/**
	 * Returns the identifier of this persistent instance.
	 * 
	 * @return this instance's identifier 
	 */
	public Long getId() {
		return id;
	}

}
