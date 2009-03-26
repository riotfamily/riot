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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@DiscriminatorValue("Number")
public class NumberWrapper extends ValueWrapper<Number> {

	private Number value;
	
	@Type(type="org.riotfamily.common.hibernate.ImmutableAnyType")
	@Columns(columns = {
	    @Column(name="num_type"),
	    @Column(name="num_value")
	})
	public Number getValue() {
		return value;
	}
	
	@Override
	public void setValue(Number value) {
		this.value = value;
	}
	
	public NumberWrapper deepCopy() {
		NumberWrapper copy = new NumberWrapper();
		copy.wrap(value);
		return copy;
	}
}
