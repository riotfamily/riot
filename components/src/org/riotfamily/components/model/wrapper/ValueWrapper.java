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
package org.riotfamily.components.model.wrapper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@Table(name="riot_value_wrappers")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
public abstract class ValueWrapper<T> {

	private Long id;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Wraps the given value. The default implementation delegates the call to
	 * {@link #setValue(Object)}.
	 */
	@SuppressWarnings("unchecked")
	public void wrap(Object value) {
		setValue((T) value);
	}
	
	/**
	 * Returns the wrapped value. The default implementation delegates the 
	 * call to {@link #getValue()}. Subclasses that wrap complex values (values
	 * with nested wrappers) must overwrite this method to perform a 
	 * "deep-unwrapping".
	 */
	public T unwrap() {
		return getValue();
	}
	
	/**
	 * Returns the value that is passed to the form element when the value 
	 * is edited. Subclasses will usually return the wrapped value, except for
	 * complex wrappers (like {@link ListWrapper} and {@link MapWrapper}) which
	 * return a self-reference.
	 */
	@Transient
	public abstract T getValue();
	
	public abstract void setValue(T value);
	
	/**
	 * Creates a deep copy. Subclasses will usually just create a new wrapper 
	 * instance and and invoke {@link #setValue(Object)} with the value returned
	 * by {@link #getValue()}. Complex wrappers have to make sure that the 
	 * deepCopy method is invoked for all nested values too. 
	 */
	public abstract ValueWrapper<T> deepCopy();

	/**
	 * Delegates the call to the hashCode method of the wrapped object. 
	 */
	public int hashCode() {
		Object value = getValue();
		if (value == this || value == null) {
			return super.hashCode();
		}
		return value.hashCode();
	}
	
	/**
	 * Delegates the call to the equals method of the wrapped object. 
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj instanceof ValueWrapper) {
			ValueWrapper<T> other = (ValueWrapper<T>) obj;
			T otherValue = other.getValue();
			if (otherValue != null) {
				return otherValue.equals(getValue());
			}
		}
		return false;
	}
	
	/**
	 * Delegates the call to the toString method of the wrapped object. 
	 */
	public String toString() {
		Object value = getValue();
		if (value == this || value == null) {
			return super.toString();
		}
		return value.toString();
	}
	
}
