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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@DiscriminatorValue("Component")
public class Component extends Content {

	private String type;
	
	private ComponentList list;

	public Component() {
	}

	public Component(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@ManyToOne
	@JoinColumn(name="list", insertable=false, updatable=false)
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public ComponentList getList() {
		return list;
	}

	public void setList(ComponentList list) {
		this.list = list;
	}

	public Content createCopy() {
		Component copy = new Component(type);
		copyValues(copy);
		return copy;
	}

	public static Component load(Long id) {
		return load(Component.class, id);
	}
	
}
