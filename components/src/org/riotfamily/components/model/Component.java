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

import java.util.ListIterator;

import org.springframework.util.Assert;


public class Component extends ContentMap {

	private ComponentList list;
	
	private String type;
	
	public Component(ComponentList list) {
		super(list.getOwner());
		this.list = list;
	}
	
	public Component(ComponentList list, String id) {
		super(list.getOwner(), id);
		this.list = list;
	}

	public ComponentList getList() {
		return list;
	}

	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void delete() {
		Assert.isTrue(list.remove(this));
	}
	
	public void move(String before) {
		delete();
		if (before != null) {
			ListIterator<Component> it = list.listIterator();
			while (it.hasNext()) {
				if (it.next().getId().equals(before)) {
					it.add(this);
					break;
				}
			}
		}
		else {
			list.add(this);
		}
	}
	
	public static Component load(String id) {
		return (Component) Content.loadPart(id);
	}

	public int getPosition() {
		return list.indexOf(this);
	}

}
