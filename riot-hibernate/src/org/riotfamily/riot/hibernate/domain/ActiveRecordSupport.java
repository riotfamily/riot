/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.Hibernate;

/**
 * Abstract base class for {@link ActiveRecord}s with a <code>Long</code> 
 * generated identifier. The class is {@link Serializable} and provides default
 * implementations for both {@link #hashCode()} and 
 * {@link #equals(Object) equals()}.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
@MappedSuperclass
public abstract class ActiveRecordSupport extends ActiveRecord 
		implements Serializable {
	
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
	
	/**
	 * Default implementation that always returns <code>0</code>. This 
	 * implementation is very inefficient for large collections as all instances
	 * will end up in the same hash bucket. Yet this is the only safe 
	 * generic implementation, as it guarantees that the hashCode does not 
	 * change while the object is contained in a collection.
	 * <p>
	 * If you plan to put your entities into large HashSets or HashMaps, you 
	 * should consider to overwrite this method and implement it based 
	 * on an immutable business key.
	 * <p>
	 * If your entities don't have such an immutable key you can use the id 
	 * property instead, but keep in mind that collections won't be intact if 
	 * you save a transient object after adding it to a set or map.
	 * 
	 * @see http://www.hibernate.org/109.html
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * Generic implementation that first checks for object identity. In case 
	 * the objects are not the same instance, but this instance has a non-null 
	 * id and the other object also extends ActiveRecordSupport, the method 
	 * will compare both id values. If the ids are equal, the method calls 
	 * {@link Hibernate#getClass(Object))} for both objects and checks if the
	 * classes are the same.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (id != null && obj instanceof ActiveRecordSupport) {
			ActiveRecordSupport other = (ActiveRecordSupport) obj;
			if (id.equals(other.getId())) {
				return Hibernate.getClass(this).equals(
						Hibernate.getClass(other));
			}
		}
		return false;
	}

}
