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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name="riot_component_lists")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
public class ComponentList {

	private Long id;

	private List<Component> components;

	public ComponentList() {
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany
	@JoinColumn(name="list")
	@IndexColumn(name="list_pos")
	@Cascade(CascadeType.ALL)
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
	public List<Component> getComponents() {
		if (components == null) {
			components = new ArrayList<Component>();
		}
		return components;
	}

	public void setComponents(List<Component> list) {
		this.components = list;
	}

	public void insertComponent(Component component, int position) {
		component.setList(this);
		if (position >= 0) {
			getComponents().add(position, component);
		}
		else {
			getComponents().add(component);
		}
	}
	
	public void appendComponent(Component component) {
		insertComponent(component, -1);
	}
	
	@Transient
	public int getSize() {
		return getComponents().size();
	}
	
	public int indexOf(Component component) {
		return getComponents().indexOf(component);
	}
	
	public ComponentList createCopy() {
		ComponentList copy = new ComponentList();
		copy.setComponents(copyComponents(components));
		return copy;
	}
	
	private List<Component> copyComponents(List<Component> source) {
		if (source == null) {
			return null;
		}
		List<Component> dest = new ArrayList<Component>(source.size());
		for (Component component : source) {
			dest.add((Component) component.createCopy());
		}
		return dest;
	}	

}
