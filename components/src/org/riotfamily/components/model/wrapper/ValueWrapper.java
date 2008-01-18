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

import java.util.Collection;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public abstract class ValueWrapper {

	private Long id;
	
	public Long getId() {
		return this.id;
	}

	public abstract void wrap(Object value);
	
	public Object unwrap() {
		return getValue();
	}
	
	public abstract Object getValue();
	
	public abstract void setValue(Object value);
	
	public abstract ValueWrapper deepCopy();

	/**
	 * Returns a Collection of Strings that should be used to tag the
	 * CacheItem containing the rendered content.
	 */
	public Collection getCacheTags() {
		return null;
	}

	public int hashCode() {
		Object value = getValue();
		if (value == this || value == null) {
			return super.hashCode();
		}
		return value.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof ValueWrapper) {
			Object otherValue = ((ValueWrapper) obj).getValue();
			if (otherValue != null) {
				return otherValue.equals(getValue());
			}
		}
		return super.equals(obj);
	}
	
	public String toString() {
		Object value = getValue();
		if (value == this || value == null) {
			return super.toString();
		}
		return value.toString();
	}
	
}
